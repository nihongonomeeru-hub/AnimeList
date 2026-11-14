package com.example.animelist.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animelist.ui.model.AnimeItem

private val HtmlTagRegex = Regex("<[^>]*>")
private val MultipleSpacesRegex = Regex("\\s{2,}")

@Composable
fun AnimeInfoSection(
    animeItem: AnimeItem,
    modifier: Modifier = Modifier
) {
    val cleanDescription = remember(animeItem.description) {
        animeItem.description
            .replace(HtmlTagRegex, " ")
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&amp;", "&")
            .replace(MultipleSpacesRegex, " ")
            .trim()
            .ifEmpty { "あらすじはありません" }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = animeItem.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = Color(0xFF222222),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = animeItem.rating?.toString() ?: "—",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            val episodeOrStatusText = if (animeItem.mediaType == "MANGA") {
                when {
                    animeItem.volumes != null && animeItem.chapters != null ->
                        "全${animeItem.volumes}巻・${animeItem.chapters}話"
                    animeItem.volumes != null -> "全${animeItem.volumes}巻"
                    animeItem.chapters != null -> "全${animeItem.chapters}話"
                    animeItem.status == "FINISHED" -> "完結"
                    animeItem.status == "NOT_YET_RELEASED" -> "発売予定"
                    animeItem.status == "CANCELLED" -> "打ち切り"
                    animeItem.status == "HIATUS" -> "休載中"
                    else -> "連載中"
                }
            } else {
                when {
                    animeItem.status == "NOT_YET_RELEASED" -> "放送予定"
                    animeItem.episodes != null -> "${animeItem.episodes}話"
                    animeItem.status == "RELEASING" -> "放送中"
                    animeItem.status == "CANCELLED" -> "制作中止"
                    animeItem.status == "HIATUS" -> "休止中"
                    else -> "話数未定"
                }
            }

            Text(
                text = episodeOrStatusText,
                color = Color.Gray,
                fontSize = 13.sp
            )

            val yearText = if (animeItem.year != null) animeItem.year.toString()  else "—"
            Text(
                text =  yearText ,
                color = Color.Gray,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(animeItem.description.isNotEmpty()) {
            Text(
                text = "あらすじ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cleanDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCCCCCC),
                lineHeight = 22.sp
            )
        }
    }
}