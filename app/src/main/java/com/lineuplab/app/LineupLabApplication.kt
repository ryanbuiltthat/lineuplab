package com.lineuplab.app

import android.app.Application
import com.lineuplab.app.data.local.DatabaseSeeder
import com.lineuplab.app.data.local.LineupLabDatabase
import com.lineuplab.app.data.repository.FormationRepository
import com.lineuplab.app.data.repository.LineupRepository
import com.lineuplab.app.data.repository.PlayingHistoryRepository
import com.lineuplab.app.data.repository.TeamRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manual dependency container. Small enough for the MVP; swap for Hilt if
 * the graph grows.
 */
class AppContainer(application: Application) {
    val database: LineupLabDatabase = LineupLabDatabase.getInstance(application)

    val teamRepository by lazy { TeamRepository(database.teamDao(), database.sportDao()) }
    val formationRepository by lazy { FormationRepository(database.formationDao()) }
    val lineupRepository by lazy { LineupRepository(database.lineupDao()) }
    val playingHistoryRepository by lazy { PlayingHistoryRepository(database.playingHistoryDao()) }
}

class LineupLabApplication : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        applicationScope.launch {
            DatabaseSeeder(container.database).seed()
        }
    }
}
