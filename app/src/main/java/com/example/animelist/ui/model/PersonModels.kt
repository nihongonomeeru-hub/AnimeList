package com.example.animelist.ui.model

import com.example.animelist.graphql.type.MediaType

data class CharacterDetailUIModel(
    val id: Int,
    val nativeName: String,
    val fullName: String,
    val imageUrl: String,
    val gender: String?,
    val birthDate: String?,
    val age: String?,
    val bloodType: String?,
    val description: String,
    val voiceActors: List<VoiceActorSummary>,
    val appearances: List<CharacterAppearanceItem>
)

data class VoiceActorSummary(
    val id: Int,
    val nativeName: String,
    val fullName: String,
    val imageUrl: String,
    val language: String
)

data class CharacterAppearanceItem(
    val mediaId: Int,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaImageUrl: String,
    val roleLabel: String, // "メイン", "サブ", "背景"
    val rating: Double?
)

data class StaffDetailUIModel(
    val id: Int,
    val nativeName: String,
    val fullName: String,
    val imageUrl: String,
    val homeTown: String?,
    val birthDate: String?,
    val age: String?,
    val bloodType: String?,
    val yearsActive: String?,
    val description: String,
    val roles: List<StaffRoleItem>
)

data class StaffRoleItem(
    val characterId: Int,
    val characterName: String,
    val characterImageUrl: String,
    val roleLabel: String,
    val mediaId: Int,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaImageUrl: String
)