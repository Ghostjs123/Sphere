package com.sphere

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.sphere.menu.activity.MenuActivity

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