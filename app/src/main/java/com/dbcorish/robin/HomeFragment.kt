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
import com.dbcorish.robin.homeFragments.MessagesFragment
import com.dbcorish.robin.homeFragments.NewsFragment
import com.dbcorish.robin.homeFragments.NotificationsFragment
import com.dbcorish.robin.homeFragments.SearchFragment
import com.dbcorish.robin.util.User
import com.dbcorish.robin.util.loadURL
import com.dbcorish.robin.util.users
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val firebaseDB = Firebase.firestore
    private val userID = Firebase.auth.currentUser?.uid
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()

        if (userID == null) {
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
            downloadProfileImage()
        }

        binding.profile.setOnClickListener {
            Navigation.findNavController(v).navigate(R.id.navigateFromHomeToProfile)
        }

        binding.tweetButton.setOnClickListener {
            val userID = userID.toString()
            val action = HomeFragmentDirections.navigateFromHomeToTweet(userID, user?.username.toString())
            Navigation.findNavController(v).navigate(action)
        }
    }

    // Updates profile image in top-left corner
    private fun downloadProfileImage() {
        firebaseDB.collection(users).document(userID ?: return).get()
            .addOnSuccessListener { documentSnapshot ->
                user = documentSnapshot.toObject(User::class.java)
                user?.imageURL.let {
                    binding.profileImage.loadURL(it, R.drawable.default_user)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // For swiping between different tabs
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