package com.lineuplab.app.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lineuplab.app.LineupLabApplication
import com.lineuplab.app.data.local.entity.PlayingHistoryEntity
import com.lineuplab.app.domain.sport.SoccerConfig
import com.lineuplab.app.ui.theme.AccentAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerStatsScreen(teamId: Long, onBack: () -> Unit) {
    val container = (LocalContext.current.applicationContext as LineupLabApplication).container
    val viewModel: PlayerStatsViewModel = viewModel(
        key = "player-stats-$teamId",
        factory = PlayerStatsViewModel.factory(teamId, container),
    )
    val summaries by viewModel.playerSummaries.collectAsStateWithLifecycle()
    var historyPlayerName by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player Stats") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        if (summaries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No players yet. Add players from the roster screen.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(summaries, key = { it.player.id }) { summary ->
                    PlayerStatsCard(
                        summary = summary,
                        onViewHistory = { historyPlayerName = summary.player.name },
                    )
                }
            }
        }
    }

    historyPlayerName?.let { playerName ->
        PlayerHistoryDialog(
            playerName = playerName,
            viewModel = viewModel,
            onDismiss = { historyPlayerName = null },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlayerStatsCard(summary: PlayerStatsSummary, onViewHistory: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(summary.player.name, style = MaterialTheme.typography.titleMedium)
                    summary.player.jerseyNumber?.let {
                        Text(
                            text = "#$it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = "${summary.totalAppearances} appearances",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }

            if (summary.positionBreakdown.isEmpty()) {
                Text(
                    text = "No playing history yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            } else {
                FlowRow(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    summary.positionBreakdown.forEach { appearance ->
                        val mapping = SoccerConfig.mappingFor(appearance.positionNumber)
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                text = "${mapping?.abbreviation ?: appearance.positionNumber} × ${appearance.count}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
                TextButton(
                    onClick = onViewHistory,
                    modifier = Modifier.padding(top = 4.dp),
                ) { Text("View history") }
            }
        }
    }
}

@Composable
private fun PlayerHistoryDialog(
    playerName: String,
    viewModel: PlayerStatsViewModel,
    onDismiss: () -> Unit,
) {
    val history by remember(playerName) { viewModel.recentHistory(playerName) }
        .collectAsStateWithLifecycle(initialValue = emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$playerName — Recent Lineups") },
        text = {
            if (history.isEmpty()) {
                Text("No history recorded yet.")
            } else {
                Column {
                    history.forEachIndexed { index, record ->
                        HistoryRow(record)
                        if (index != history.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

@Composable
private fun HistoryRow(record: PlayingHistoryEntity) {
    val mapping = SoccerConfig.mappingFor(record.positionNumber)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = mapping?.let { "${it.abbreviation} — ${it.label}" } ?: "Position ${record.positionNumber}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = formatDate(record.dateTime),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        record.notes?.takeIf { it.isNotBlank() }?.let { notes ->
            Text(
                text = notes,
                style = MaterialTheme.typography.bodySmall,
                color = AccentAmber,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}
