package com.rubenmackin.firestorage.data

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StoregeService @Inject constructor(private val storage: FirebaseStorage) {
    fun basicExample() {

        val reference = storage.reference.child("ejemplo/test.png")
        reference.name // test.png
        reference.path // ejemplo/test.png
        reference.bucket // gs://firestorage-e2afb.appspot.com
    }

    fun uploadBasicImage(uri: Uri) {
        val reference = storage.reference.child(uri.lastPathSegment.orEmpty())
        reference.putFile(uri)
    }

    suspend fun downloadBasicImage(): Uri {
//        val reference = storage.reference.child("$userId/profile.png")
        val reference = storage.reference.child("image:32")

        return reference.downloadUrl.await()
    }

    suspend fun uploadAndDownloadImage(uri: Uri): Uri {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val reference = storage.reference.child("download/${uri.lastPathSegment}")
            reference.putFile(uri, createMetaData()).addOnSuccessListener {
                downloadImagce(it, cancellableContinuation)
            }.addOnFailureListener {
                cancellableContinuation.resumeWithException(it)
            }
        }
//        removeImage()
//        return Uri.EMPTY
    }

    private fun downloadImagce(
        uploadTask: UploadTask.TaskSnapshot,
        cancellableContinuation: CancellableContinuation<Uri>
    ) {
        uploadTask.storage.downloadUrl
            .addOnSuccessListener { uri ->
                cancellableContinuation.resume(uri)
            }
            .addOnFailureListener {
                cancellableContinuation.resumeWithException(it)
            }
    }

    private suspend fun readMetadataBasic() {
        //Debe existir la referencia
        val reference = storage.reference.child("download/metadata1495372959460338420.jpg")
        val response = reference.metadata.await()
        val metainfo = response.getCustomMetadata("author")
        Log.i("author", metainfo.orEmpty())
    }

    private suspend fun readMetadataAdvance() {
        val reference = storage.reference.child("download/metadata1495372959460338420.jpg")
        val response = reference.metadata.await()

        response.customMetadataKeys.forEach { key ->
            response.getCustomMetadata(key)?.let { value ->
                Log.i("metadata", "para la key: $key es el valor: $value")
            }
        }
    }

    private fun removeImage(): Boolean {
        val reference = storage.reference.child("download/image:33")
        return reference.delete().isSuccessful
    }

    private fun createMetaData(): StorageMetadata {
        val metadata = storageMetadata {
            contentType = "image/jpeg"
            setCustomMetadata("date", "12-01-1993")
            setCustomMetadata("author", "ruben")
        }
        return metadata
    }

    private fun uploadImageWithProgress(uri: Uri) {
        val reference = storage.reference.child("download/miImage.png")
        reference.putFile(uri).addOnProgressListener { uploadTask ->
            val progress = (100.0 * uploadTask.bytesTransferred) / uploadTask.totalByteCount
            Log.i("upload", "progress: $progress")
        }
    }

    //LISTA
    suspend fun getAllImages(): List<Uri>{
        val reference = storage.reference.child("download/")
//        reference.listAll().addOnSuccessListener {
//            it.items.forEach { item ->
//                Log.i("download", "item: ${item.name}")
//            }
//        }

        return reference.listAll().await().items.map { it.downloadUrl.await() }
    }
}