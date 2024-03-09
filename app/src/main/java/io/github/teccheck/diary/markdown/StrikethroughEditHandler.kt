/*
Adapted from
https://github.com/noties/Markwon/blob/master/app-sample/src/main/java/io/noties/markwon/app/samples/editor/shared/StrikethroughEditHandler.java
 */
package io.github.teccheck.diary.markdown

import android.text.Editable
import android.text.Spanned
import android.text.style.StrikethroughSpan
import io.noties.markwon.editor.AbstractEditHandler
import io.noties.markwon.editor.MarkwonEditorUtils
import io.noties.markwon.editor.PersistedSpans

class StrikethroughEditHandler : AbstractEditHandler<StrikethroughSpan>() {
    override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
        builder.persistSpan(StrikethroughSpan::class.java) { StrikethroughSpan() }
    }

    override fun handleMarkdownSpan(
        persistedSpans: PersistedSpans,
        editable: Editable,
        input: String,
        span: StrikethroughSpan,
        spanStart: Int,
        spanTextLength: Int
    ) {
        val match = MarkwonEditorUtils.findDelimited(input, spanStart, "~~")
        if (match != null) {
            editable.setSpan(
                persistedSpans.get(StrikethroughSpan::class.java),
                match.start(),
                match.end(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun markdownSpanType() = StrikethroughSpan::class.java
}