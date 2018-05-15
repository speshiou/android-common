package com.speshiou.android.common.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager

/**
 * Created by joey on 2018/1/11.
 */
open class BaseActivity: AppCompatActivity() {
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun launchImagePicker(requestCode: Int, prompt: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, prompt), requestCode)
    }

    fun handleImagePickerResult(data: Intent?): Array<Uri> {
        val uri = data?.data
        var uris = arrayListOf<Uri>()
        if (uri == null) {
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val item = clipData.getItemAt(i)
                    val uri = item.uri
                    uris.add(uri)
                }
            }
        } else {
            uris.add(uri)
        }
        return uris.toTypedArray()
    }

    fun hideSoftInput() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}