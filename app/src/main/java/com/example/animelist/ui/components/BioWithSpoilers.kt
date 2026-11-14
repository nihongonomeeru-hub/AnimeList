package com.example.animelist.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SpoilerRegex = Regex("~!(.*?)!~", RegexOption.DOT_MATCHES_ALL)
private val HtmlTagRegex = Regex("<[^>]*>")

private sealed interface BioSegment {
    data class Normal(val text: String) : BioSegment
    data class Spoiler(val text: String) : BioSegment
}

@Composable
fun BioWithSpoilers(
    rawDescription: String,
    modifier: Modifier = Modifier
) {
    val segments = remember(rawDescription) {
        val cleaned = rawDescription
            .replace(HtmlTagRegex, " ")
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&amp;", "&")
            .trim()

        if (cleaned.isEmpty()) return@remember emptyList()

        val result = mutableListOf<BioSegment>()
        var lastIndex = 0
        for (match in SpoilerRegex.findAll(cleaned)) {
            if (match.range.first > lastIndex) {
                val normalText = cleaned.substring(lastIndex, match.range.first).trim()
                if (normalText.isNotEmpty()) result.add(BioSegment.Normal(normalText))
            }
            val spoilerText = match.groupValues[1].trim()
            if (spoilerText.isNotEmpty()) result.add(BioSegment.Spoiler(spoilerText))
            lastIndex = match.range.last + 1
        }
        if (lastIndex < cleaned.length) {
            val tail = cleaned.substring(lastIndex).trim()
            if (tail.isNotEmpty()) result.add(BioSegment.Normal(tail))
        }
        result
    }

    if (segments.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        segments.forEach { segment ->
            when (segment) {
                is BioSegment.Normal -> {
                    Text(
                        text = segment.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCCCCCC),
                        lineHeight = 22.sp
                    )
                }
                is BioSegment.Spoiler -> {
                    var revealed by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1C1C1E))
                            .clickable { revealed = !revealed }
                            .animateContentSize()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (revealed) "ネタバレ（タップで隠す）" else "ネタバレを表示（タップ）",
                            color = Color(0xFFE50914),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (revealed) {
                            Text(
                                text = segment.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCCCCCC),
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}