package com.growingio.demo

import com.google.protobuf.ByteString
import com.growingio.android.protobuf.EventV3Protocol
import org.junit.Test
import java.lang.StringBuilder

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

    @Test
    fun transferPb2Json() {
        val attrs = hashMapOf<String, String>()
        attrs.put("name", "cpacm")
        val dto: EventV3Protocol.EventV3Dto = EventV3Protocol.EventV3Dto.newBuilder()
            .setAndroidId("cpacm")
            .setScreenHeight(1000)
            .setLatitude(55.0)
            .setLongitude(100.0)
            .setEventType(EventV3Protocol.EventType.CUSTOM)
            .putAllAttributes(attrs)
            .build()
        dto.eventType.toString()
        dto.javaClass.methods.forEach {
            val name = it.name
            val jsonName = StringBuilder()
            val delName = it.name.removePrefix("get")
            delName.forEach {
                if (delName.indexOf(it) == 0) {
                    jsonName.append(it.lowercaseChar())
                } else {
                    jsonName.append(it)
                }
            }
            if (name.startsWith("get") &&
                it.returnType != ByteString::class.java &&
                it.parameterCount == 0
            ) {
                val value = it.invoke(dto)
                value?.apply {
                    when (it.returnType) {
                        String::class.java -> {
                            println("$jsonName:$this")
                        }

                        Int::class.java -> {
                            println("$jsonName:${this as Int}")
                        }

                        Long::class.java -> {
                            println("$jsonName:${this as Long}")
                        }

                        Double::class.java -> {
                            println("$jsonName:${this as Double}")
                        }

                        EventV3Protocol.EventType::class.java -> {
                            println("$jsonName:$this")
                        }

                        Map::class.java -> {
                            println("$jsonName:${this as Map<String, String>}")
                        }
                    }
                }
            }
        }
    }
}
