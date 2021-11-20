package com.sphere.utility

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Test
import com.google.firebase.firestore.FirebaseFirestore
import com.sphere.room_code.Sphere
import com.sphere.sphere_fragments.SphereFragment
import org.junit.Before
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*


class TestFirestore {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Mock
    private val mockDB = mock(FirebaseFirestore::class.java)

    @Mock
    private val mockSpheresCollection = mock(CollectionReference::class.java)

    @Mock
    private val mockSphereDocument = mock(DocumentReference::class.java)

    @Before
    fun setupMockDB() {
        `when`(mockDB.collection(anyString())).thenReturn(mockSpheresCollection)
        `when`(mockSpheresCollection.document(anyString())).thenReturn(mockSphereDocument)
    }

    @Test
    fun addSphereToFirestore_HappyPath() {
        val sphereName = "asd"
        val seed = 100L
        val subdivision = 5

        val mockTask = mock(Task::class.java)
        `when`(mockSphereDocument.set(anyMap<String, Int>())).thenReturn(mockTask as Task<Void>?)
        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

        addSphereToFirestore(mockDB, context, sphereName, 100, 5)

        verify(mockSphereDocument).set(hashMapOf(
            "seed" to seed,
            "subdivision" to subdivision
        ))
    }
}