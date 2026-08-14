package com.lineuplab.app.ui.team

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lineuplab.app.LineupLabApplication
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.domain.sport.SoccerConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamId: Long,
    onBack: () -> Unit,
    onBuildLineup: (Long) -> Unit,
    onViewStats: (Long) -> Unit = {},
) {
    val container = (LocalContext.current.applicationContext as LineupLabApplication).container
    val viewModel: TeamDetailViewModel = viewModel(
        key = "team-detail-$teamId",
        factory = TeamDetailViewModel.factory(teamId, container.teamRepository),
    )
    val team by viewModel.team.collectAsStateWithLifecycle()
    val players by viewModel.players.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPlayer by remember { mutableStateOf<PlayerEntity?>(null) }
    var deletingPlayer by remember { mutableStateOf<PlayerEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(team?.name ?: "Team") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onViewStats(teamId) }) {
                        Icon(Icons.Filled.BarChart, contentDescription = "Player stats")
                    }
                    IconButton(onClick = { onBuildLineup(teamId) }) {
                        Icon(Icons.Filled.SportsSoccer, contentDescription = "Build lineup")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add player")
            }
        },
    ) { padding ->
        if (players.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("No players yet. Tap + to add your roster.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(players, key = { it.id }) { player ->
                    PlayerRow(
                        player = player,
                        onClick = { editingPlayer = player },
                        onDelete = { deletingPlayer = player },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        PlayerFormDialog(
            initial = null,
            onDismiss = { showAddDialog = false },
            onSubmit = { name, position, jersey ->
                viewModel.addPlayer(name, position, jersey)
                showAddDialog = false
            },
        )
    }

    editingPlayer?.let { player ->
        PlayerFormDialog(
            initial = player,
            onDismiss = { editingPlayer = null },
            onSubmit = { name, position, jersey ->
                viewModel.updatePlayer(player.copy(name = name, defaultPosition = position, jerseyNumber = jersey))
                editingPlayer = null
            },
        )
    }

    deletingPlayer?.let { player ->
        AlertDialog(
            onDismissRequest = { deletingPlayer = null },
            title = { Text("Remove ${player.name}?") },
            text = { Text("This removes them from the roster. Past playing history is kept.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePlayer(player)
                    deletingPlayer = null
                }) { Text("Remove") }
            },
            dismissButton = { TextButton(onClick = { deletingPlayer = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun PlayerRow(player: PlayerEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    val mapping = SoccerConfig.mappingFor(player.defaultPosition)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = player.defaultPosition.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(player.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = mapping?.let { "${it.abbreviation} — ${it.label}" } ?: "Unknown position",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            player.jerseyNumber?.let { number ->
                Text(
                    text = "#$number",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove ${player.name}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerFormDialog(
    initial: PlayerEntity?,
    onDismiss: () -> Unit,
    onSubmit: (name: String, position: Int, jerseyNumber: Int?) -> Unit,
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var jersey by remember { mutableStateOf(initial?.jerseyNumber?.toString() ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPosition by remember {
        mutableStateOf(initial?.defaultPosition ?: SoccerConfig.positionMappings.first().number)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Player" else "Edit Player") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                )
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    val selectedMapping = SoccerConfig.mappingFor(selectedPosition)
                    OutlinedTextField(
                        value = selectedMapping?.let { "${it.number} — ${it.abbreviation} (${it.label})" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Position") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        SoccerConfig.positionMappings.forEach { mapping ->
                            DropdownMenuItem(
                                text = { Text("${mapping.number} — ${mapping.abbreviation} (${mapping.label})") },
                                onClick = {
                                    selectedPosition = mapping.number
                                    expanded = false
                                },
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = jersey,
                    onValueChange = { input -> if (input.all { it.isDigit() }) jersey = input },
                    label = { Text("Jersey number (optional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = { onSubmit(name.trim(), selectedPosition, jersey.toIntOrNull()) },
            ) { Text(if (initial == null) "Add" else "Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
