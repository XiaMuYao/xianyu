package com.confluence.xianyu

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.confluence.xianyu.DataCenter.liveData
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnTouchRangeListener
import com.lzf.easyfloat.utils.DragUtils
import com.lzf.easyfloat.widget.BaseSwitchView


class MainActivity : AppCompatActivity() {


    private val takePhoto = registerForActivityResult(TakePhotoContract()) { uri: Uri? ->
        if (uri != null) {
            Log.d("url:", "${uri}")
            Log.d("url getRealPathFromUri:", "${getRealPathFromUri(this, uri)}")
            val path = getRealPathFromUri(this, uri)
            liveData.postValue(path)
//            Glide.with(App.context).asGif().load(getRealPathFromUri(this,uri)).into(findViewById(R.id.imageview))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        findViewById<Button>(R.id.setLocalGif).setOnClickListener {

            takePhoto.launch(null)


        }



        findViewById<Button>(R.id.button).setOnClickListener {

            val intentStart = Intent(this, MusicPlayerService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intentStart);
            } else {
                startService(intentStart)
            }

//            showAppFloat()
        }
    }

    private fun showAppFloat() {
        EasyFloat.with(this.applicationContext)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .setImmersionStatusBar(true)
            .setGravity(Gravity.CENTER, -40, 40)
            .setLayout(R.layout.float_iv) {
                Glide.with(App.context).asGif().load(R.drawable.gif309).into(it.findViewById(R.id.demoIv))
                it.findViewById<ImageView>(R.id.demoIv).setOnClickListener {
                    toast("爷是一条咸鱼!")
                }
            }
            .registerCallback {
                drag { _, motionEvent ->
                    DragUtils.registerDragClose(motionEvent, object : OnTouchRangeListener {
                        override fun touchInRange(inRange: Boolean, view: BaseSwitchView) {
                            view.findViewById<TextView>(com.lzf.easyfloat.R.id.tv_delete).text =
                                if (inRange) "松手删除" else "删除浮窗"

                            view.findViewById<ImageView>(com.lzf.easyfloat.R.id.iv_delete)
                                .setImageResource(
                                    if (inRange) com.lzf.easyfloat.R.drawable.icon_delete_selected
                                    else com.lzf.easyfloat.R.drawable.icon_delete_normal
                                )
                        }

                        override fun touchUpInRange() {
                            EasyFloat.dismiss()
                        }
                    }, showPattern = ShowPattern.ALL_TIME)
                }
            }
            .show()
    }

    private fun toast(string: String = "onClick") =
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()


    fun getRealPathFromUri(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            column_index?.let { cursor?.getString(it) }
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }
}