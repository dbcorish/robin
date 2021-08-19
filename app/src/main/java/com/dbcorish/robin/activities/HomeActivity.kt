package com.dbcorish.robin.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dbcorish.robin.R
import com.dbcorish.robin.databinding.ActivityHomeBinding
import com.dbcorish.robin.fragments.ActivityFragment
import com.dbcorish.robin.fragments.HomeFragment
import com.dbcorish.robin.fragments.MessagesFragment
import com.dbcorish.robin.fragments.SearchFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.selector_home)
                }
                1 -> {
                    tab.setIcon(R.drawable.selector_search)
                }
                2 -> {
                    tab.setIcon(R.drawable.selector_notifications)
                }
                3 -> {
                    tab.setIcon(R.drawable.selector_messages)
                }
            }
        }.attach()
    }

    fun onLogout() {
        firebaseAuth.signOut()
        startActivity(LoginActivity.newIntent(this))
        finish()
    }


    class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        private val numTabs = 4

        override fun getItemCount(): Int {
            return numTabs
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> SearchFragment()
                2 -> ActivityFragment()
                else -> MessagesFragment()
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}