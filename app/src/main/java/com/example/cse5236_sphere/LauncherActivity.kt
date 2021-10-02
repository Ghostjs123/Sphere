package com.example.cse5236_sphere

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cse5236_sphere.menu.activity.MenuActivity

class LauncherActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: check our ViewModel if there is a selected sphere.
        // If so, navigate to SphereActivity.
        // Else, navigate to MenuActivity - NoSphereFragment.

        startActivity(
            Intent(this, MenuActivity::class.java)
        );
    }
}