package com.example.josh.android.camera

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun saveImageToGallery(context: Context, uri: Uri): String {
    return uri.toString()
}
