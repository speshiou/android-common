package com.speshiou.android.common.test

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback

class SimpleIdlingResource: IdlingResource {
    @Volatile
    private var _callback: ResourceCallback? = null

    // Idleness is controlled with this boolean.
    private var _isIdleNow = true

    override fun getName(): String {
        return this.javaClass.name
    }

    override fun isIdleNow(): Boolean {
        return _isIdleNow
    }

    override fun registerIdleTransitionCallback(callback: ResourceCallback?) {
        _callback = callback
    }

    /**
     * Sets the new idle state, if isIdleNow is true, it pings the [ResourceCallback].
     * @param isIdleNow false if there are pending operations, true if idle.
     */
    fun setIdleState(isIdleNow: Boolean) {
        _isIdleNow = isIdleNow
        if (isIdleNow) {
            _callback?.onTransitionToIdle()
        }
    }
}