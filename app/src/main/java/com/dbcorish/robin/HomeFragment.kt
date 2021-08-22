package com.dbcorish.robin

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dbcorish.robin.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val iMm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        iMm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()

        val user = FirebaseAuth.getInstance().currentUser?.uid
        if (user == null) {
            Navigation.findNavController(v).navigate(R.id.navigateFromHomeToLogin)
        } else {
            val viewPager = binding.viewPager
            val tabLayout = binding.tabLayout

            val adapter = activity?.let { ViewPagerAdapter(childFragmentManager, lifecycle) }
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

        binding.profile.setOnClickListener {
            Navigation.findNavController(v).navigate(R.id.navigateFromHomeToProfile)
        }
    }

    private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        private val tabs = 4

        override fun getItemCount(): Int {
            return tabs
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> NewsFragment()
                1 -> SearchFragment()
                2 -> NotificationsFragment()
                else -> MessagesFragment()
            }
        }
    }
}