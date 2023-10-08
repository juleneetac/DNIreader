package com.example.myapplication.utils

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.gemalto.jp2.JP2Decoder
import java.io.IOException
import java.io.InputStream

class DecodeJp2Coroutine(val view: ImageView, val bytearrayimage: ByteArray) {
    private val TAG = "DecodeJp2Coroutine"
    private val width: Int
    private val height: Int

    init {
        // Get the size of the ImageView
        width = view.width
        height = view.height
    }

    fun doInBackground(): Bitmap? {
        Log.d(TAG, String.format("View resolution: %d x %d", width, height))
        var ret: Bitmap? = null
        var inStream: InputStream? = null

        try {
            //inStream = view.context.assets.open("balloon.jp2")

            // Create a new JP2 decoder object
            val decoder = JP2Decoder(bytearrayimage)

            // Read image information only, but don't decode the actual image
            val header = decoder.readHeader()

            // Get the size of the image
            val imgWidth = header.width
            val imgHeight = header.height
            Log.d(TAG, String.format("JP2 resolution: %d x %d", imgWidth, imgHeight))

            // We halve the resolution until we go under the ImageView size or until we run out of available JP2 image resolutions
            var skipResolutions = 1
            while (skipResolutions < header.numResolutions) {
                imgWidth shr 1
                imgHeight shr 1
                if (imgWidth < width && imgHeight < height) break
                else skipResolutions++
            }

            // We break the loop when skipResolutions goes over the correct value
            skipResolutions--
            Log.d(TAG, String.format("Skipping %d resolutions", skipResolutions))

            // Set the number of resolutions to skip
            if (skipResolutions > 0) decoder.setSkipResolutions(skipResolutions)

            // Decode the image
            ret = decoder.decode()
            Log.d(TAG, String.format("Decoded at resolution: %d x %d", ret?.width, ret?.height))
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            close(inStream)
        }
        return ret
    }

    fun onPostExecute(bitmap: Bitmap?) {
        if (bitmap != null) {
            view.setImageBitmap(bitmap)
        }
    }

    private fun close(inputStream: InputStream?) {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}