package com.dbcorish.robin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * An activity that inflates a layout that has a NavHostFragment.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}