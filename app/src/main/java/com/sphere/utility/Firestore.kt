package com.sphere.utility

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.sphere.R

private const val TAG = "Firestore"


// create/update
fun addSphereToFirestore(
    db: FirebaseFirestore,
    context: Context,
    sphereName: String,
    seed: Long?,
    subdivision: Int
) {
    db.collection("Spheres").document(sphereName)
        .set(hashMapOf(
            "seed" to seed,
            "subdivision" to subdivision
        ))
        .addOnSuccessListener { Toast.makeText(context, context.getString(R.string.exported) + " $sphereName", Toast.LENGTH_SHORT).show() }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "$sphereName "+ context.getString(R.string.export_failure),
                Toast.LENGTH_LONG
            ).show()
            Log.w(TAG, "Error during Firestore.set()", e)
        }
}

// read
fun readSphereFromFirestore(
    db: FirebaseFirestore,
    context: Context,
    sphereName: String,
    callback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
) {
    db.collection("Spheres").document(sphereName).get()
        .addOnSuccessListener { doc ->
            if (doc.exists()) {
                callback(sphereName, doc.get("seed") as? Long, (doc.get("subdivision") as Long).toInt())

                Toast.makeText(
                    context,
                    context.getString(R.string.imported)+" $sphereName",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "'$sphereName' "+ context.getString(R.string.does_not_exist),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "$sphereName "+context.getString(R.string.read_failure),
                Toast.LENGTH_LONG
            ).show()
            Log.w(TAG, "Error during Firestore.get()", e)
        }
}

// delete
fun removeSphereFromFirestore(
    db: FirebaseFirestore,
    context: Context,
    sphereName: String
) {
    db.collection("Spheres").document(sphereName).delete()
        .addOnSuccessListener { Toast.makeText(context, context.getString(R.string.deleted)+" $sphereName", Toast.LENGTH_SHORT).show() }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "$sphereName "+context.getString(R.string.delete_failure),
                Toast.LENGTH_LONG
            ).show()
            Log.w(TAG, "Error during Firestore.delete()", e)
        }
}
