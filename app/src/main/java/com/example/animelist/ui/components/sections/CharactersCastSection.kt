package com.example.animelist.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    castUiState: CharactersCastUiState
) {
    when(val state = castUiState) {
        is CharactersCastUiState.Success -> {
            if (state.castList.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "キャスト・登場人物",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8E8E93)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.castList) { item ->
                        CharacterCastCard(
                            characterName = item.characterName,
                            characterImageUrl = item.characterImageUrl,
                            actorName = item.voiceActorName,
                            actorImageUrl = item.voiceActorImageUrl,
                            onCharacterClick = { /* Действия при клике на персонажа */ },
                            onActorClick = { /* Действия при клике на сейю */ },
                            modifier = Modifier.width(135.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
        is CharactersCastUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        is CharactersCastUiState.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.message, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}