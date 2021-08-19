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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        binding.buttonLogin.setOnClickListener {
            onLogin(v)
        }
        binding.signupTV.setOnClickListener {
            Navigation.findNavController(v).navigate(R.id.navigateToCreateAccountFragment)
        }

        setTextChangeListener(binding.emailET, binding.emailTIL)
        setTextChangeListener(binding.passwordET, binding.passwordTIL)

        binding.passwordET.onDone { onLogin(v)}
    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    private fun onLogin(v: View) {
        var check = true
        if (binding.emailET.text.isNullOrEmpty()) {
            binding.emailTIL.error = "Email is required"
            binding.emailTIL.isErrorEnabled = true
            check = false
        }
        if (binding.passwordET.text.isNullOrEmpty()) {
            binding.passwordTIL.error = "Password is required"
            binding.passwordTIL.isErrorEnabled = true
            check = false
        }
        if (check) {
            binding.loginProgressLayout.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(
                binding.emailET.text.toString(),
                binding.passwordET.text.toString()
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