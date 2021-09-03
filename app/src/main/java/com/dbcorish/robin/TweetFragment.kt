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
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.dbcorish.robin.databinding.FragmentTweetBinding
import com.dbcorish.robin.util.Tweet
import com.dbcorish.robin.util.tweets
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class TweetFragment : Fragment() {

    private lateinit var binding: FragmentTweetBinding
    private val args: TweetFragmentArgs by navArgs()
    private val firebaseDB = Firebase.firestore
    private val firebaseStorage = Firebase.storage.reference
    private val imageURL: String? = null
    private var userID: String? = null
    private var userName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTweetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        userID = args.userID
        userName = args.userName

        binding.tweetEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.tweetEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding.tweetEditText.requestFocus()
        showKeyboard()

        binding.tweetEditText.onDone { sendTweet(v) }

        binding.fabPhoto.setOnClickListener {
            addImage()
        }

        binding.fabSend.setOnClickListener() {
            sendTweet(v)
        }
    }

    private fun addImage() {

    }

    private fun sendTweet(v: View) {
        binding.tweetProgressLayout.visibility = View.VISIBLE
        val text: String = binding.tweetEditText.text.toString()
        val hashtags: ArrayList<String> = getHashtags(text)

        val tweetID = firebaseDB.collection(tweets).document()
        val tweet = Tweet(
            tweetID.id,
            arrayListOf(userID!!),
            userName,
            text,
            imageURL,
            System.currentTimeMillis(),
            hashtags,
            arrayListOf()
        )
        tweetID.set(tweet)
            .addOnCompleteListener() {
                binding.tweetProgressLayout.visibility = View.GONE
                Navigation.findNavController(v).navigate(R.id.navigateFromTweetToHome)
            }
            .addOnFailureListener() {
                binding.tweetProgressLayout.visibility = View.GONE
                Toast.makeText(
                    this@TweetFragment.requireActivity(),
                    "Failed to send tweet",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getHashtags(source: String): ArrayList<String> {
        return arrayListOf()
    }

    private fun showKeyboard() {
        val inputMethodManager =
            (view?.context ?: return).getSystemService(Activity.INPUT_METHOD_SERVICE)
                    as InputMethodManager
        inputMethodManager.showSoftInput(binding.tweetEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
            }
            false
        }
    }
}