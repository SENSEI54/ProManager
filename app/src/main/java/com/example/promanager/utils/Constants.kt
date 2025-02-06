package com.example.promanager.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.promanager.activities.MyProfileActivity


object Constants {
    const val Users:String="user"
    const val Boards:String="Boards"
    const val ID:String ="id"
    const val NAME:String="name"
    const val IMAGE:String="image"
    const val MOBILE:String="mobile"
    const val ASSIGNED_TO:String="assignedTo"
    const val Read_Storage_Permission_Code=1
    const val Pick_imagePermission_code=2
    const val DOCUMENT_ID:String="documentID"
    const val TASK_LIST:String="taskList"
    const val BOARD_DETAIL:String = "board_detail"
    const val EMAIL:String="Email"

    fun showImagePicker(activity: Activity){
        val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, Pick_imagePermission_code)
    }

    fun getFileExtension(activity:Activity,uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}