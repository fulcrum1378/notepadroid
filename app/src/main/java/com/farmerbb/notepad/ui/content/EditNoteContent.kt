package com.farmerbb.notepad.ui.content

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farmerbb.notepad.R
import com.farmerbb.notepad.ui.previews.EditNotePreview

@Composable
fun EditNoteContent(
    text: String,
    textStyle: TextStyle = TextStyle(),
    onTextChanged: (String) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    var value by remember {
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        )
    }

    BasicTextField(
        value = value,
        onValueChange = {
            value = it
            onTextChanged(it.text)
        },
        textStyle = textStyle,
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
            .fillMaxWidth()
            .fillMaxHeight()
            .focusRequester(focusRequester)
    )

    if (value.text.isEmpty()) {
        BasicText(
            text = stringResource(id = R.string.edit_text),
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.LightGray
            ),
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
fun EditNoteContentPreview() = EditNotePreview()
