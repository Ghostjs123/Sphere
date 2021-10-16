package com.sphere.sphere.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sphere.R
import com.sphere.sphere.fragments.SphereFragment

private const val TAG = "SphereActivity"

class SphereActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        setContentView(R.layout.activity_sphere)

        val intentAction = intent.getStringExtra("ACTION")
        val sphereName = intent.getStringExtra("SPHERE_NAME")

        Log.i(TAG, "Received '$intentAction' Intent")

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.sphere_fragment_container,
                SphereFragment(intentAction!!, sphereName!!)
            )
            .addToBackStack(null)
            .commit()

        Log.i(TAG, "onCreate() Finished")
    }
}