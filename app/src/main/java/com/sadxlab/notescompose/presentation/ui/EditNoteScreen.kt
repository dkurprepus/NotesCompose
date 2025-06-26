package com.sadxlab.notescompose.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.presentation.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    navController: NavController,
    noteId: Int,
    viewModel: NoteViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadNoteById(noteId)
    }

    val note by viewModel.editingNote.collectAsState()



    when (val noteValue = note) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            var title by rememberSaveable { mutableStateOf(noteValue.title) }
            var content by rememberSaveable { mutableStateOf(noteValue.content) }
            var selectedColor by rememberSaveable { mutableStateOf(noteValue.color) }

            // rest of UI
            var showDeleteDialog by remember { mutableStateOf(false) }

            val context = LocalContext.current

            val noteColors = listOf(
                Color(0xFFFFF59D), // Light Yellow
                Color(0xFFB2EBF2), // Light Cyan
                Color(0xFFC8E6C9), // Light Green
                Color(0xFFFFCDD2), // Light Red/Pink
                Color(0xFFD1C4E9), // Lavender
                Color(0xFFFFE0B2), // Light Orange
                Color(0xFFF8BBD0)  // Light Pink
            )

            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Edit Note") }, actions = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {
                                    showDeleteDialog = true
                                })
                    })
                }, floatingActionButton = {
                    FloatingActionButton(onClick = {
                        if (title.isBlank() || content.isBlank()) {
                            Toast.makeText(
                                context,
                                "Title and content can not be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@FloatingActionButton
                        }
                        viewModel.updateNote(
                            Note(
                                id = noteId,
                                title = title,
                                content = content,
                                color = selectedColor
                            )
                        )
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Note Color", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        noteColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedColor == color.toArgb()) 3.dp else 1.dp,
                                        color = if (selectedColor == color.toArgb()) Color.Black else Color.Gray,
                                        shape = CircleShape,
                                    )
                                    .clickable {
                                        selectedColor = color.toArgb()
                                    }
                            ) { }
                        }
                    }
                }
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Note ?") },
                    text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(

                            onClick = {
                                viewModel.deleteNote(noteValue)
                                showDeleteDialog = false
                                navController.popBackStack()
                            }
                        ) {
                            Text("Delete", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                               showDeleteDialog=false
                            }
                        ) {
                            Text("Cancel")
                        }


                    }
                )
            }
        }
    }


}