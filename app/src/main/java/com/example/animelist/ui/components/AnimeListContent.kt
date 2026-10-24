package com.example.animelist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animelist.R
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection

@Composable
fun AnimeListContent(
    sections: List<AnimeSection>,
    onAnimeClick: (AnimeItem) -> Unit,
    onSeeAllClick: (AnimeSection) -> Unit,
    modifier: Modifier = Modifier,
    headerContent: (@Composable () -> Unit)? = null,
    listState: LazyListState,
    getRawState: (Int) -> LazyListState
) {
    LazyColumn (
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        if(headerContent != null) {
            item {
                headerContent()
            }
        }
        items(
            items = sections
        ) { section ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeeAllClick(section) },
                    horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onSeeAllClick(section) }
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 4.dp
                            )
                    )
                    IconButton(onClick = {onSeeAllClick(section)}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                            contentDescription = stringResource(R.string.next),
                            tint = Color.White
                        )
                    }
                }


                LazyRow(
                    state = getRawState(section.id),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    val previewItems = section.items.take(8)
                    items(
                        items = previewItems
                    ) { animeItem ->
                        AnimeCard(
                            animeItem = animeItem,
                            onAnimeClick = onAnimeClick
                        )
                    }
                }
            }
        }
    }
}