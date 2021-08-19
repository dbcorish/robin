package com.dbcorish.robin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.dbcorish.robin.activities.MainActivity_Old
import com.dbcorish.robin.databinding.CreateAccountFragmentBinding
import com.dbcorish.robin.util.User
import com.dbcorish.robin.util.users
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountFragment : Fragment() {

    private lateinit var binding: CreateAccountFragmentBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CreateAccountFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        binding.createAccountButton.setOnClickListener { onSignup(v) }
        binding.createPasswordEditText.onDone { onSignup(v) }

        setTextChangeListener(binding.createUsernameEditText, binding.createUsernameTextInputLayout)
        setTextChangeListener(binding.createEmailEditText, binding.createEmailTextInputLayout)
        setTextChangeListener(binding.createPasswordEditText, binding.createPasswordTextInputLayout)
    }

    private fun onSignup(v: View) {
        var check = true
        if (binding.createUsernameEditText.text.isNullOrEmpty()) {
            binding.createUsernameTextInputLayout.error = "Username is required"
            binding.createUsernameTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (binding.createEmailEditText.text.isNullOrEmpty()) {
            binding.createEmailTextInputLayout.error = "Email is required"
            binding.createEmailTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (binding.createPasswordEditText.text.isNullOrEmpty()) {
            binding.createPasswordTextInputLayout.error = "Password is required"
            binding.createPasswordTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (check) {
            binding.signUpProgressLayout.visibility = View.VISIBLE
            val email = binding.createEmailEditText.text.toString()
            firebaseAuth.createUserWithEmailAndPassword(email, binding.createPasswordEditText.text.toString())
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this@CreateAccountFragment.requireActivity(),
                            "Account creation error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val username = binding.createUsernameEditText.text.toString()
                        val user = User(email, username, "", arrayListOf(), arrayListOf())
                        firebaseDB.collection(users).document(firebaseAuth.uid!!).set(user)
                        v.findNavController().navigate(R.id.navigateToLoginFragment)
                    }
                    binding.signUpProgressLayout.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    binding.signUpProgressLayout.visibility = View.GONE
                }
        }
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

    fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
                true
            }
            false
        }
    }
}