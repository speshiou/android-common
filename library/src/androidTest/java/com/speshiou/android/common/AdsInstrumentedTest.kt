package com.speshiou.android.common

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.speshiou.android.common.test.MockActivity
import com.speshiou.android.common.test.SimpleIdlingResource
import com.speshiou.android.common.ui.ads.AdTaskListener
import com.speshiou.android.common.ui.ads.AdType
import com.speshiou.android.common.ui.ads.AdViewRecycler
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AdsInstrumentedTest {

    private val ADMOB_TEST_NATIVE_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    private var _idlingResource: SimpleIdlingResource? = null

    @Before
    fun setup() {
        MockActivity.layout = R.layout.list_item_ad
        val scenario = ActivityScenario.launch(MockActivity::class.java)
        scenario.onActivity {
            _idlingResource = it.getIdlingResource()
            IdlingRegistry.getInstance().register(_idlingResource)
            val task = AdViewRecycler(it).obtainAdTask(AdType.AD_ADMOB_NATIVE, ADMOB_TEST_NATIVE_UNIT_ID, 30)
            task?.adContainer = it.findViewById(R.id.ad_container)
            task?.listener = object : AdTaskListener {
                override fun onAdLoaded() {
                    _idlingResource?.setIdleState(true)
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    _idlingResource?.setIdleState(true)
                }

            }
            task?.run()
            _idlingResource?.setIdleState(false)
        }
    }

    @Test
    fun testAdMobNativeAdTask() {
        // dummy click for getting IdlingResource to work
        onView(withId(R.id.ad_container)).perform(click())
        onView(withId(R.id.ad_container)).check(matches(hasDescendant(withId(R.id.title))))
    }

    @After
    fun release() {
        if (_idlingResource != null) {
            IdlingRegistry.getInstance().unregister(_idlingResource)
        }
    }
}
