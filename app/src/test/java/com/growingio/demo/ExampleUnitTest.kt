package com.growingio.demo

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun reg() {
        val text = "level:debug cpacm"

        val pattern = """^(level:(verbose|debug|info|warn|error))($|( (.*))+)"""

        val regex = Regex(pattern)

        val matchResult = regex.find(text)

        if (matchResult != null) {
            matchResult.groups.forEach {
                println("group:" + it?.value + "${it?.range}")
            }
        } else {
            println("No matching found.")
        }
    }
}