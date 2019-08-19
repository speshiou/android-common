package com.speshiou.android.common.ui.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

class MediaUtils {
    companion object {
        fun launchImagePicker(activity: Activity, requestCode: Int, prompt: String, allowMultiple: Boolean) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            if (allowMultiple) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            activity.startActivityForResult(Intent.createChooser(intent, prompt), requestCode)
        }

        fun launchImagePicker(fragment: Fragment, requestCode: Int, prompt: String, allowMultiple: Boolean) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            if (allowMultiple) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            fragment.startActivityForResult(Intent.createChooser(intent, prompt), requestCode)
        }

        fun handleImagePickerResult(data: Intent?): Array<Uri> {
            val uri = data?.data
            var uris = arrayListOf<Uri>()
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val item = clipData.getItemAt(i)
                    val uri = item.uri
                    uris.add(uri)
                }
            } else if (uri != null) {
                uris.add(uri)
            }
            return uris.sortedBy { it.toString() }.toTypedArray()
        }
    }
}