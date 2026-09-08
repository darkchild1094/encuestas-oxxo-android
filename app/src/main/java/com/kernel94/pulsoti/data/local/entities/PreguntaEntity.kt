package com.kernel94.pulsoti.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pregunta_cache",
    indices = [Index(value = ["cuestionarioId"])]
)
data class PreguntaEntity(
    @PrimaryKey val id: Int,
    val cuestionarioId: Int,
    val texto: String,
    val orden: Int
)
