package com.growingio.demo

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.startup.Initializer
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.growingio.android.sdk.autotrack.CdpAutotrackConfiguration
import com.growingio.android.sdk.autotrack.CdpAutotracker
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.EventFilterInterceptor
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.data.settingsDataStore
import com.growingio.demo.util.enableStrictMode
import com.growingio.giokit.GioKit
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * <p>
 *
 * @author cpacm 2023/4/11
 */
@HiltAndroidApp
class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        enableStrictMode()
    }
}

class GiokitInitializer : Initializer<Boolean> {
    override fun create(context: Context): Boolean {
        if (context is Application) {
            GioKit.with(context)
                .attach(false).bindWindow(false)
                .build()
            return true
        }
        return false
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(GrowingioInitializer::class.java)
    }

}

/**
 * dagger with {@link com.growingio.demo.util.GrowingIOManager}
 */
class GrowingioInitializer : Initializer<CdpAutotracker> {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GrowingIOProvider {
        fun eventFilterInterceptor(): EventFilterInterceptor
    }

    @SourceCode
    override fun create(context: Context): CdpAutotracker {
        val growingIOProvider = EntryPoints.get(context, GrowingIOProvider::class.java)
        val agreePolicy = runBlocking { context.settingsDataStore.data.first().agreePolicy }
        GrowingAutotracker.startWithConfiguration(
            context,
            CdpAutotrackConfiguration("bc675c65b3b0290e", "growing.55ea7f7853827722")
                .setDataSourceId("9fe6e7c0ceffe22c")
                .setDataCollectionServerHost("http://117.50.84.75:8080")
                .setChannel("demo")
                .setDebugEnabled(BuildConfig.DEBUG)
                .setDataCollectionEnabled(agreePolicy)
                .setCellularDataLimit(10)
                .setDataUploadInterval(15)
                .setSessionInterval(30)
                .setEventFilterInterceptor(growingIOProvider.eventFilterInterceptor())
                .setIdMappingEnabled(true)
                .setImpressionScale(0f)
        )

        return GrowingAutotracker.get()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}

class CoilInitializer : Initializer<Coil> {
    override fun create(context: Context): Coil {
        val coilOkHttpClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "coil_cache"), 50L * 1024 * 1024))
            .connectionPool(ConnectionPool(10, 2, TimeUnit.MINUTES))
            .build()
        Coil.setImageLoader {
            ImageLoader.Builder(context)
                .memoryCache {
                    MemoryCache.Builder(context)
                        .maxSizePercent(0.4)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve("demo_image_cache"))
                        .maxSizePercent(0.1)
                        .build()
                }
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                    add(SvgDecoder.Factory())
                }
                .okHttpClient(coilOkHttpClient)
                .build()
        }
        return Coil
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}