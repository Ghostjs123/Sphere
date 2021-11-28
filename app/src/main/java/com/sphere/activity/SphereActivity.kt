package com.sphere.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.SphereViewModelFactory
import com.sphere.menu_fragments.NewSphereFragment
import com.sphere.menu_fragments.NoSphereFragment
import com.sphere.room_code.SphereApplication
import com.sphere.sphere_fragments.SphereFragment
import com.sphere.utility.getSelectedSpherePref

private const val TAG = "SphereActivity"


class SphereActivity : AppCompatActivity() {

    private val sphereViewModel: SphereViewModel by viewModels()

    private lateinit var mSphereFragment: SphereFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        Log.i(TAG, "onCreate() Started")

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        setContentView(R.layout.activity_sphere)

        val selected = getSelectedSpherePref(this)

        mSphereFragment = SphereFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.sphere_fragment_container, mSphereFragment)
            .commit()

        if (selected == "") {
            // Only way this is "" is if there are no spheres, can skip checking viewmodel
            addNoSphereFragment()
        }
        else {
            sphereViewModel.allSpheres.observeOnce(this, {
                if (sphereViewModel.loadSphere(selected!!)) {
                    mSphereFragment.updateSphere(
                        sphereViewModel.getName(),
                        sphereViewModel.getSeed(),
                        sphereViewModel.getSubdivisions()
                    )
                }
                else {
                    addNoSphereFragment()
                }
            })
        }

        Log.i(TAG, "onCreate() Finished")
    }

    private fun addNoSphereFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.sphere_menu_fragment_container, NoSphereFragment.newInstance(
                mSphereFragment.getUpdateSphereCallback()
            ))
            .addToBackStack(null)
            .commit()
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return SphereViewModelFactory((application as SphereApplication).repository)
    }

    fun getUpdateSphereCallback(): (sphereName: String, seed: Long?, subdivision: Int) -> Unit {
        return mSphereFragment.getUpdateSphereCallback()
    }

    fun getRenameSphereCallback(): (sphereName: String) -> Unit {
        return mSphereFragment.getRenameSphereCallback()
    }
}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}
