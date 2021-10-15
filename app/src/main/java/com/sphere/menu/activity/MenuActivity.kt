package com.sphere.menu.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sphere.R
import com.sphere.databinding.ActivityMenuBinding
import com.sphere.sphere.SphereViewModel
import com.sphere.sphere.SphereViewModelFactory
import com.sphere.sphere.room_code.SphereApplication
import com.sphere.sphere.room_code.SphereDatabase
import kotlinx.coroutines.CoroutineScope

private const val TAG = "MenuActivity"

class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    private val sphereViewModel: SphereViewModel by viewModels {
        SphereViewModelFactory((application as SphereApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.fragment_container)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        Log.i(TAG, "onCreate() Finished")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}