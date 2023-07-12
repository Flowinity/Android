package com.troplo.privateuploader.components.chat

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

// Regex containing the syntax tokens
val symbolPattern by lazy {
    Regex("""(https?://[^\s\t\n]+)|(`[^`]+`)|(@\w+)|(\*[\w]+\*)|(_[\w]+_)|(~[\w]+~)""")
}

// Accepted annotations for the ClickableTextWrapper
enum class SymbolAnnotationType {
    USER, LINK, COLLECTION
}

typealias StringAnnotation = AnnotatedString.Range<String>
// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>

@Composable
fun MarkdownTextV2(modifier: Modifier = Modifier, content: String, color: Color = LocalContentColor.current, onClick: (() -> Unit)? = null, onLongClick: (() -> Unit)? = null) {
    val context = LocalContext.current
    val styledMessage = ProcessMessageText(
        messageText = content
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    if(onLongClick != null) {
                        onLongClick()
                    }
                },
                onTap = {

                }
            )
        }.then(modifier),
        onClick = {
            if(onClick == null) {
                styledMessage
                    .getStringAnnotations(start = it, end = it)
                    .firstOrNull()
                    ?.let { annotation ->
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }

                            SymbolAnnotationType.USER.name -> {
                                Log.d("MarkdownText", "User annotation clicked")
                            }

                            else -> Unit
                        }
                    }
            } else {
                onClick()
            }
        }
    )
}

@Composable
fun c(
    text: String
): AnnotatedString {
    val tokens = symbolPattern.findAll(text)

    return buildAnnotatedString {

        var cursorPosition = 0

        val codeSnippetBackground = MaterialTheme.colorScheme.surface

        for (token in tokens) {
            append(text.slice(cursorPosition until token.range.first))

            val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                matchResult = token,
                colorScheme = MaterialTheme.colorScheme,
                codeSnippetBackground = codeSnippetBackground
            )
            append(annotatedString)

            if (stringAnnotation != null) {
                val (item, start, end, tag) = stringAnnotation
                addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
            }

            cursorPosition = token.range.last + 1
        }

        if (!tokens.none()) {
            append(text.slice(cursorPosition..text.lastIndex))
        } else {
            append(text)
        }
    }
}

/**
 * Map regex matches found in a message with supported syntax symbols
 *
 * @param matchResult is a regex result matching our syntax symbols
 * @return pair of AnnotatedString with annotation (optional) used inside the ClickableText wrapper
 */
private fun getSymbolAnnotation(
    matchResult: MatchResult,
    colorScheme: ColorScheme,
    codeSnippetBackground: Color
): SymbolAnnotation {
    return when (matchResult.value.first()) {
        '*' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('*'),
                spanStyle = SpanStyle(
                    fontWeight = if (matchResult.value.length == 2) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (matchResult.value.length == 2) FontStyle.Normal else FontStyle.Italic
                )
            ),
            null
        )
        '_' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('_'),
                spanStyle = SpanStyle(fontStyle = FontStyle.Italic)
            ),
            null
        )
        '~' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('~'),
                spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
            ),
            null
        )
        '`' -> {
            val snippetText = matchResult.value.removeSurrounding("`")
            SymbolAnnotation(
                AnnotatedString(
                    text = snippetText,
                    spanStyle = SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        background = codeSnippetBackground,
                        baselineShift = BaselineShift(0.2f)
                    )
                ),
                null
            )
        }
        '<' -> {
            val numericalId = matchResult.value.substringAfter("<@").substringBefore(">")
            Log.d("MarkdownText", "Numerical ID: $numericalId")
            SymbolAnnotation(
                AnnotatedString(
                    text = matchResult.value,
                    spanStyle = SpanStyle(
                        color = colorScheme.primary
                    )
                ),
                StringAnnotation(
                    item = numericalId,
                    start = matchResult.range.first,
                    end = matchResult.range.last,
                    tag = SymbolAnnotationType.USER.name
                )
            )
        }
        'h' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = colorScheme.primary
                )
            ),
            StringAnnotation(
                item = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.LINK.name
            )
        )
        else -> SymbolAnnotation(AnnotatedString(matchResult.value), null)
    }
}

@Composable
fun ProcessMessageText(messageText: String): AnnotatedString {
    val hyperlinkRegex = "<@(\\d+)>".toRegex()
    val italicRegex = "\\*(.*?)\\*".toRegex()
    val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
    val strikethroughRegex = "~~(.*?)~~".toRegex()

    val matches = mutableListOf<MatchResult>()

    matches.addAll(hyperlinkRegex.findAll(messageText))
    matches.addAll(italicRegex.findAll(messageText))
    matches.addAll(boldRegex.findAll(messageText))
    matches.addAll(strikethroughRegex.findAll(messageText))

    matches.sortBy { it.range.first }

    var currentIndex = 0
    var processedText = ""

    matches.forEach { match ->
        val startIndex = match.range.first
        val endIndex = match.range.last + 1
        if(startIndex < currentIndex) return@forEach
        // Append the text before the formatting tag
        processedText += messageText.substring(currentIndex, startIndex)

        // Process the matched tag
        val tag = match.value
        val content = match.groupValues[1]

        processedText += when (tag) {
            in hyperlinkRegex.pattern -> processHyperlink(content)
            in italicRegex.pattern -> processItalic(content)
            in boldRegex.pattern -> processBold(content)
            in strikethroughRegex.pattern -> processStrikethrough(content)
            else -> content // Should not reach here, but include for safety
        }

        currentIndex = endIndex
    }

    // Append the remaining text
    processedText += messageText.substring(currentIndex)

    // Display the processed text
    return buildAnnotatedString { append(processedText) }
}

@Composable
fun processHyperlink(content: String): AnnotatedString {
    val id = content.trim()
    val username = "test"

    return buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(username)
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
            append(" ")
            append(id)
        }
    }
}

@Composable
fun processItalic(content: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
            append(content)
        }
    }
}

@Composable
fun processBold(content: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(content)
        }
    }
}

@Composable
fun processStrikethrough(content: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
            append(content)
        }
    }
}
