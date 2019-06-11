package com.speshiou.android.common.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.opengl.ETC1.getHeight
import android.util.TypedValue
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewGroup
import com.speshiou.android.common.ui.utils.ViewUtils
import com.speshiou.android.common.ui.widget.OnKeyboardVisibilityListener



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

    fun launchImagePicker(requestCode: Int, prompt: String, allowMultiple: Boolean) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (allowMultiple) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
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

    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            ViewUtils.showSoftKeyboard(view)
        }
    }

    fun hideSoftInput() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            private var alreadyOpen: Boolean = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP = defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
            private val rect = Rect()

            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP.toFloat(), parentView.resources.displayMetrics).toInt()
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff = parentView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight

                if (isShown == alreadyOpen) {
                    return
                }
                alreadyOpen = isShown
                onKeyboardVisibilityListener.onKeyboardVisibilityChanged(isShown)
            }
        })
    }
}