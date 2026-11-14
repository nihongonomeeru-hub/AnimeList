package com.example.animelist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.example.animelist.view_model.uiState.StaffDetailUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StaffDetailScreen(
    staffId: Int,
    onBackClick: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onMediaClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier,
    showCloseAll: Boolean = false,
    onCloseAllClick: () -> Unit,
    personViewModel: PersonDetailViewModel = viewModel()
) {
    val scrollState = key(staffId) { rememberScrollState() }
    val uiState by personViewModel.staffState.collectAsStateWithLifecycle()

    LaunchedEffect(staffId) {
        personViewModel.loadStaffDetail(staffId)
    }

    when (val state = uiState) {
        is StaffDetailUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0F0F)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        is StaffDetailUiState.Error -> {
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
                Button(onClick = { personViewModel.loadStaffDetail(staffId) }) {
                    Text("再読み込み")
                }
            }
        }

        is StaffDetailUiState.Success -> {
            val staff = state.staff
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
                // Photo + name + Meta
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = staff.imageUrl,
                        contentDescription = staff.nativeName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(115.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1E1E))
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = staff.nativeName,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (staff.fullName.isNotEmpty() && staff.fullName != staff.nativeName) {
                            Text(
                                text = staff.fullName,
                                color = Color(0xFF8E8E93),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        val metaChips = listOfNotNull(
                            staff.birthDate,
                            staff.age,
                            staff.homeTown?.let { "出身: $it" },
                            staff.bloodType,
                            staff.yearsActive?.let { "活動: $it" }
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

                // Bio
                if (staff.description.isNotEmpty()) {
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
                        rawDescription = staff.description,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Roles
                if (staff.roles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "主な出演キャラクター",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        staff.roles.forEach { role ->
                            Surface(
                                color = Color(0xFF18181A),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Character
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onCharacterClick(role.characterId) }
                                            .padding(end = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        AsyncImage(
                                            model = role.characterImageUrl,
                                            contentDescription = role.characterName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2C2C2E))
                                        )
                                        Column {
                                            Text(
                                                text = role.characterName,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = role.roleLabel,
                                                color = Color(0xFF8E8E93),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // anime/manga
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onMediaClick(role.mediaId, role.mediaType) }
                                            .padding(start = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = role.mediaTitle,
                                            color = Color(0xFFCCCCCC),
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            textAlign = TextAlign.End,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        )
                                        AsyncImage(
                                            model = role.mediaImageUrl,
                                            contentDescription = role.mediaTitle,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .width(36.dp)
                                                .height(50.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF2C2C2E))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}