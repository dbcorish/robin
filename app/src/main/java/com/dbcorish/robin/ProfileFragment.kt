package com.dbcorish.robin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.dbcorish.robin.databinding.FragmentProfileBinding
import com.dbcorish.robin.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val userID = auth.currentUser?.uid
    private var email = ""
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private var imageURL: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        binding.userNameEditText.onDone { onUpdateUserName() }
        binding.updateUsernameButton.setOnClickListener {
            hideKeyboard()
            onUpdateUserName()
        }
        binding.emailEditText.onDone {
            onUpdateEmail()
        }
        binding.updateEmailButton.setOnClickListener {
            hideKeyboard()
            onUpdateEmail()
        }
        binding.signOutButton.setOnClickListener {
            binding.profileProgressLayout.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.profileProgressLayout.visibility = View.GONE
                FirebaseAuth.getInstance().signOut()
                Navigation.findNavController(v).navigate(R.id.navigateFromProfileToLogin)
            }, 300)
        }

        binding.profilePhotoImage.setOnClickListener() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, photo_request_code)
        }

        setTextChangeListener(binding.userNameEditText, binding.userNameTextInputLayout)
        setTextChangeListener(binding.emailEditText, binding.emailTextInputLayout)

        downloadInfo()
    }

    private fun downloadInfo() {
        binding.profileProgressLayout.visibility = View.VISIBLE
        firebaseDB.collection(users).document(userID ?: return).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                binding.userNameEditText.setText(user?.username, TextView.BufferType.EDITABLE)
                binding.emailEditText.setText(user?.email, TextView.BufferType.EDITABLE)
                imageURL.let {
                    binding.profilePhotoImage.loadURL(user?.imageURL, R.drawable.default_user)
                }
                email = (user?.email.toString())
                binding.profileProgressLayout.visibility = View.GONE
            }.addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun onUpdateUserName() {
        var check = true
        if (binding.userNameEditText.text.isNullOrEmpty()) {
            binding.userNameTextInputLayout.error = "Username is required"
            binding.userNameTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (check) {
            binding.userNameEditText.clearFocus()
            binding.profileProgressLayout.visibility = View.VISIBLE
            val map = HashMap<String, Any>()
            map[user_username] = binding.userNameEditText.text.toString()
            firebaseDB.collection(users).document(userID ?: return).update(map)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@ProfileFragment.requireActivity(),
                            "Updated username successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ProfileFragment.requireActivity(),
                            "${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            Handler(Looper.getMainLooper()).postDelayed({
                binding.profileProgressLayout.visibility = View.GONE
            }, 300)
        }
    }

    private fun onUpdateEmail() {
        var check = true
        if (binding.emailEditText.text.isNullOrEmpty()) {
            binding.emailTextInputLayout.error = "Email is required"
            binding.emailTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (check) {
            binding.emailEditText.clearFocus()
            val input = EditText(this@ProfileFragment.requireActivity())
            input.hint = "Confirm your password"
            input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            input.transformationMethod = PasswordTransformationMethod()

            val dialogBuilder = MaterialAlertDialogBuilder(
                this@ProfileFragment.requireActivity(),
                R.style.Dialog
            )
            dialogBuilder.setView(input)
                .setNegativeButton(resources.getString(R.string.decline)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    val password = input.text.toString()
                    if (password != "") {
                        updateFirebaseEmail(password)
                    }
                }

            val dialog = dialogBuilder.create()
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            input.requestFocus()
            dialog.show()

            input.onDone {
                dialog.dismiss()
                if (input.text.toString() != "") {
                    updateFirebaseEmail(input.text.toString())
                }
            }
        }
    }

    private fun updateFirebaseEmail(password: String) {
        binding.profileProgressLayout.visibility = View.VISIBLE
        val credential = EmailAuthProvider
            .getCredential(email, password)

        user?.reauthenticate(credential)?.addOnCompleteListener { userAuthenticated ->
            if (userAuthenticated.isSuccessful) {
                user.updateEmail(
                    binding.emailEditText.text.toString()
                ).addOnCompleteListener { authenticationEmailUpdated ->
                    if (authenticationEmailUpdated.isSuccessful) {
                        val map = HashMap<String, Any>()
                        map[user_email] = binding.emailEditText.text.toString()
                        firebaseDB.collection(users)
                            .document(userID ?: return@addOnCompleteListener).update(map)
                            .addOnCompleteListener { emailUpdated ->
                                if (emailUpdated.isSuccessful) {
                                    Toast.makeText(
                                        this@ProfileFragment.requireActivity(),
                                        "Updated email successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@ProfileFragment.requireActivity(),
                                        "${emailUpdated.exception?.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this@ProfileFragment.requireActivity(),
                            "${authenticationEmailUpdated.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this@ProfileFragment.requireActivity(),
                    "${userAuthenticated.exception?.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        email = binding.emailEditText.text.toString()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.profileProgressLayout.visibility = View.GONE
        }, 700)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == photo_request_code) {
            storeImage(data?.data)
        }
    }

    fun storeImage(imageURI: Uri?) {
        imageURI?.let {
            Toast.makeText(
                this@ProfileFragment.requireActivity(),
                "Uploading...",
                Toast.LENGTH_SHORT
            ).show()
            binding.profileProgressLayout.visibility = View.VISIBLE
            val filepath = firebaseStorage.child(images).child(userID ?: return@let)
            filepath.putFile(imageURI)
                .addOnSuccessListener {
                    filepath.downloadUrl
                        .addOnSuccessListener { uri ->
                            val url = uri.toString()
                            firebaseDB.collection(users).document(userID)
                                .update(user_image_url, url)
                                .addOnSuccessListener {
                                    imageURL = url
                                    binding.profilePhotoImage.loadURL(
                                        imageURL,
                                        R.drawable.default_user
                                    )
                                    binding.profileProgressLayout.visibility = View.GONE
                                }
                        }
                        .addOnFailureListener() {
                            onUploadFailure()
                        }
                }.addOnFailureListener() {
                    onUploadFailure()
                }
        }
    }

    fun onUploadFailure() {
        Toast.makeText(
            this@ProfileFragment.requireActivity(),
            "Image upload failed",
            Toast.LENGTH_SHORT
        ).show()
        binding.profileProgressLayout.visibility = View.GONE
    }

    private fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                til.isErrorEnabled = false
            }

        })
    }

    private fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
            }
            false
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            (view?.context ?: return).getSystemService(Activity.INPUT_METHOD_SERVICE)
                    as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}