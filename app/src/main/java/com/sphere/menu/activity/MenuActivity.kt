package com.sphere.menu.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R
import com.sphere.databinding.ActivityMenuBinding
import com.sphere.sphere.SphereListAdapter

private const val TAG = "MenuActivity"

class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel Code
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = SphereListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

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