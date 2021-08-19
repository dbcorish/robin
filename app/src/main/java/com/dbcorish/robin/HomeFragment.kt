package com.dbcorish.robin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dbcorish.robin.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : RobinFragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        binding.logoutButton.setOnClickListener {
            onLogout(v)
        }
    }

    private fun onLogout(v: View) {
        binding.signOutProgressLayout.visibility = View.VISIBLE
        FirebaseAuth.getInstance().signOut()
        Navigation.findNavController(v).navigate(R.id.navigateFromMainToLogin)
    }
}