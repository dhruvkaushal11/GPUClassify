package com.example

import android.app.ActivityManager
import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.os.Build
import android.os.Debug
import android.util.Log
import androidx.annotation.RequiresApi


enum class DeviceClass {
    HIGH, MEDIUM, LOW
}


object DevicePerformanceClassifier {

    fun getGpuName(): String {
        try {
            val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            val version = IntArray(2)
            EGL14.eglInitialize(display, version, 0, version, 1)

            val configAttr = intArrayOf(
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_NONE
            )

            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfig = IntArray(1)
            EGL14.eglChooseConfig(display, configAttr, 0, configs, 0, 1, numConfig, 0)

            if (numConfig[0] == 0) {
                return ""
            }

            val contextAttr = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
            )

            val context = EGL14.eglCreateContext(
                display, configs[0], EGL14.EGL_NO_CONTEXT, contextAttr, 0
            )

            val surfaceAttr = intArrayOf(
                EGL14.EGL_WIDTH, 1,
                EGL14.EGL_HEIGHT, 1,
                EGL14.EGL_NONE
            )

            val surface = EGL14.eglCreatePbufferSurface(display, configs[0], surfaceAttr, 0)
            EGL14.eglMakeCurrent(display, surface, surface, context)

            val renderer = GLES20.glGetString(GLES20.GL_RENDERER) ?: ""

            // Clean up
            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroySurface(display, surface)
            EGL14.eglDestroyContext(display, context)
            EGL14.eglTerminate(display)

            return renderer.lowercase()
        } catch (e: Exception) {
            Log.e("GpuClassifier", "Error getting GPU name via EGL", e)
            return ""
        }
    }

    // High-end GPU regex patterns
    // High-End GPU Regex Patterns
    private val highEndGpuRegex = listOf(
        "adreno\\s*(\\(tm\\)\\s*)?(6[0-9][0-9]|7[0-9][0-9]|8[0-9][0-9]|530|540|550|560|570|580|590)",
        "mali-g(7[0-9]|8[0-9]|9[0-9])",
        "mali-t(880|760)",
        "mali.*immortalis",
        "powervr\\s*(gm|gt|xt|9|rogue.*xt)",
        "tegra\\s*[kx][1-9]"
    ).map { it.toRegex(RegexOption.IGNORE_CASE) }

    // Mid-Tier GPU Regex Patterns
    private val midTierGpuRegex = listOf(
        "adreno\\s*(\\(tm\\)\\s*)?(5[0-2][0-9]|4[3-9][0-9])",
        "mali-g([3-6][0-9])",
        "mali-t(6[2-9][0-9]|7[0-7][0-9]|820)",
        "powervr\\s*(g6|ge|gx|rogue(?!.*xt))",
        "vivante.*gc(2|3|4|5|6)",
        "videocore\\s*(iv|v|4|5|6)"
    ).map { it.toRegex(RegexOption.IGNORE_CASE) }

    // Low-End GPU Regex Patterns
    private val lowEndGpuRegex = listOf(
        "adreno\\s*(\\(tm\\)\\s*)?(3[0-9][0-9]|4[0-2][0-9])",
        "mali-400",
        "mali-450",
        "powervr\\s*sgx",
        "vivante.*gc(7000|8000|9000)",
        "tegra\\s*3|4"
    ).map { it.toRegex(RegexOption.IGNORE_CASE) }

    // Low-end is anything not matching the above patterns

    fun classifyGpu(): Int {
        val gpuName = getGpuName()

        // Check for high-end GPU match
        if (highEndGpuRegex.any { it.containsMatchIn(gpuName) }) {

            return 3 // High-end
        }

        // Check for mid-tier GPU match
        if (midTierGpuRegex.any { it.containsMatchIn(gpuName) }) {


            return 2 // Mid-tier
        }

        if (lowEndGpuRegex.any { it.containsMatchIn(gpuName) }) {


            return 1// Mid-tier
        }

        // Default to low-end
        return 0
    }
}
