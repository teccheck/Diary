/*
Adapted from
https://github.com/noties/Markwon/blob/master/app-sample/src/main/java/io/noties/markwon/app/samples/editor/shared/HeadingEditHandler.java
 */

package io.github.teccheck.diary.markdown

import android.text.Editable
import android.text.Spanned
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.spans.HeadingSpan
import io.noties.markwon.editor.EditHandler
import io.noties.markwon.editor.PersistedSpans

class HeadingEditHandler : EditHandler<HeadingSpan> {
    private var theme: MarkwonTheme? = null
    override fun init(markwon: Markwon) {
        theme = markwon.configuration().theme()
    }

    override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
        builder
            .persistSpan(Head1::class.java) { Head1(theme!!) }
            .persistSpan(Head2::class.java) { Head2(theme!!) }
            .persistSpan(Head3::class.java) { Head3(theme!!) }
            .persistSpan(Head4::class.java) { Head4(theme!!) }
    }

    override fun handleMarkdownSpan(
        persistedSpans: PersistedSpans,
        editable: Editable,
        input: String,
        span: HeadingSpan,
        spanStart: Int,
        spanTextLength: Int
    ) {
        val type = when (span.level) {
            1 -> Head1::class.java
            2 -> Head2::class.java
            3 -> Head3::class.java
            4 -> Head4::class.java
            else -> null
        }

        if (type != null) {
            val index = input.indexOf('\n', spanStart + spanTextLength)
            val end = if (index < 0) input.length else index
            editable.setSpan(persistedSpans[type], spanStart, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun markdownSpanType() = HeadingSpan::class.java

    private class Head1(theme: MarkwonTheme) : HeadingSpan(theme, 1)
    private class Head2(theme: MarkwonTheme) : HeadingSpan(theme, 2)
    private class Head3(theme: MarkwonTheme) : HeadingSpan(theme, 3)
    private class Head4(theme: MarkwonTheme) : HeadingSpan(theme, 4)
}