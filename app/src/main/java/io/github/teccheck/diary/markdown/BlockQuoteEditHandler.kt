/*
Adapted from
https://github.com/noties/Markwon/blob/master/app-sample/src/main/java/io/noties/markwon/app/samples/editor/shared/BlockQuoteEditHandler.java
 */
package io.github.teccheck.diary.markdown

import android.text.Editable
import android.text.Spanned
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.spans.BlockQuoteSpan
import io.noties.markwon.editor.EditHandler
import io.noties.markwon.editor.PersistedSpans

class BlockQuoteEditHandler : EditHandler<BlockQuoteSpan> {
    private var theme: MarkwonTheme? = null
    override fun init(markwon: Markwon) {
        theme = markwon.configuration().theme()
    }

    override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
        builder.persistSpan(BlockQuoteSpan::class.java) { BlockQuoteSpan(theme!!) }
    }

    override fun handleMarkdownSpan(
        persistedSpans: PersistedSpans,
        editable: Editable,
        input: String,
        span: BlockQuoteSpan,
        spanStart: Int,
        spanTextLength: Int
    ) {
        // todo: here we should actually find a proper ending of a block quote...
        editable.setSpan(
            persistedSpans.get(BlockQuoteSpan::class.java),
            spanStart,
            spanStart + spanTextLength,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    override fun markdownSpanType() = BlockQuoteSpan::class.java
}