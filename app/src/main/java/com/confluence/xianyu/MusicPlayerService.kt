package com.confluence.xianyu

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnTouchRangeListener
import com.lzf.easyfloat.utils.DragUtils
import com.lzf.easyfloat.widget.BaseSwitchView


class MusicPlayerService : Service() {

    var mImageView: ImageView? = null
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        DataCenter.liveData.observeForever {
            mImageView?.let { it1 -> Glide.with(App.context).load(it).into(it1) }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")


        // 在API11之后构建Notification的方式
        val builder: Notification.Builder = Notification.Builder(this.applicationContext) //获取一个Notification构造器
        val nfIntent = Intent(this, MainActivity::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("CHANNEL_ONE_ID", "CHANNEL_ONE_NAME", NotificationManager.IMPORTANCE_MIN)
            notificationChannel.enableLights(false)
            notificationChannel.setShowBadge(false) //是否显示角标
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            val systemService = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            systemService.createNotificationChannel(notificationChannel)
            builder.setChannelId("CHANNEL_ONE_ID")
        }

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.logo
                )
            ) // 设置下拉列表中的图标(大图标)
            .setContentTitle("一只咸鱼") // 设置下拉列表里的标题
            .setSmallIcon(R.drawable.logo) // 设置状态栏内的小图标
            .setContentText("闲鱼running") // 设置上下文内容
            .setWhen(System.currentTimeMillis()) // 设置该通知发生的时间
        val notification: Notification = builder.build() // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND //设置为默认的声音
        startForeground(100, notification)


        fun showAppFloat() {
            EasyFloat.with(this@MusicPlayerService)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setSidePattern(SidePattern.RESULT_SIDE)
                .setImmersionStatusBar(true)
                .setGravity(Gravity.CENTER, -40, 40)
                .setLayout(R.layout.float_iv) {
                    mImageView = it.findViewById<ImageView>(R.id.demoIv)
                    Glide.with(App.context).asGif().load(R.drawable.gif309).into(mImageView!!)
                    mImageView!!.setOnClickListener {
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
        showAppFloat()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind()")
        throw UnsupportedOperationException("Not yet implemented")
    }

    companion object {
        private val TAG = MusicPlayerService::class.java.simpleName
    }
}