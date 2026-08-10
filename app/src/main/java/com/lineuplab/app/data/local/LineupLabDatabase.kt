package com.lineuplab.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lineuplab.app.data.local.dao.FormationDao
import com.lineuplab.app.data.local.dao.LineupDao
import com.lineuplab.app.data.local.dao.PlayingHistoryDao
import com.lineuplab.app.data.local.dao.SportDao
import com.lineuplab.app.data.local.dao.TeamDao
import com.lineuplab.app.data.local.entity.FormationEntity
import com.lineuplab.app.data.local.entity.LineupAssignmentEntity
import com.lineuplab.app.data.local.entity.LineupEntity
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.data.local.entity.PlayerPositionStatsEntity
import com.lineuplab.app.data.local.entity.PlayingHistoryEntity
import com.lineuplab.app.data.local.entity.SportEntity
import com.lineuplab.app.data.local.entity.TeamEntity

@Database(
    entities = [
        SportEntity::class,
        TeamEntity::class,
        PlayerEntity::class,
        FormationEntity::class,
        LineupEntity::class,
        LineupAssignmentEntity::class,
        PlayingHistoryEntity::class,
        PlayerPositionStatsEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class LineupLabDatabase : RoomDatabase() {

    abstract fun sportDao(): SportDao
    abstract fun teamDao(): TeamDao
    abstract fun formationDao(): FormationDao
    abstract fun lineupDao(): LineupDao
    abstract fun playingHistoryDao(): PlayingHistoryDao

    companion object {
        private const val DATABASE_NAME = "lineuplab.db"

        @Volatile
        private var instance: LineupLabDatabase? = null

        fun getInstance(context: Context): LineupLabDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LineupLabDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { instance = it }
            }
    }
}
