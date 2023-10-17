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
import com.growingio.android.sdk.autotrack.AutotrackConfiguration
import com.growingio.android.sdk.autotrack.Autotracker
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.EventFilterInterceptor
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.data.settingsDataStore
import com.growingio.demo.util.GrowingIOManager
import com.growingio.demo.util.enableStrictMode
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

class GrowingioInitializer : Initializer<Autotracker> {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GrowingIOProvider {
        fun eventFilterInterceptor(): EventFilterInterceptor
    }

    @SourceCode
    override fun create(context: Context): Autotracker {
        //val growingIOProvider = EntryPoints.get(context, GrowingIOProvider::class.java)
        val growingIOProvider = GrowingIOManager.provideEventFilterInterceptor()
        val agreePolicy = runBlocking { context.settingsDataStore.data.first().agreePolicy }
        val autotrackConfiguration =
            AutotrackConfiguration(GROWINGIO_PROJECT_ID, GROWINGIO_URL_SCHEME)
                .setDataSourceId(GROWINGIO_DATASOURCE_ID)
                .setDataCollectionServerHost(GROWINGIO_SERVER_HOST)
                .setChannel("demo")
                .setDebugEnabled(BuildConfig.DEBUG)
                .setAndroidIdEnabled(true)
                .setRequireAppProcessesEnabled(true)
                .setDataCollectionEnabled(agreePolicy)
                .setCellularDataLimit(10)
                .setDataUploadInterval(15)
                .setSessionInterval(30)
                .setEventFilterInterceptor(growingIOProvider)
                .setIdMappingEnabled(true)
                .setImpressionScale(0f)
                //.downgrade()

        //it's demo logic
        configWithDataStore(context, autotrackConfiguration)
        GrowingAutotracker.startWithConfiguration(context, autotrackConfiguration)

        return GrowingAutotracker.get()
    }

    private fun configWithDataStore(context: Context, configuration: AutotrackConfiguration) {
        runBlocking {
            val projectId = context.settingsDataStore.data.first().projectId.default(GROWINGIO_PROJECT_ID)
            val urlScheme = context.settingsDataStore.data.first().urlScheme.default(GROWINGIO_URL_SCHEME)
            val dataSourceId = context.settingsDataStore.data.first().datasourceId.default(GROWINGIO_DATASOURCE_ID)
            val serverHost = context.settingsDataStore.data.first().serverHost.default(GROWINGIO_SERVER_HOST)
            configuration.setProject(projectId, urlScheme)
                .setDataSourceId(dataSourceId)
                .setDataCollectionServerHost(serverHost)
        }
    }

    fun String.default(default: String): String {
        if (this.isEmpty()) return default
        return this
    }

    companion object {
        const val GROWINGIO_PROJECT_ID = "0a1b4118dd954ec3bcc69da5138bdb96"
        const val GROWINGIO_URL_SCHEME = "growing.bd71d91eb56f5f53"
        const val GROWINGIO_DATASOURCE_ID = "baffd6fb52b78ca7"
        const val GROWINGIO_SERVER_HOST = "https://napi.growingio.com"
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
