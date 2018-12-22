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
import com.example.tunghoang.ScriptC_singlesource
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var mOutBitmap : Bitmap? = null;
    var mbIn: Bitmap? = null;
    private var mInAllocation: Allocation? = null;
    private var mOutAllocation: Allocation? = null;
    private var mScript: ScriptC_singlesource? = null;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btn = findViewById<Button>(R.id.button);
        preProcess();

        btn.setOnClickListener(){
            fucku();
        }

    }
    fun preProcess(){
        mbIn = loadBitmap(R.drawable.lion);
        mOutBitmap =
                Bitmap.createBitmap(mbIn!!.getWidth(), mbIn!!.getHeight(), mbIn!!.getConfig());

        val RS: RenderScript = RenderScript.create(this)
        mScript = ScriptC_singlesource(RS);
        mInAllocation = Allocation.createFromBitmap(RS, mbIn, Allocation.MipmapControl.MIPMAP_NONE,
            Allocation.USAGE_SCRIPT);

        mOutAllocation = Allocation.createFromBitmap(
            RS, mOutBitmap, Allocation.MipmapControl.MIPMAP_NONE,
            android.renderscript.Allocation.USAGE_SCRIPT
        );
    }



    public fun fucku() {

        // var bm2 = blurBitmap(bm, 5.0f, this)
        var b = findViewById<ImageView>(R.id.result);

        mScript!!.invoke_process(mInAllocation, mOutAllocation)
        mOutAllocation!!.copyTo(mOutBitmap);
        b.setImageBitmap(mOutBitmap);

        b.invalidate();
    }

    private fun loadBitmap(resource: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        return BitmapFactory.decodeResource(resources, resource, options)
    }

}
