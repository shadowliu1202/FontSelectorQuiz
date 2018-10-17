package com.shadow.fontselectorquiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.shadow.fontselectorquiz.domain.repository.cloud.GoogleApiClient
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val client = GoogleApiClient(this)
        Log.d("test","test")
        client.webFontService.webFonts.subscribeOn(Schedulers.io())
                .subscribe { a->Log.d("test","getL:"+a.items[0].files.toString()) }
    }
}
