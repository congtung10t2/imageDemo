package com.example.tunghoang.imagedemo


import org.tensorflow.lite.Delegate

/**
 * Helper class for `GpuDelegate`.
 *
 *
 * WARNING: This is an experimental API and subject to change.
 */
object GpuDelegateHelper {
    private fun GpuDelegateHelper() {}

    /** Checks whether `GpuDelegate` is available.  */
    public fun isGpuDelegateAvailable(): Boolean {
        try {
            Class.forName("org.tensorflow.lite.experimental.GpuDelegate")
            return true
        } catch (e: Exception) {
            return false
        }

    }
    /** Checks whether `GpuDelegate` is available.  */

    /** Returns an instance of `GpuDelegate` if available.  */
     public fun createGpuDelegate(): Delegate {
        try {
            return Class.forName("org.tensorflow.lite.experimental.GpuDelegate")
                .asSubclass(Delegate::class.java)
                .getDeclaredConstructor()
                .newInstance()
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }

    }
}