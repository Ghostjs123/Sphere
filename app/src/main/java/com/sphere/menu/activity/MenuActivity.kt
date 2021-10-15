package com.sphere.menu.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sphere.R
import com.sphere.databinding.ActivityMenuBinding
import com.sphere.menu.fragments.*
import com.sphere.sphere.SphereViewModel
import com.sphere.sphere.SphereViewModelFactory
import com.sphere.sphere.room_code.SphereApplication
import com.sphere.sphere.room_code.SphereDatabase
import kotlinx.coroutines.CoroutineScope

private const val TAG = "MenuActivity"

class MenuActivity : AppCompatActivity() {

    private val sphereViewModel: SphereViewModel by viewModels {
        SphereViewModelFactory((application as SphereApplication).repository)
    }

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        setContentView(R.layout.activity_menu)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        handleIntent()

        Log.i(TAG, "onCreate() Finished")
    }

    private fun handleIntent() {
        when (val intentAction = intent.getStringExtra("ACTION")) {
            "MySpheres" -> {
                supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MySpheresFragment())
                .addToBackStack(null)
                .commit()
            }
            "ImportSphere" -> {
                supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ImportSphereFragment())
                .addToBackStack(null)
                .commit()
            }
            "NewSphere" -> {
                supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NewSphereFragment())
                .addToBackStack(null)
                .commit()
            }
            "SettingsMenu" -> {
                supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsMenuFragment())
                .addToBackStack(null)
                .commit()
            }
            else -> {
                Log.i(TAG, "Un-handled Intent ACTION: $intentAction, launching NoSphereFragment")

                supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NoSphereFragment())
                .addToBackStack(null)
                .commit()
            }
        }
    }
}