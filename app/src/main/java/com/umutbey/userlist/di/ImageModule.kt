package com.umutbey.userlist


import android.content.Context
import android.graphics.*
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit


class ImageModule(context: Context? = null) {

    private val picassoCache = "picasso-cache"
    private val cacheDir = File(ApplicationWrapper.INSTANCE.cacheDir, picassoCache)
    private val cacheSize: Long = 500 * 1024 * 1024 // 500 MB
    private val cache = Cache(cacheDir, cacheSize)
    private val requestList = mutableListOf<String>()
    private val circleImage by lazy {
        object : Transformation {
            override fun transform(source: Bitmap): Bitmap {
                val size = Math.min(source.width, source.height)

                val x = (source.width - size) / 2
                val y = (source.height - size) / 2

                val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
                if (squaredBitmap != source) {
                    source.recycle()
                }

                val bitmap = Bitmap.createBitmap(size, size, source.config)

                val canvas = Canvas(bitmap)
                val paint = Paint()
                val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                paint.shader = shader
                paint.isAntiAlias = true

                val r = size / 2f
                canvas.drawCircle(r, r, r, paint)

                squaredBitmap.recycle()
                return bitmap
            }

            override fun key(): String {
                return "circle"
            }
        }
    }

    private val picasso by lazy {
        val okHttpClient = OkHttpClient().newBuilder()
        okHttpClient.connectTimeout(30, TimeUnit.SECONDS)
        okHttpClient.readTimeout(30, TimeUnit.SECONDS)


        okHttpClient.cache(cache)
        val okHttp3Downloader = OkHttp3Downloader(okHttpClient.build())
        context?.let {
            Picasso.Builder(context)
                .downloader(okHttp3Downloader).build()
        }
    }

    fun get(url: String, target: ImageView, isCircle: Boolean = false, callback: ((result: Boolean) -> Unit)? = null) {
        requestList.add(url)
        picasso?.load(url)?.tag(url)
            ?.fit()?.centerCrop()
            ?.also {
                if (isCircle) it.transform(circleImage)
            }
            ?.into(target, object : Callback {
                override fun onSuccess() {
                    requestList.remove(url)
                    callback?.let { it(true) }
                }

                override fun onError(e: Exception?) {
                    requestList.remove(url)
                    callback?.let { it(false) }
                }
            })
    }

    fun get(res: Int, target: ImageView, callback: ((result: Boolean) -> Unit)? = null) {
        requestList.add(res.toString())
        picasso?.load(res)?.tag(res)?.fit()?.centerCrop()?.into(target, object : Callback {
            override fun onSuccess() {
                requestList.remove(res.toString())
                callback?.let { it(true) }

            }

            override fun onError(e: Exception?) {
                requestList.remove(res.toString())
                callback?.let { it(false) }

            }
        })
    }

    fun getBitmap(url: String): Bitmap? {
        return picasso?.load(url)?.get()
    }

    fun destroy() {
        requestList.forEach {
            cancelRequest(tag = it)
        }
    }

    fun cancelRequest(target: ImageView? = null, tag: String? = null) {
        target?.let {
            picasso?.cancelRequest(it)
        }
        tag?.let {
            picasso?.cancelTag(it)
        }
    }

    fun clearCache() {
        cache.evictAll()
    }


}