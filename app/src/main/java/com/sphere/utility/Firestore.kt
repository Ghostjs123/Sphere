package com.sphere.utility

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "Firestore"


// create/update
fun addSphereToFirestore(context: Context, sphereName: String, seed: Long?, subdivision: Int) {
    FirebaseApp.initializeApp(context)
    val db = FirebaseFirestore.getInstance()

    db.collection("Spheres").document(sphereName)
        .set(hashMapOf(
            "seed" to seed,
            "subdivision" to subdivision
        ))
        .addOnSuccessListener { Toast.makeText(context, "Exported $sphereName", Toast.LENGTH_SHORT).show() }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Exporting $sphereName failed, check your network connection and try again",
                Toast.LENGTH_LONG
            ).show()
            Log.w(TAG, "Error during Firestore.set()", e)
        }
}

// read
fun readSphereFromFirestore(
    context: Context,
    sphereName: String,
    callback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
) {
    FirebaseApp.initializeApp(context)
    val db = FirebaseFirestore.getInstance()

    db.collection("Spheres").document(sphereName).get()
        .addOnSuccessListener { doc ->
            if (doc.exists()) {
                callback(sphereName, doc.get("seed") as? Long, (doc.get("subdivision") as Long).toInt())

                Toast.makeText(
                    context,
                    "Imported $sphereName",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Could not find sphere $sphereName to import",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Reading $sphereName failed, check your network connection and try again",
                Toast.LENGTH_LONG
            ).show()
            Log.w(TAG, "Error during Firestore.get()", e)
        }
}

// delete
fun removeSphereFromFirestore(context: Context, sphereName: String) {
    FirebaseApp.initializeApp(context)
    val db = FirebaseFirestore.getInstance()

    db.collection("Spheres").document(sphereName).delete()
        .addOnSuccessListener { Toast.makeText(context, "Deleted $sphereName", Toast.LENGTH_SHORT).show() }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Deleting $sphereName failed, check your network connection and try again",
                Toast.LENGTH_LONG
            ).show()
            Log.w(TAG, "Error during Firestore.delete()", e)
        }
}



