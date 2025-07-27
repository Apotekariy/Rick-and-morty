package com.example.rickandmorty.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.rickandmorty.R
import com.example.rickandmorty.domain.CharacterFilterOptions
import com.example.rickandmorty.domain.CharacterGender
import com.example.rickandmorty.domain.CharacterStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentFilter: CharacterFilterOptions,
    onApply: (CharacterFilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentFilter.name ?: "") }
    var selectedStatus by remember { mutableStateOf(currentFilter.status) }
    var species by remember { mutableStateOf(currentFilter.species ?: "") }
    var type by remember { mutableStateOf(currentFilter.type ?: "") }
    var selectedGender by remember { mutableStateOf(currentFilter.gender) }

    var showStatusDropdown by remember { mutableStateOf(false) }
    var showGenderDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Characters") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Name filter
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text("e.g. Rick, Morty") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Status filter
                ExposedDropdownMenuBox(
                    expanded = showStatusDropdown,
                    onExpandedChange = { showStatusDropdown = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedStatus?.displayName ?: "Any Status",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown)
                        },
                        colors = OutlinedTextFieldDefaults.colors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Any Status") },
                            onClick = {
                                selectedStatus = null
                                showStatusDropdown = false
                            }
                        )
                        CharacterStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    color = when (status) {
                                                        CharacterStatus.ALIVE -> Color.Green
                                                        CharacterStatus.DEAD -> Color.Red
                                                        CharacterStatus.UNKNOWN -> Color.Gray
                                                    },
                                                    shape = CircleShape
                                                )
                                        )
                                        Text(status.displayName)
                                    }
                                },
                                onClick = {
                                    selectedStatus = status
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }

                // Species filter
                OutlinedTextField(
                    value = species,
                    onValueChange = { species = it },
                    label = { Text("Species") },
                    placeholder = { Text("e.g. Human, Alien") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Type filter
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    placeholder = { Text("e.g. Genetic experiment") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Gender filter
                ExposedDropdownMenuBox(
                    expanded = showGenderDropdown,
                    onExpandedChange = { showGenderDropdown = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedGender?.displayName ?: "Any Gender",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderDropdown)
                        },
                        colors = OutlinedTextFieldDefaults.colors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = showGenderDropdown,
                        onDismissRequest = { showGenderDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Any Gender") },
                            onClick = {
                                selectedGender = null
                                showGenderDropdown = false
                            }
                        )
                        CharacterGender.entries.forEach { gender ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                when (gender) {
                                                    CharacterGender.MALE -> R.drawable.outline_male_24
                                                    CharacterGender.FEMALE -> R.drawable.outline_female_24
                                                    CharacterGender.GENDERLESS -> R.drawable.outline_agender_24
                                                    CharacterGender.UNKNOWN -> R.drawable.outline_question_mark_24
                                                }
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(gender.displayName)
                                    }
                                },
                                onClick = {
                                    selectedGender = gender
                                    showGenderDropdown = false
                                }
                            )
                        }
                    }
                }

                // Summary of active filters
                if (name.isNotBlank() || selectedStatus != null || species.isNotBlank() ||
                    type.isNotBlank() || selectedGender != null) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Active Filters:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (name.isNotBlank()) {
                                Text("• Name: $name", style = MaterialTheme.typography.bodySmall)
                            }
                            if (selectedStatus != null) {
                                Text("• Status: ${selectedStatus!!.displayName}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (species.isNotBlank()) {
                                Text("• Species: $species", style = MaterialTheme.typography.bodySmall)
                            }
                            if (type.isNotBlank()) {
                                Text("• Type: $type", style = MaterialTheme.typography.bodySmall)
                            }
                            if (selectedGender != null) {
                                Text("• Gender: ${selectedGender!!.displayName}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApply(
                        CharacterFilterOptions(
                            name = name.takeIf { it.isNotBlank() },
                            status = selectedStatus,
                            species = species.takeIf { it.isNotBlank() },
                            type = type.takeIf { it.isNotBlank() },
                            gender = selectedGender
                        )
                    )
                }
            ) {
                Text("Apply Filters")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}