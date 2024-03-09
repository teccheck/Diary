package io.github.teccheck.diary

import io.github.teccheck.diary.markdown.HidePunctuationSpan
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import com.google.android.material.appbar.MaterialToolbar
import io.github.teccheck.diary.markdown.BlockQuoteEditHandler
import io.github.teccheck.diary.markdown.CodeEditHandler
import io.github.teccheck.diary.markdown.HeadingEditHandler
import io.github.teccheck.diary.markdown.StrikethroughEditHandler
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.editor.handler.EmphasisEditHandler
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler

class DiaryActivity : DiaryBaseActivity() {

    private lateinit var textInput: AppCompatEditText
    private lateinit var markdown: Markwon
    private lateinit var editor: MarkwonEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_diary_list -> {
                    startActivity(Intent(this, DiaryListActivity::class.java))
                    true
                }

                R.id.action_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }

                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }

        textInput = findViewById(R.id.text_input)
        textInput.setText(diaryStorage.getCurrentDiaryText())
        textInput.doOnTextChanged { text, _, _, _ -> diaryStorage.setCurrentDiaryText(text.toString()) }

        markdown = Markwon.builder(this).usePlugin(SoftBreakAddsNewLinePlugin.create()).build()
        editor = MarkwonEditor.builder(markdown)
            .punctuationSpan(HidePunctuationSpan::class.java) { HidePunctuationSpan() }
            .useEditHandler(EmphasisEditHandler())
            .useEditHandler(StrongEmphasisEditHandler())
            .useEditHandler(StrikethroughEditHandler())
            .useEditHandler( CodeEditHandler())
            .useEditHandler( BlockQuoteEditHandler())
            .useEditHandler(HeadingEditHandler())
            .build()

        textInput.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))
        editor.process(textInput.text!!)
    }

    override fun onPause() {
        diaryStorage.writeCurrentDiary()
        super.onPause()
    }
}