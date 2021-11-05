package com.sphere.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.SphereViewModelFactory
import com.sphere.menu_fragments.NewSphereFragment
import com.sphere.menu_fragments.NoSphereFragment
import com.sphere.sphere_fragments.SphereFragment
import com.sphere.room_code.SphereApplication
import com.sphere.room_code.SphereListAdapter
import com.sphere.utility.getSelectedSpherePref
import com.sphere.utility.setSelectedSpherePref

private const val TAG = "SphereActivity"


class SphereActivity : AppCompatActivity() {

    private val sphereViewModel: SphereViewModel by viewModels {
        SphereViewModelFactory((application as SphereApplication).repository)
    }

    private lateinit var sphereFragment: SphereFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        setContentView(R.layout.activity_sphere)

        Log.i(TAG, "onCreate() Finished")
    }

    override fun onStart() {
        super.onStart()

        val selected = getSelectedSpherePref(this)
        sphereViewModel.loadSphere(selected!!)

        sphereFragment = SphereFragment(selected!!)
        supportFragmentManager.beginTransaction()
            .replace(R.id.sphere_fragment_container, sphereFragment)
            .commit()

        if (selected == "") {
            supportFragmentManager.beginTransaction()
                .add(R.id.sphere_fragment_container, NoSphereFragment(
                    sphereFragment.getUpdateSphereCallback()
                ))
                .commit()
        }
    }

    fun onRadioButtonClicked(view: View) {
        val f = supportFragmentManager.findFragmentById(R.id.sphere_fragment_container)

        if (f is NewSphereFragment) f.onRadioButtonClicked(view)
        else Log.w(TAG, "onRadioButtonClicked() occurred on a fragment that was not a NewSphereFragment")
    }

    fun addNewSphereToViewModel(sphereName: String, seed: Long?, subdivisions: Int) {
        setSelectedSpherePref(this, sphereName)
        sphereViewModel.addSphere(sphereName, seed, subdivisions)
    }
}