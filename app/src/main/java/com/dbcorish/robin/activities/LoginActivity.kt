package com.dbcorish.robin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dbcorish.robin.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener { onLogin() }
        binding.signupTV.setOnClickListener { goToSignup() }

        setTextChangeListener(binding.emailET, binding.emailTIL)
        setTextChangeListener(binding.passwordET, binding.passwordTIL)
    }

    private fun onLogin() {
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
                            this@LoginActivity,
                            "Login error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (task.isSuccessful) {
                        binding.loginProgressLayout.visibility = View.GONE
                        startActivity(HomeActivity.newIntent(this))
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    binding.loginProgressLayout.visibility = View.GONE
                }
        }
    }

    private fun goToSignup() {
        startActivity(SignUpActivity.newIntent(this))
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

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}