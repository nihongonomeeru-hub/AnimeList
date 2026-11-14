package com.example.animelist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.animelist.graphql.type.MediaType
import com.example.animelist.ui.components.BioWithSpoilers
import com.example.animelist.view_model.PersonDetailViewModel
import com.example.animelist.view_model.uiState.CharacterDetailUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBackClick: () -> Unit,
    onVoiceActorClick: (Int) -> Unit,
    onMediaClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier,
    onCloseAllClick: () -> Unit,
    showCloseAll: Boolean = false,
    personViewModel: PersonDetailViewModel = viewModel()
) {
    val scrollState = key(characterId) { rememberScrollState() }
    val uiState by personViewModel.characterState.collectAsStateWithLifecycle()

    LaunchedEffect(characterId) {
        personViewModel.loadCharacterDetail(characterId)
    }

    when (val state = uiState) {
        is CharacterDetailUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0F0F)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        is CharacterDetailUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0F0F))
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.message, color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { personViewModel.loadCharacterDetail(characterId) }) {
                    Text("再読み込み")
                }
            }
        }

        is CharacterDetailUiState.Success -> {
            val character = state.character
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0F0F))
                    .verticalScroll(scrollState)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る",
                            tint = Color.White
                        )
                    }

                    if (showCloseAll) {
                        IconButton(onClick = onCloseAllClick) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "すべて閉じる",
                                tint = Color.White
                            )
                        }
                    }
                }
                // Avatar + Name + Meta
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = character.imageUrl,
                        contentDescription = character.nativeName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1E1E))
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = character.nativeName,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (character.fullName.isNotEmpty() && character.fullName != character.nativeName) {
                            Text(
                                text = character.fullName,
                                color = Color(0xFF8E8E93),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        val metaChips = listOfNotNull(
                            character.gender,
                            character.age?.let { "年齢: $it" },
                            character.birthDate?.let { "誕生日: $it" },
                            character.bloodType
                        )
                        if (metaChips.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                metaChips.forEach { label ->
                                    Surface(
                                        color = Color(0xFF1E1E1E),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            color = Color(0xFFCCCCCC),
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 声優
                if (character.voiceActors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "担当声優 (CV)",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(character.voiceActors, key = { it.id }) { va ->
                            Surface(
                                color = Color(0xFF1A1A1C),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onVoiceActorClick(va.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    AsyncImage(
                                        model = va.imageUrl,
                                        contentDescription = va.nativeName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2C2C2E))
                                    )
                                    Column {
                                        Text(
                                            text = va.nativeName,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (va.fullName.isNotEmpty()) {
                                            Text(
                                                text = va.fullName,
                                                color = Color(0xFF8E8E93),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bio
                if (character.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "プロフィール",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BioWithSpoilers(
                        rawDescription = character.description,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // appearance anime
                if (character.appearances.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "出演作品",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = character.appearances,
                            key = { "${it.mediaType}_${it.mediaId}" }
                        ) { app ->
                            Column(
                                modifier = Modifier
                                    .width(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onMediaClick(app.mediaId, app.mediaType) }
                            ) {
                                AsyncImage(
                                    model = app.mediaImageUrl,
                                    contentDescription = app.mediaTitle,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f / 3f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E1E1E))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = app.roleLabel,
                                    color = Color(0xFF8E8E93),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = app.mediaTitle,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}