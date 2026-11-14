package com.example.animelist.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animelist.R
import com.example.animelist.ui.model.AnimeItem

private val HeroTopScrimBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0x99000000),
        Color.Transparent
    )
)

private val HeroBottomScrimBrush =
    Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Transparent,
            Color(0xCC0F0F0F),
            Color(0xFF0F0F0F)
        )
    )


@Composable
fun HeroBanner(
    animeOfDay: AnimeItem?,
    onDetailsClick: (AnimeItem) -> Unit,
    modifier: Modifier = Modifier
) {

    if(animeOfDay == null) return
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(510.dp)
            .background(Color(0xFF1A1A1A))
    ) {
        AsyncImage(
            model = animeOfDay.imageUrl,
            contentDescription = animeOfDay.title,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.TopCenter)
                .background(HeroTopScrimBrush)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HeroBottomScrimBrush)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = animeOfDay.title,
                color = Color.White,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 14.dp)
            )
                OutlinedButton(
                    onClick = { onDetailsClick(animeOfDay) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.details),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
