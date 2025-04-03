package io.github.teccheck.diary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class DiaryListActivity : DiaryBaseActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = diaryStorage.getDiaryEntryNames()?.let {
            it.sortDescending()
            DiaryEntryRecyclerAdapter(it, this::viewDiaryEntry)
        }

        ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun viewDiaryEntry(filename: String) {
        val intent = Intent(this, DiaryViewActivity::class.java)
        intent.putExtra(DiaryViewActivity.EXTRA_FILENAME, filename)
        startActivity(intent)
    }

    inner class DiaryEntryRecyclerAdapter(
        private val filenames: Array<String>,
        private val diaryEntryClickListener: DiaryEntryClickListener
    ) :
        RecyclerView.Adapter<DiaryEntryRecyclerAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.text_view)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_diary_entry, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = filenames.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val filename = filenames[position]

            holder.textView.text = DiaryUtils.getLocalizedDateString(filename)
            holder.itemView.setOnClickListener { diaryEntryClickListener.onClick(filename) }
        }
    }

    fun interface DiaryEntryClickListener {
        fun onClick(filename: String)
    }
}