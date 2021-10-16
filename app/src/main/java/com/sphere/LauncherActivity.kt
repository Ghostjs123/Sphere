package com.sphere

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sphere.menu.activity.MenuActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: check our ViewModel if there is a selected sphere.
        // If so, navigate to SphereActivity.
        // Else, navigate to MenuActivity - NoSphereFragment.

        startActivity(
            Intent(this, MenuActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
        )
        finish()  // removes this Activity from the backstack
    }
}