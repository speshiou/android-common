package com.speshiou.android.common.test

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity

class MockActivity: AppCompatActivity() {

    private var _idlingResource: SimpleIdlingResource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
    }

    @VisibleForTesting
    fun getIdlingResource(): SimpleIdlingResource? {
        if (_idlingResource == null) {
            _idlingResource = SimpleIdlingResource()
        }
        return _idlingResource
    }

    companion object {
        var layout: Int = 0
    }
}