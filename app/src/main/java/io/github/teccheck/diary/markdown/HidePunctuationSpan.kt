import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class HidePunctuationSpan : ReplacementSpan() {
    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        // last space (which is swallowed until next non-space character appears)
        // block quote
        // code tick

        //      Debug.i("text: '%s', %d-%d (%d)", text.subSequence(start, end), start, end, text.length());
        if (end == text.length) {
            // TODO: find first non-space character (not just first one because commonmark allows
            //  arbitrary (0-3) white spaces before content starts

            //  TODO: if all white space - render?
            val c = text[start]
            if (// TODO: not thematic break
            // `*` is fine but only for a list
                '#' == c || '>' == c || '-' == c || '+' == c || isBulletList(
                    text, c, start, end
                ) || Character.isDigit(c) // assuming ordered list (replacement should only happen for ordered lists)
                || Character.isWhitespace(c)
            ) {
                return (paint.measureText(text, start, end) + 0.5f).toInt()
            }
        }
        return 0
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // will be called only when getSize is not 0 (and if it was once reported as 0...)
        if (end == text.length) {

            // if first non-space is `*` then check for is bullet
            //  else `**` would be still rendered at the end of the emphasis
            if (text[start] == '*' && !isBulletList(text, '*', start, end)) {
                return
            }

            // TODO: inline code last tick received here, handle it (do not highlight)
            //  why can't we have reported width in this method for supplied text?

            // let's use color to make it distinct from the rest of the text for demonstration purposes
            paint.color = -0x10000
            canvas.drawText(text, start, end, x, y.toFloat(), paint)
        }
    }

    companion object {
        private fun isBulletList(
            text: CharSequence, firstChar: Char, start: Int, end: Int
        ): Boolean {
            return ('*' == firstChar && (end - start == 1 || Character.isWhitespace(text[start + 1])))
        }
    }
}