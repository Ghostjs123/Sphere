package com.sphere.menu.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sphere.R

class MySpheresFragment : Fragment() {

    companion object {
        fun newInstance() = MySpheresFragment()
    }

    private lateinit var viewModel: SphereViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_spheres, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SphereViewModel::class.java)
        // TODO: Use the ViewModel
    }

}