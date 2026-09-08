package com.kernel94.pulsoti.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rol_cache")
data class RolEntity(
    @PrimaryKey val id: Int,
    val nombre: String
)
