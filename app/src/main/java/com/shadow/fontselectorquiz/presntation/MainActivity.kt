package com.shadow.fontselectorquiz.presntation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shadow.fontselectorquiz.R
import com.shadow.fontselectorquiz.domain.executor.FontDecorator
import com.shadow.fontselectorquiz.domain.executor.FontTypeFetcher
import com.shadow.fontselectorquiz.domain.repository.OfflineFirstRepository
import com.shadow.fontselectorquiz.domain.repository.db.WebFontDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MainActivity : AppCompatActivity() {

    lateinit var dispose:Disposable
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repository = OfflineFirstRepository(this, WebFontDatabase.getDatabase(this))
        val recyclerView = findViewById<RecyclerView>(R.id.rv_fonts)
        val font = findViewById<TextView>(R.id.tv_show)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val manager = LinearLayoutManager(this)
        val order = PublishSubject.create<Number>()
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        val adapter = FontFamilyRecyclerViewAdapter(FontFamilyRecyclerViewAdapter.itemSelector {
            font.typeface = it
        }, FontDecorator(FontTypeFetcher.getInstance(repository)))
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                order.onNext(position)
            }
        }
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter

        dispose =order.flatMap { orderBy -> repository.getFontFamilyList(orderBy as Int) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::submitList)
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose.dispose()
    }
}
