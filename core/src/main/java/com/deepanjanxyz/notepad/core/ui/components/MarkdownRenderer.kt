package com.deepanjanxyz.notepad.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownRenderer(
    markdown: String,
    modifier: Modifier = Modifier
) {
    SelectionContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val lines = markdown.split("\n")
            var inCodeBlock = false
            val codeBlockContent = StringBuilder()

            for (line in lines) {
                val trimmed = line.trim()

                if (trimmed.startsWith("```")) {
                    if (inCodeBlock) {
                        // End of code block
                        CodeBlockView(codeBlockContent.toString())
                        codeBlockContent.clear()
                        inCodeBlock = false
                    } else {
                        inCodeBlock = true
                    }
                    continue
                }

                if (inCodeBlock) {
                    codeBlockContent.append(line).append("\n")
                    continue
                }

                when {
                    trimmed.startsWith("# ") -> HeadingView(trimmed.substring(2), level = 1)
                    trimmed.startsWith("## ") -> HeadingView(trimmed.substring(3), level = 2)
                    trimmed.startsWith("### ") -> HeadingView(trimmed.substring(4), level = 3)
                    trimmed.startsWith("#### ") -> HeadingView(trimmed.substring(5), level = 4)
                    trimmed.startsWith("---") || trimmed.startsWith("***") -> HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    trimmed.startsWith("> ") -> BlockquoteView(trimmed.substring(2))
                    trimmed.startsWith("- [ ] ") || trimmed.startsWith("* [ ] ") -> TaskListItemView(
                        text = trimmed.substring(6),
                        checked = false
                    )
                    trimmed.startsWith("- [x] ") || trimmed.startsWith("* [x] ") || trimmed.startsWith("- [X] ") -> TaskListItemView(
                        text = trimmed.substring(6),
                        checked = true
                    )
                    trimmed.startsWith("- ") || trimmed.startsWith("* ") -> BulletListItemView(trimmed.substring(2))
                    trimmed.isEmpty() -> Spacer(modifier = Modifier.height(6.dp))
                    else -> FormattedTextView(line)
                }
            }

            if (inCodeBlock && codeBlockContent.isNotEmpty()) {
                CodeBlockView(codeBlockContent.toString())
            }
        }
    }
}

@Composable
private fun HeadingView(text: String, level: Int) {
    val style = when (level) {
        1 -> MaterialTheme.typography.headlineLarge
        2 -> MaterialTheme.typography.headlineMedium
        3 -> MaterialTheme.typography.titleLarge
        else -> MaterialTheme.typography.labelLarge
    }
    Text(
        text = parseInlineMarkdown(text),
        style = style,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun BulletListItemView(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
    ) {
        Text(
            text = "• ",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = parseInlineMarkdown(text),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun TaskListItemView(text: String, checked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = parseInlineMarkdown(text),
            style = MaterialTheme.typography.bodyLarge.copy(
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun BlockquoteView(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = parseInlineMarkdown(text),
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CodeBlockView(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FormattedTextView(text: String) {
    Text(
        text = parseInlineMarkdown(text),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

private fun parseInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val length = text.length

        while (cursor < length) {
            val boldIndex = text.indexOf("**", cursor)
            val italicIndex = text.indexOf("*", cursor)
            val codeIndex = text.indexOf("`", cursor)
            val strikeIndex = text.indexOf("~~", cursor)

            // Find closest match
            val nextIndex = listOf(boldIndex, italicIndex, codeIndex, strikeIndex)
                .filter { it != -1 }
                .minOrNull() ?: -1

            if (nextIndex == -1) {
                append(text.substring(cursor))
                break
            }

            append(text.substring(cursor, nextIndex))

            when (nextIndex) {
                boldIndex -> {
                    val end = text.indexOf("**", boldIndex + 2)
                    if (end != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(text.substring(boldIndex + 2, end))
                        }
                        cursor = end + 2
                    } else {
                        append("**")
                        cursor = boldIndex + 2
                    }
                }
                strikeIndex -> {
                    val end = text.indexOf("~~", strikeIndex + 2)
                    if (end != -1) {
                        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                            append(text.substring(strikeIndex + 2, end))
                        }
                        cursor = end + 2
                    } else {
                        append("~~")
                        cursor = strikeIndex + 2
                    }
                }
                codeIndex -> {
                    val end = text.indexOf("`", codeIndex + 1)
                    if (end != -1) {
                        withStyle(SpanStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp)) {
                            append(text.substring(codeIndex + 1, end))
                        }
                        cursor = end + 1
                    } else {
                        append("`")
                        cursor = codeIndex + 1
                    }
                }
                italicIndex -> {
                    val end = text.indexOf("*", italicIndex + 1)
                    if (end != -1 && end != italicIndex + 1) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(text.substring(italicIndex + 1, end))
                        }
                        cursor = end + 1
                    } else {
                        append("*")
                        cursor = italicIndex + 1
                    }
                }
                else -> {
                    cursor++
                }
            }
        }
    }
}
