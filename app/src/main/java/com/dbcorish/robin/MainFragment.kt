package com.dbcorish.robin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dbcorish.robin.databinding.ActivityMainBinding
import com.dbcorish.robin.databinding.FragmentLoginBinding
import com.dbcorish.robin.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = activity?.let { ViewPagerAdapter(it.supportFragmentManager, lifecycle) }
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.selector_home)
                1 -> tab.setIcon(R.drawable.selector_search)
                2 -> tab.setIcon(R.drawable.selector_notifications)
                else -> tab.setIcon(R.drawable.selector_messages)
            }
        }.attach()
    }

    private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        private val NUM_TABS = 4

        override fun getItemCount(): Int {
            return NUM_TABS
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> SearchFragment()
                2 -> NotificationsFragment()
                else -> MessagesFragment()
            }
        }
    }
}