package com.jomeva.driving.activities.providers

import android.content.Context
import android.net.Uri
import android.util.Log
import com.jomeva.driving.activities.util.CompressorBitmapImage
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File






class ImageProvider {
    lateinit var mContext: Context
    var mstorage: StorageReference
    var mUrl: Uri? = null
    var index: Int = 0
    lateinit var messagesProvider: MessagesProvider


    constructor() {
        mstorage = FirebaseStorage.getInstance().reference
        messagesProvider = MessagesProvider()

    }

    public fun save(context: Context, file: File, name: String): UploadTask {

        val imageByte: ByteArray =
            CompressorBitmapImage.getImage(context, file.toString(), 500, 500)

        val storage: StorageReference =
            FirebaseStorage.getInstance().reference.child("images/").child("$name")
        val task: UploadTask = storage.putBytes(imageByte)
        this.mstorage = storage
        return task
    }
    public fun saveAudio(context: Context,  path: String,name:String): UploadTask {

val uri=Uri.fromFile(File(path))
        val storage: StorageReference =
            FirebaseStorage.getInstance().reference.child("audioRecord/").child("$name")
        val task: UploadTask = storage.putFile(uri)
        this.mstorage = storage
        return task
    }




    public fun deleteFile(name:String): Task<Void> {
        Log.d("borandoFile2","${ name}")
        return mstorage.child("audioRecord/$name").delete()
    }

 public fun getStorage(): StorageReference {
     return mstorage
 }

}