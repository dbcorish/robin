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
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.dbcorish.robin.databinding.LoginFragmentBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        binding.loginButton.setOnClickListener {
            onLogin(v)
        }
        binding.createAccountTextView.setOnClickListener {
            Navigation.findNavController(v).navigate(R.id.navigateToCreateAccountFragment)
        }

        setTextChangeListener(binding.emailEditText, binding.emailTextInputLayout)
        setTextChangeListener(binding.passwordEditText, binding.passwordTextInputLayout)

        binding.passwordEditText.onDone { onLogin(v)}
    }

    private fun onLogin(v: View) {
        var check = true
        if (binding.emailEditText.text.isNullOrEmpty()) {
            binding.emailTextInputLayout.error = "Email is required"
            binding.emailTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (binding.passwordEditText.text.isNullOrEmpty()) {
            binding.passwordTextInputLayout.error = "Password is required"
            binding.passwordTextInputLayout.isErrorEnabled = true
            check = false
        }
        if (check) {
            binding.loginProgressLayout.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        binding.loginProgressLayout.visibility = View.GONE
                        Toast.makeText(
                            this@LoginFragment.requireActivity(),
                            "Login error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (task.isSuccessful) {
                        binding.loginProgressLayout.visibility = View.GONE
                        v.findNavController().navigate(R.id.navigateToCreateAccountFragment)
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    binding.loginProgressLayout.visibility = View.GONE
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