package com.shadow.fontselectorquiz.presntation

import android.annotation.SuppressLint
import android.os.Bundle
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
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = FontFamilyRecyclerViewAdapter(FontDecorator(repository))
        recyclerView.adapter = adapter
        repository.fontFamily.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { fonts-> adapter.update(fonts)}
    }
}
