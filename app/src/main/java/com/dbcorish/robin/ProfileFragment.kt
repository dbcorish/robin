package com.dbcorish.robin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.dbcorish.robin.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        binding.applyButton.setOnClickListener {
            onApply(v)
        }
        binding.signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Navigation.findNavController(v).navigate(R.id.navigateFromProfileToLogin)
        }
    }

    fun onApply(v: View) {

    }
}