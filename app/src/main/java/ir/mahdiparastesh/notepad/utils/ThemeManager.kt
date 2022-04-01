package ir.mahdiparastesh.notepad.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ir.mahdiparastesh.notepad.R
import us.feras.mdv.MarkdownView

object ThemeManager {
    @JvmStatic
    fun setBackgroundColor(context: Context, noteViewEdit: View) {
        noteViewEdit.setBackgroundColor(ContextCompat.getColor(context, R.color.window_background))
    }

    @JvmStatic
    fun setTextColor(context: Context, noteContents: TextView) {
        noteContents.setTextColor(ContextCompat.getColor(context, R.color.text_color_primary))
    }

    @JvmStatic
    fun setTextColorDate(context: Context, noteDate: TextView) {
        noteDate.setTextColor(ContextCompat.getColor(context, R.color.text_color_secondary))
    }

    @JvmStatic
    fun setFontSize(pref: SharedPreferences, noteContents: TextView) {
        noteContents.textSize = when (pref.getString("font_size", "normal")) {
            "smallest" -> 12f
            "small" -> 14f
            "normal" -> 16f
            "large" -> 18f
            else -> 20f
        }
    }

    @JvmStatic
    fun setFontSizeDate(pref: SharedPreferences, noteDate: TextView) {
        noteDate.textSize =
            when (pref.getString("font_size", "normal")) {
                "smallest" -> 8f
                "small" -> 10f
                "normal" -> 12f
                "large" -> 14f
                else -> 16f
            }
    }

    @JvmStatic
    fun setFont(pref: SharedPreferences, noteContents: TextView) {
        noteContents.typeface = with(pref.getString("theme", "sans").orEmpty()) {
            when {
                contains("sans") -> Typeface.SANS_SERIF
                contains("serif") -> Typeface.SERIF
                else -> Typeface.MONOSPACE
            }
        }
    }

    private fun getFontMarkdown(pref: SharedPreferences) =
        with(pref.getString("theme", "sans").orEmpty()) {
            when {
                contains("sans") -> "sans"
                contains("serif") -> "serif"
                else -> "monospace"
            }
        }

    private fun getTextColor(context: Context) =
        ContextCompat.getColor(context, R.color.text_color_primary)

    @JvmStatic
    fun applyTextSettings(activity: FragmentActivity, noteContents: EditText) {
        val pref = activity.getSharedPreferences(
            activity.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
        val scrollView = activity.findViewById<ScrollView>(R.id.scrollView1)

        setBackgroundColor(activity, scrollView)
        setTextColor(activity, noteContents)
        setFont(pref, noteContents)
        setFontSize(pref, noteContents)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun applyNoteViewTheme(
        activity: FragmentActivity, noteContents: TextView?, markdownView: MarkdownView?
    ): String {
        val pref = activity.getSharedPreferences(
            activity.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
        val scrollView = activity.findViewById<ScrollView>(R.id.scrollView)

        var fontFamily = ""
        var textColor = -1
        var textSize = -1F

        noteContents?.let {
            setTextColor(activity, noteContents)
            setBackgroundColor(activity, noteContents)
            setFont(pref, noteContents)
            setFontSize(pref, noteContents)
            textSize = noteContents.textSize
        }

        markdownView?.let {
            setBackgroundColor(activity, markdownView)
            fontFamily = getFontMarkdown(pref)
            textColor = getTextColor(activity)
        }

        setBackgroundColor(activity, scrollView)

        if (markdownView == null) return ""

        val density = activity.resources.displayMetrics.density
        val topBottom = activity.resources.getDimension(R.dimen.padding_top_bottom) / density
        val leftRight = activity.resources.getDimension(R.dimen.padding_left_right) / density

        val fontColor = Integer.toHexString(textColor).replace("ff", "")
        val linkColor =
            Integer.toHexString(TextView(activity).linkTextColors.defaultColor).replace("ff", "")

        markdownView.apply {
            settings.apply {
                javaScriptEnabled = false
                loadsImagesAutomatically = false
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    try {
                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                    }

                    return true
                }
            }
        }

        return """body {
                    margin: ${topBottom}px ${topBottom}px ${leftRight}px ${leftRight}px;
                    font-family: $fontFamily;
                    font-size: ${textSize}px;
                    color: #$fontColor;
                  }
                  
                  a {
                    color: #$linkColor;
                  }""".trimIndent()
    }
}
