package com.confluence.xianyu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

/**
 * 拍照协定
 * Input type  : Unit? 不需要传值
 * Output type : Uri?  拍照完成后的uri
 */
class TakePhotoContract : ActivityResultContract<Unit?, Uri?>() {

    @CallSuper
    override fun createIntent(context: Context, input: Unit?): Intent {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        val uri = intent?.data
        Log.d("parseResult", "take photo uri : $uri")
        return uri
    }
}
