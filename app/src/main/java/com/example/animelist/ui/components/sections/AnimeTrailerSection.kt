package com.example.animelist.ui.components.sections

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.animelist.ui.model.TrailerItem
import androidx.core.net.toUri

@Composable
fun AnimeTrailerSection(
    trailer: TrailerItem?,
    modifier: Modifier = Modifier
) {
    if (trailer?.id == null || !trailer.site.equals("youtube", ignoreCase = true)) {
        return
    }

    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Official Trailer",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "公式PV・予告映像",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF8E8E93)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A1A))
                .clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.youtube.com/watch?v=${trailer.id}".toUri()
                    )
                    runCatching { context.startActivity(intent) }
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = trailer.thumbnailUrl,
                contentDescription = "Trailer preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )

            Surface(
                shape = CircleShape,
                color = Color(0xCCFF0000),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play trailer",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}