package com.example.animelist.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.animelist.ui.model.AnimeItem

@Composable
fun AnimeCard(
    animeItem: AnimeItem,
    onAnimeClick: (AnimeItem) -> Unit,
    modifier: Modifier = Modifier.width(135.dp),
    onLongClick: (() -> Unit)? = null
) {

    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "anime_card_scale"
    )


    val clickModifier = if (onLongClick != null) {
        Modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = ripple(),
            onClick = { onAnimeClick(animeItem) },
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick()
            }
        )
    } else {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(),
            onClick = { onAnimeClick(animeItem) }
        )
    }

    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .then(clickModifier)
    ) {
        AsyncImage(
            model = animeItem.imageUrl,
            contentDescription = animeItem.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        AnimeRate(
            score = animeItem.rating,
            modifier = Modifier.align(Alignment.TopEnd)
        )

    }
}