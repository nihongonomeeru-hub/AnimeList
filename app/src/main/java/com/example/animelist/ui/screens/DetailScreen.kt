package com.example.animelist.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animelist.R
import com.example.animelist.ui.model.AnimeItem
import com.example.animelist.view_model.FavoriteViewModel

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    animeItem: AnimeItem,
    onBackClick: () -> Unit,
    viewModel: FavoriteViewModel,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
        ) {
            Image(
                painter = painterResource(animeItem.imageResourceId),
                contentDescription = stringResource(animeItem.stringResourceId),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x660F0F0F),
                                Color(0xFF0F0F0F)
                            ),
                            startY = 100f
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp)
                ) {

                    val isFavorite = viewModel.isFavorite(animeItem)

                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(animeItem)
                        }
                    ) {
                        Icon(
                            imageVector = if(isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.to_mylist),
                            tint = if(isFavorite) Color.Red else Color.Gray
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(animeItem.stringResourceId),
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

                Text(
                    text = "全24話",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Text(
                    text = "2024年",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "あらすじ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Текст синопсиса
            Text(
                text = "コンビニからの帰り道、突如として異世界へと召喚されてしまった少年・菜月昴。無力な彼が手にした唯一の力は、死して時間を巻き戻す《死に戻り》の力だった。大切な人たちを守るため、過酷な運命に立ち向かっていく。",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCCCCCC),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Column() {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val genres = listOf("異世界", "ファンタジー", "ドラマ")
                    genres.forEach { genre ->
                        Surface(
                            color = Color(0xFF1E1E1E),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = genre,
                                color = Color(0xFFB0B0B0),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}