package com.dbcorish.robin.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dbcorish.robin.databinding.ActivityHomeBinding
import com.dbcorish.robin.fragments.ActivityFragment
import com.dbcorish.robin.fragments.HomeFragment
import com.dbcorish.robin.fragments.MessagesFragment
import com.dbcorish.robin.fragments.SearchFragment
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val activityFragment = ActivityFragment()
    private val messagesFragment = MessagesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onLogout() {
        firebaseAuth.signOut()
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    inner class SectionPageAdapter(fm: FragmentManager) : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount() = 3

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> homeFragment
                1 -> searchFragment
                2 -> activityFragment
                else -> messagesFragment
            }
        }

    }

    companion object {
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}