package com.example.animelist.ui.model

data class CharacterVoiceActorUIModel(
    val characterId: Int,
    val characterName: String,
    val characterRole: String,
    val characterImageUrl: String?,
    val voiceActorId: Int?,
    val voiceActorName: String?,
    val voiceActorImageUrl: String?,
    val language: String?
)