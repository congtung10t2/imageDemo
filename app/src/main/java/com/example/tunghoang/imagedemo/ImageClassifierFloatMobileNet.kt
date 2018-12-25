package com.example.tunghoang.imagedemo

import android.app.Activity
import java.io.IOException
class ImageClassifierFloatMobileNet: ImageClassifier {
    private var labelProbArray: Array<FloatArray>? = null
    override fun getModelPath(): String {
        return "mobilenet_v1_1.0_224.tflite" //To change body of created functions use File | Settings | File Templates.
    }
    constructor(activity: Activity?) : super(activity) {
        labelProbArray = Array(1) { FloatArray(numLabels) }
    }


    override fun getLabelPath(): String {
        return "labels_mobilenet_quant_v1_224.txt"
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun getImageSizeX(): Int {
        return 224 //To change body of created functions use File | Settings | File Templates.
    }

    override fun getImageSizeY(): Int {
        return 224 //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNumBytesPerChannel(): Int {
        return 4//To change body of created functions use File | Settings | File Templates.
    }

    override fun addPixelValue(pixelValue: Int) {
        imgData.putFloat((pixelValue shr 16 and 0xFF) / 255f)
        imgData.putFloat((pixelValue shr 8 and 0xFF) / 255f)
        imgData.putFloat((pixelValue and 0xFF) / 255f)
    }

    override fun getProbability(labelIndex: Int): Float {
        return labelProbArray!![0][labelIndex]
    }

    override fun setProbability(labelIndex: Int, value: Number) {
        labelProbArray!![0][labelIndex] = value.toFloat()
    }

    override fun getNormalizedProbability(labelIndex: Int): Float {
        return labelProbArray!![0][labelIndex]
    }

    override fun runInference() {
        tflite.run(imgData, labelProbArray)
    }

}