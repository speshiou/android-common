package com.speshiou.android.common

import com.speshiou.android.common.security.SecurityUtils
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SecurityUtilsUnitTest {
    @Test
    fun md5_isCorrect() {
        assertEquals("3dfd6114db52c8e6130ba6642a7a2257", SecurityUtils.md5("android common library"))
    }
}
