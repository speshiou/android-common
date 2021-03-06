package com.speshiou.android.common.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.speshiou.android.common.R
import com.speshiou.android.common.ui.utils.MediaUtils
import com.speshiou.android.common.ui.utils.ViewUtils
import com.speshiou.android.common.ui.widget.OnKeyboardVisibilityListener
import java.io.File
import java.io.IOException


/**
 * Created by joey on 2018/1/11.
 */
open class BaseActivity: AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onResume() {
        super.onResume()
        BaseApplication.firebaseAnalytics?.setCurrentScreen(this, this@BaseActivity::class.java.simpleName, null)
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        // workaround for font scale issue in Android 7
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun launchCameraCapture(requestCode: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                            this,
                            "$packageName.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, requestCode)
                }
            }
        }
    }

    private var capturedPhotoFileName: File? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_${System.currentTimeMillis()}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            capturedPhotoFileName = this
        }
    }

    protected fun getCapturedImageFile(): File? {
        return capturedPhotoFileName
    }

    fun launchImagePicker(requestCode: Int, prompt: String, allowMultiple: Boolean) {
        MediaUtils.launchImagePicker(this, requestCode, prompt, allowMultiple)
    }

    fun handleImagePickerResult(data: Intent?): Array<Uri> {
        return MediaUtils.handleImagePickerResult(data)
    }

    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            ViewUtils.showSoftKeyboard(view)
        }
    }

    fun hideSoftInput() {
        val view = currentFocus ?: findViewById<View>(android.R.id.content).getRootView()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
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

    fun setKeyboardSizeChangedListener(onKeyboardVisibilityListener: (size: Rect) -> Unit) {
        val rootWindow = window
        rootWindow?.getDecorView()?.findViewById<View>(android.R.id.content)?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            rootWindow?.getDecorView()?.getWindowVisibleDisplayFrame(r)
            onKeyboardVisibilityListener.invoke(r)
        }
    }
}