package com.lineuplab.app.data.local

import androidx.room.TypeConverter
import com.lineuplab.app.domain.model.FormationType

class Converters {

    @TypeConverter
    fun fromFormationType(type: FormationType): String = type.name

    @TypeConverter
    fun toFormationType(value: String): FormationType = FormationType.valueOf(value)
}
