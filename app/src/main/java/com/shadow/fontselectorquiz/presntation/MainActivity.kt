package com.shadow.fontselectorquiz.presntation

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shadow.fontselectorquiz.R
import com.shadow.fontselectorquiz.domain.executor.FontDecorator
import com.shadow.fontselectorquiz.domain.repository.OfflineFirstRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repository = OfflineFirstRepository(this)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_fonts)
        val font = findViewById<TextView>(R.id.tv_show)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = FontFamilyRecyclerViewAdapter(FontFamilyRecyclerViewAdapter.itemSelector {
            font.typeface = it
        }, FontDecorator(repository))
        recyclerView.adapter = adapter
        repository.fontFamily.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { fonts-> adapter.update(fonts)}
    }

}
