package com.example.animelist.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromGenreList(genres: List<String>): String {
        return genres.joinToString(",")
    }

    @TypeConverter
    fun toGenreList(data: String): List<String> {
        return if (data.isBlank()) emptyList() else data.split(",")
    }
}