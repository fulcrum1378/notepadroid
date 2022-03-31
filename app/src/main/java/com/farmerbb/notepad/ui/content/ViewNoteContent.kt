package com.farmerbb.notepad.ui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.farmerbb.notepad.ui.previews.ViewNotePreview
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.halilibo.richtext.ui.RichTextThemeIntegration
import com.linkifytext.LinkifyText

@Composable
fun ViewNoteContent(
    text: String,
    textStyle: TextStyle = TextStyle(),
    markdown: Boolean = false
) {
    Box(
        modifier = Modifier.verticalScroll(
            state = rememberScrollState()
        )
    ) {
        val modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
            .fillMaxWidth()
            .fillMaxHeight()

        SelectionContainer {
            if (markdown) {
                val localTextStyle = compositionLocalOf {
                    textStyle.copy(color = Color.Unspecified)
                }
                val localContentColor = compositionLocalOf {
                    textStyle.color
                }

                RichTextThemeIntegration(
                    textStyle = { localTextStyle.current },
                    contentColor = { localContentColor.current },
                    ProvideTextStyle = { textStyle, content ->
                        CompositionLocalProvider(
                            localTextStyle provides textStyle,
                            content = content
                        )
                    },
                    ProvideContentColor = { color, content ->
                        CompositionLocalProvider(
                            localContentColor provides color,
                            content = content
                        )
                    }
                ) {
                    RichText(modifier = modifier) {
                        Markdown(text)
                    }
                }
            } else {
                LinkifyText(
                    text = text,
                    style = textStyle,
                    modifier = modifier
                )
            }
        }
    }
}

@Preview
@Composable
fun ViewNoteContentPreview() = ViewNotePreview()
