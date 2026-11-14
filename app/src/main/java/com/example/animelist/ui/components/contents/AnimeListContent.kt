package com.example.animelist.ui.components.contents

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animelist.R
import com.example.animelist.ui.components.AnimeCard
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection

private const val SECTION_PREVIEW_LIMIT = 8

@Composable
fun AnimeListContent(
    sections: List<AnimeSection>,
    modifier: Modifier = Modifier,
    onAnimeClick: (AnimeItem) -> Unit,
    onLongClick: ((AnimeItem) -> Unit)? = null,
    onSeeAllClick: (AnimeSection) -> Unit,
    headerContent: (@Composable () -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
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
            items = sections,
            key = {section -> section.id}
        ) { section ->
            val previewItems = remember(section.items) {
                section.items.take(SECTION_PREVIEW_LIMIT)
            }
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
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 4.dp
                            )
                    )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                            contentDescription = stringResource(R.string.next),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                }


                LazyRow(
                    state = getRawState(section.id),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(
                        items = previewItems,
                         key = {anime -> anime.id}
                    ) { animeItem ->
                        AnimeCard(
                            animeItem = animeItem,
                            onAnimeClick = onAnimeClick,
                            onLongClick = onLongClick?.let { {it(animeItem)} }
                        )
                    }
                }
            }
        }
    }
}