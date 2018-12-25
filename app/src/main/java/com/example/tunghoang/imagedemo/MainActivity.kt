package com.example.tunghoang.imagedemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import android.content.Context;
import android.support.annotation.RequiresApi
import android.widget.ImageView
import android.graphics.drawable.BitmapDrawable
import android.widget.Button
import android.graphics.BitmapFactory

import android.R.attr.bitmap
import android.graphics.Canvas
import android.os.Environment
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.RenderScript
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.TextView
import com.example.tunghoang.ScriptC_singlesource
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private var classifier: ImageClassifier? = null
    var textView: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        var btn = findViewById<Button>(R.id.button);
        preProcess();

        btn.setOnClickListener(){
            fucku();
        }

    }
    fun preProcess(){
        var image = findViewById<ImageView>(R.id.source);
        try {
            classifier = ImageClassifierFloatMobileNet(this);
        } catch (e:IOException) {
            Log.d("fuck", "Failed to load", e);
            classifier = null;
        }
        classifier!!.useCPU();
    }

    private fun showToast(s: String) {
        val builder = SpannableStringBuilder()
        val str1 = SpannableString(s)
        builder.append(str1)
        showToast(builder)
    }

    private fun showToast(builder: SpannableStringBuilder) {
        val activity = this
        if (activity != null) {
            activity!!.runOnUiThread(
                Runnable { textView!!.setText(builder, TextView.BufferType.SPANNABLE) })
        }
    }


    public fun fucku() {
        val bitmap =loadBitmap(R.drawable.imagedemo);
        // var bm2 = blurBitmap(bm, 5.0f, this)
        val textToShow = SpannableStringBuilder()
        val bitmap2 = Bitmap.createScaledBitmap(bitmap, 224, 224, false)
        val a = bitmap2.byteCount;
        classifier!!.classifyFrame(bitmap2, textToShow)

        textView = findViewById(R.id.textView)
        showToast(textToShow);


    }

    private fun loadBitmap(resource: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        return BitmapFactory.decodeResource(resources, resource, options)
    }

}
