package com.sphere.utility

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "Firestore"


fun addSphereToFirestore(context: Context, sphereName: String, seed: Long) {
    FirebaseApp.initializeApp(context)
    val db = FirebaseFirestore.getInstance()

    db.collection("Spheres").document(sphereName)
        .set(hashMapOf(
            "seed" to seed
        ))
        .addOnSuccessListener { Toast.makeText(context, "Exported $sphereName", Toast.LENGTH_SHORT).show() }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Exporting $sphereName failed, check your network connection and try again.",
                Toast.LENGTH_LONG
            ).show()
            Log.w(TAG, "Error during Firestore.set()", e)
        }
}



