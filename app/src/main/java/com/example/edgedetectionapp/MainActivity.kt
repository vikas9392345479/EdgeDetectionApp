package com.example.edgedetectionapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var outputView: ImageView

    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var cameraCaptureSessions: CameraCaptureSession
    private lateinit var backgroundHandler: Handler
    private lateinit var backgroundThread: HandlerThread

    // Processing thread handler (uses same backgroundHandler for simplicity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textureView = findViewById(R.id.textureView)
        outputView = findViewById(R.id.outputView)

        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread.start()
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) { /* ignore */ }
    }

    private fun openCamera() {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = manager.cameraIdList.firstOrNull() ?: return
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestCameraPermission()
                return
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }

    private fun createCameraPreview() {
        val texture: SurfaceTexture = textureView.surfaceTexture ?: return
        // Keep preview size reasonable
        val width = maxOf(320, textureView.width)
        val height = maxOf(240, textureView.height)
        texture.setDefaultBufferSize(width, height)
        val surface = Surface(texture)
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice.createCaptureSession(listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSessions = session
                        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler)
                        // start periodic processing
                        startFrameProcessingLoop()
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                }, backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // Grab a bitmap from the TextureView and process it on background thread.
    private fun startFrameProcessingLoop() {
        val intervalMs = 100L // ~10 FPS; adjust for speed
        val runnable = object : Runnable {
            override fun run() {
                val bmp = textureView.bitmap
                if (bmp != null) {
                    // Resize to smaller for faster processing (optional)
                    val maxDim = 320
                    val scaled = if (bmp.width > maxDim || bmp.height > maxDim) {
                        val scale = min(maxDim.toFloat() / bmp.width, maxDim.toFloat() / bmp.height)
                        Bitmap.createScaledBitmap(bmp, (bmp.width * scale).toInt(), (bmp.height * scale).toInt(), true)
                    } else bmp

                    val edges = sobelEdgeDetect(scaled)
                    runOnUiThread { outputView.setImageBitmap(edges) }

                    if (scaled !== bmp) {
                        scaled.recycle()
                    }
                }
                backgroundHandler.postDelayed(this, intervalMs)
            }
        }
        backgroundHandler.post(runnable)
    }

    // Simple Sobel edge detection implemented in Kotlin (CPU). Returns ARGB_8888 Bitmap.
    private fun sobelEdgeDetect(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        // convert to grayscale float
        val gray = FloatArray(width * height)
        for (i in pixels.indices) {
            val c = pixels[i]
            val r = (c shr 16) and 0xff
            val g = (c shr 8) and 0xff
            val b = c and 0xff
            gray[i] = 0.299f * r + 0.587f * g + 0.114f * b
        }

        val out = IntArray(width * height)

        // Sobel kernels
        val gx = intArrayOf(-1, 0, 1, -2, 0, 2, -1, 0, 1)
        val gy = intArrayOf(-1, -2, -1, 0, 0, 0, 1, 2, 1)

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                var sx = 0f
                var sy = 0f
                var k = 0
                for (ky in -1..1) {
                    for (kx in -1..1) {
                        val v = gray[(y + ky) * width + (x + kx)]
                        sx += gx[k] * v
                        sy += gy[k] * v
                        k++
                    }
                }
                val mag = sqrt(sx * sx + sy * sy)
                val edge = min(255f, mag).toInt()
                val color = (0xFF shl 24) or (edge shl 16) or (edge shl 8) or edge
                out[y * width + x] = color
            }
        }
        // Fill borders (copy)
        for (x in 0 until width) {
            out[x] = out[width + x]
            out[(height - 1) * width + x] = out[(height - 2) * width + x]
        }
        for (y in 0 until height) {
            out[y * width] = out[y * width + 1]
            out[y * width + width - 1] = out[y * width + width - 2]
        }

        val outBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        outBmp.setPixels(out, 0, width, 0, 0, width, height)
        return outBmp
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable) openCamera()
        else textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) { openCamera() }
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = true
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            stopBackgroundThread()
        } catch (e: Exception) { /* ignore */ }
        try {
            cameraDevice.close()
        } catch (e: Exception) { /* ignore */ }
    }
}
