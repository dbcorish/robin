package com.dbcorish.robin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.dbcorish.robin.databinding.FragmentProfileBinding
import com.dbcorish.robin.util.User
import com.dbcorish.robin.util.user_email
import com.dbcorish.robin.util.user_username
import com.dbcorish.robin.util.users
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        binding.updateUsernameButton.setOnClickListener {
            onUpdateUserName()
        }
        binding.updateEmailButton.setOnClickListener {
            onUpdateEmail()
        }
        binding.signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Navigation.findNavController(v).navigate(R.id.navigateFromProfileToLogin)
        }

        setTextChangeListener(binding.userNameEditText, binding.userNameTextInputLayout)
        setTextChangeListener(binding.emailEditText, binding.emailTextInputLayout)

        downloadInfo()
    }

    fun onUpdateUserName() {
        var check = true
        if (binding.userNameEditText.text.isNullOrEmpty()) {
            binding.userNameTextInputLayout.error = "Username is required"
            binding.userNameTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (check) {
            binding.profileProgressLayout.visibility = View.VISIBLE
            val map = HashMap<String, Any>()
            map[user_username] = binding.userNameEditText.text.toString()
            firebaseDB.collection(users).document(user!!).update(map).addOnSuccessListener {
                Toast.makeText(
                    this@ProfileFragment.requireActivity(),
                    "Updated username successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(
                    this@ProfileFragment.requireActivity(),
                    "Update failed. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.profileProgressLayout.visibility = View.GONE
        }
    }

    fun onUpdateEmail() {
        var check = true
        if (binding.emailEditText.text.isNullOrEmpty()) {
            binding.emailTextInputLayout.error = "Email is required"
            binding.emailTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (check) {
            binding.profileProgressLayout.visibility = View.VISIBLE
            val map = HashMap<String, Any>()
            map[user_email] = binding.emailEditText.text.toString()
            firebaseDB.collection(users).document(user ?: return).update(map).addOnSuccessListener {
                Toast.makeText(
                    this@ProfileFragment.requireActivity(),
                    "Updated email successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(
                    this@ProfileFragment.requireActivity(),
                    "Update failed. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.profileProgressLayout.visibility = View.GONE
        }
    }


    fun downloadInfo() {
        binding.profileProgressLayout.visibility = View.VISIBLE
        firebaseDB.collection(users).document(user ?: return).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                binding.userNameEditText.setText(user?.username, TextView.BufferType.EDITABLE)
                binding.emailEditText.setText(user?.email, TextView.BufferType.EDITABLE)
                binding.profileProgressLayout.visibility = View.GONE
            }.addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun setTextChangeListener(et: EditText, til: TextInputLayout) {
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
}