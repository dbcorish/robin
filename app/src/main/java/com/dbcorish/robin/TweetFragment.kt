package com.dbcorish.robin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.dbcorish.robin.databinding.FragmentTweetBinding

class TweetFragment : Fragment() {

    private lateinit var binding: FragmentTweetBinding
    private val args: TweetFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTweetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val userID = args.userID

        binding.textView.text = "My user ID is $userID"
    }
}