package com.growingio.demo.util

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.widget.TextView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.qualifiers.ApplicationContext
import io.noties.markwon.Markwon
import io.noties.markwon.PrecomputedTextSetterCompat
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * <p>
 *
 * @author cpacm 2023/2/28
 */
@Singleton
class MarkwonManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        const val DEFAULT_MARKWON_TAG = "DEFAULT"
    }

    private val cache = Collections.synchronizedMap(WeakHashMap<String, Markwon>())

    fun renderMarkdown(tv: TextView, markdown: String) {
        getDefaultMarkwon().setMarkdown(tv, markdown)
    }

    private fun getDefaultMarkwon(): Markwon {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.4)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("moment_image_cache"))
                    .maxSizePercent(0.1)
                    .build()
            }
            .apply {
                crossfade(true)
            }.build()

        return cache[DEFAULT_MARKWON_TAG] ?: Markwon.builder(context)
            .textSetter(PrecomputedTextSetterCompat.create(Executors.newCachedThreadPool()))
            .usePlugin(CoilImagesPlugin.create(context, imageLoader))
            .usePlugin(TablePlugin.create(context))
            .build()
            .also {
                cache[DEFAULT_MARKWON_TAG] = it
            }
    }
}
