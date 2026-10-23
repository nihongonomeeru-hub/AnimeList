package com.example.animelist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.ui.model.AnimeSection
import com.example.animelist.view_model.FavoriteViewModel

@Composable
fun AnimeListContent(
    sections: List<AnimeSection>,
    onAnimeClick: (AnimeItem) -> Unit,
    modifier: Modifier = Modifier
    ) {
    LazyColumn (
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        items(
            items = sections,
            key = {section -> section.type.typeRes}
        ) { section ->
            Column {
                Text(
                    text = stringResource(section.type.typeRes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 4.dp
                        )
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(
                        items = section.items,
                        key = {animeItem -> animeItem.stringResourceId}
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