package com.example.animelist.ui.screens.main_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animelist.R
import com.example.animelist.ui.components.contents.AnimeGridContent
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.view_model.SearchViewModel
import com.example.animelist.view_model.uiState.SearchUIState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

private val GENRE_LIST = listOf(
    "アクション" to "Action",
    "コメディ" to "Comedy",
    "ドラマ" to "Drama",
    "ファンタジー" to "Fantasy",
    "日常" to "Slice of Life",
    "ロマンス" to "Romance",
    "SF" to "Sci-Fi",
    "サスペンス" to "Mystery",
    "ホラー" to "Horror",
    "冒険" to "Adventure"
)

@Composable
fun SearchScreen(
    onAnimeClick: (AnimeItem) -> Unit,
    onLongClick: (AnimeItem) -> Unit,
    modifier: Modifier = Modifier,
    searchView: SearchViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val queryText by searchView.searchQuery.collectAsState()
    val state = searchView.uiState
    val selectedGenre = searchView.selectedGenre

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.search),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = queryText,
            onValueChange = { query -> searchView.updateQuery(query) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "作品名、キーワードで検索",
                    color = Color(0xFF6E6E73),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF8E8E93)
                )
            },
            trailingIcon = {
                if (queryText.isNotEmpty()) {
                    IconButton(onClick = { searchView.clearQuery() }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color(0xFF8E8E93)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1C1C1E),
                unfocusedContainerColor = Color(0xFF1C1C1E),
                focusedBorderColor = Color(0xFF3A3A3C),
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(GENRE_LIST) { (label, apiGenre) ->
                val isSelected = apiGenre == selectedGenre

                GenreChip(
                    label = label,
                    isSelected = isSelected,
                    onClick = {
                        focusManager.clearFocus()
                        searchView.searchByGenre(apiGenre)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                when (state) {
                    is SearchUIState.Loading -> {
                        Box(
                            modifier = modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    is SearchUIState.Default -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "人気のアニメ",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )

                            AnimeGridContent(
                                animeList = state.trendingAnime,
                                onLongClick = onLongClick,
                                onAnimeClick = onAnimeClick,
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }

                    is SearchUIState.Success -> {
                        if (state.animeList.isEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color(0xFF3A3A3C),
                                    modifier = Modifier.size(56.dp)
                                )
                                Text(
                                    text = "該当する作品が見つかりません",
                                    color = Color(0xFF6E6E73),
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Spacer(modifier = Modifier.height(16.dp))

                                val genreTitle = GENRE_LIST.find { it.second == selectedGenre }?.first
                                    ?:if(queryText.isNotBlank()) "「$queryText」の検索結果" else "検索結果"

                                Text(
                                    text = genreTitle,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                                AnimeGridContent(
                                    animeList = state.animeList,
                                    onAnimeClick = onAnimeClick,
                                    onLongClick = onLongClick,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }

                        }
                    }

                    is SearchUIState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = state.message, color = Color.Red, fontSize = 14.sp)
                        }
                    }
                }
            }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GenreChip(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
    isSelected: Boolean = false,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.5.dp
        ),
        modifier = modifier
    )
}