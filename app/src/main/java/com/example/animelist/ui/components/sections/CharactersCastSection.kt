package com.example.animelist.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.animelist.ui.components.CharacterCastCard
import com.example.animelist.view_model.uiState.CharactersCastUiState

@Composable
fun CharactersCastSection(
    modifier: Modifier = Modifier,
    castUiState: CharactersCastUiState,
    onCharacterClick: (Int) -> Unit = {},
    onVoiceActorClick: (Int) -> Unit = {}
) {
    when(val state = castUiState) {
        is CharactersCastUiState.Success -> {
            if (state.castList.isNotEmpty()) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "キャスト・登場人物",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(
                            items = state.castList,
                            key = {item -> "${item.characterName}_${item.voiceActorName}"}
                        ) { item ->
                            CharacterCastCard(
                                characterName = item.characterName,
                                characterImageUrl = item.characterImageUrl,
                                actorName = item.voiceActorName,
                                actorImageUrl = item.voiceActorImageUrl,
                                onCharacterClick = {  onCharacterClick(item.characterId) },
                                onActorClick = { item.voiceActorId?.let(onVoiceActorClick) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
        is CharactersCastUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        is CharactersCastUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message, color = Color.Gray)
            }
        }
    }
}