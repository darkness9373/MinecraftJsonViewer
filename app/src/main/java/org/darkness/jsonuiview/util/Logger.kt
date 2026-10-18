package org.darkness.jsonuiview.util

class Logger(private val tag: String) {
    companion object {
        private const val DEBUG = true
    }

    fun d(message: String) {
        if (DEBUG) android.util.Log.d(tag, message)
    }

    fun i(message: String) {
        if (DEBUG) android.util.Log.i(tag, message)
    }

    fun w(message: String) {
        if (DEBUG) android.util.Log.w(tag, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (DEBUG) android.util.Log.e(tag, message, throwable)
    }
}