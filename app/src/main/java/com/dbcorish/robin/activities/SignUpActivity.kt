package com.dbcorish.robin.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.dbcorish.robin.databinding.ActivitySignupBinding
import com.dbcorish.robin.util.User
import com.dbcorish.robin.util.users
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener { onSignup() }
        binding.passwordET.onDone { onSignup() }

        setTextChangeListener(binding.usernameET, binding.usernameTIL)
        setTextChangeListener(binding.emailET, binding.emailTIL)
        setTextChangeListener(binding.passwordET, binding.passwordTIL)
    }

    private fun onSignup() {
        var check = true
        if (binding.usernameET.text.isNullOrEmpty()) {
            binding.usernameTIL.error = "Username is required"
            binding.usernameTIL.isErrorEnabled = true
            check = false
        }
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
            binding.signUpProgressLayout.visibility = View.VISIBLE
            val email = binding.emailET.text.toString()
            firebaseAuth.createUserWithEmailAndPassword(email, binding.passwordET.text.toString())
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Account creation error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val username = binding.usernameET.text.toString()
                        val user = User(email, username, "", arrayListOf(), arrayListOf())
                        firebaseDB.collection(users).document(firebaseAuth.uid!!).set(user)
                        startActivity(MainActivity_Old.newIntent(this))
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

    companion object {
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}