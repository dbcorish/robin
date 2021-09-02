package com.dbcorish.robin

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
        val userName = args.userName

        binding.tweetText.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.tweetText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding.tweetText.requestFocus()
        showKeyboard()

        binding.fabPhoto.setOnClickListener {
            addImage()
        }

        binding.fabSend.setOnClickListener() {
            sendTweet()
        }
    }

    private fun addImage() {

    }

    private fun sendTweet() {

    }

    private fun showKeyboard() {
        val inputMethodManager =
            (view?.context ?: return).getSystemService(Activity.INPUT_METHOD_SERVICE)
                    as InputMethodManager
        inputMethodManager.showSoftInput(binding.tweetText, InputMethodManager.SHOW_IMPLICIT)
    }
}