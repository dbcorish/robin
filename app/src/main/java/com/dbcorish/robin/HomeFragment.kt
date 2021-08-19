package com.dbcorish.robin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class HomeFragment : RobinFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}