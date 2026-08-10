package com.lineuplab.app.ui.lineup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.lineuplab.app.data.local.entity.FormationEntity
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.domain.model.FormationType
import com.lineuplab.app.domain.sport.SoccerConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineupBuilderScreen(teamId: Long, onBack: () -> Unit) {
    val container = (LocalContext.current.applicationContext as LineupLabApplication).container
    val viewModel: LineupBuilderViewModel = viewModel(
        key = "lineup-builder-$teamId",
        factory = LineupBuilderViewModel.factory(teamId, container),
    )

    val team by viewModel.team.collectAsStateWithLifecycle()
    val players by viewModel.players.collectAsStateWithLifecycle()
    val availableFormations by viewModel.availableFormations.collectAsStateWithLifecycle()
    val selectedFormation by viewModel.selectedFormation.collectAsStateWithLifecycle()
    val slots by viewModel.formationSlots.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()

    var pickingPosition by remember { mutableStateOf<Int?>(null) }
    var showFormationPicker by remember { mutableStateOf(false) }
    var showSavePreset by remember { mutableStateOf(false) }
    var showSetLineup by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(team?.name ?: "Lineup")
                        Text(
                            text = selectedFormation?.name ?: "Choose a formation",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showFormationPicker = true }) { Text("Formation") }
                    TextButton(
                        enabled = assignments.isNotEmpty(),
                        onClick = { showSavePreset = true },
                    ) { Text("Save Preset") }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Set Lineup") },
                icon = {},
                onClick = { if (assignments.isNotEmpty()) showSetLineup = true },
            )
        },
    ) { padding ->
        if (slots.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            SoccerFieldView(
                slots = slots,
                assignments = assignments,
                onPositionClick = { position -> pickingPosition = position },
                modifier = Modifier.fillMaxSize().padding(padding),
            )
        }
    }

    pickingPosition?.let { position ->
        PlayerPickerDialog(
            positionNumber = position,
            players = players,
            hasAssignment = assignments.containsKey(position),
            onDismiss = { pickingPosition = null },
            onPick = { playerName ->
                viewModel.assignPlayer(position, playerName)
                pickingPosition = null
            },
            onClear = {
                viewModel.clearPosition(position)
                pickingPosition = null
            },
        )
    }

    if (showFormationPicker) {
        FormationPickerDialog(
            formations = availableFormations,
            selectedId = selectedFormation?.id,
            onDismiss = { showFormationPicker = false },
            onSelect = { formationId ->
                viewModel.selectFormation(formationId)
                showFormationPicker = false
            },
        )
    }

    if (showSavePreset) {
        SavePresetDialog(
            onDismiss = { showSavePreset = false },
            onSave = { name ->
                viewModel.savePreset(name) { feedback = "Saved \"$name\" as a preset." }
                showSavePreset = false
            },
        )
    }

    if (showSetLineup) {
        SetLineupDialog(
            assignments = assignments,
            onDismiss = { showSetLineup = false },
            onConfirm = { notes ->
                viewModel.setLineup(notes) { feedback = "Lineup recorded." }
                showSetLineup = false
            },
        )
    }

    feedback?.let { message ->
        AlertDialog(
            onDismissRequest = { feedback = null },
            title = { Text("Done") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { feedback = null }) { Text("OK") } },
        )
    }
}

@Composable
private fun PlayerPickerDialog(
    positionNumber: Int,
    players: List<PlayerEntity>,
    hasAssignment: Boolean,
    onDismiss: () -> Unit,
    onPick: (String) -> Unit,
    onClear: () -> Unit,
) {
    val mapping = SoccerConfig.mappingFor(positionNumber)
    val sorted = remember(players, positionNumber) {
        players.sortedBy { if (it.defaultPosition == positionNumber) 0 else 1 }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Position $positionNumber — ${mapping?.label ?: ""}") },
        text = {
            if (sorted.isEmpty()) {
                Text("No players on the roster yet.")
            } else {
                Column(modifier = Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                    sorted.forEach { player ->
                        val defaultMapping = SoccerConfig.mappingFor(player.defaultPosition)
                        Text(
                            text = "${player.name} (default: ${defaultMapping?.abbreviation ?: player.defaultPosition})",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPick(player.name) }
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (hasAssignment) {
                TextButton(onClick = onClear) { Text("Clear") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun FormationPickerDialog(
    formations: List<FormationEntity>,
    selectedId: Long?,
    onDismiss: () -> Unit,
    onSelect: (Long) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Formation") },
        text = {
            Column(modifier = Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                formations.forEach { formation ->
                    val kind = if (formation.type == FormationType.STANDARD) "Standard" else "Custom"
                    val marker = if (formation.id == selectedId) "✓ " else ""
                    Text(
                        text = "$marker${formation.name} ($kind)",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(formation.id) }
                            .padding(vertical = 12.dp),
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

@Composable
private fun SavePresetDialog(onDismiss: () -> Unit, onSave: (name: String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save as Preset") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Preset name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = { onSave(name.trim()) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun SetLineupDialog(
    assignments: Map<Int, String>,
    onDismiss: () -> Unit,
    onConfirm: (notes: String?) -> Unit,
) {
    var notes by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Lineup") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                assignments.toSortedMap().forEach { (position, playerName) ->
                    val mapping = SoccerConfig.mappingFor(position)
                    Text("$playerName → Position $position (${mapping?.label ?: "Unknown"})")
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(notes.trim().ifBlank { null }) }) { Text("Confirm & Record") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
