package com.growingio.demo

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FragmentTest {

    @Test
    fun testFragment() {
        val a = AFragment()
        a.onCreate()
        val b = BFragment()
        b.onCreate()
        println(fragmentStore)
    }

    companion object {
        val fragmentStore = mutableSetOf<BaseFragment>()
    }

    open class BaseFragment {
        open fun onCreate() {
            println("<BaseFragment>:$this")
            fragmentStore.add(this)
        }
    }

    class AFragment : BaseFragment() {
        override fun onCreate() {
            println("<AFragment>:$this")
            fragmentStore.add(this)
            super.onCreate()
        }
    }

    class BFragment : BaseFragment() {
        override fun onCreate() {
            println("<BFragment>:$this")
            fragmentStore.add(this)
            super.onCreate()
        }
    }
}
