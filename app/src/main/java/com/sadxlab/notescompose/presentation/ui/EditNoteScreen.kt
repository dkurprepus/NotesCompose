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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.presentation.UiEvent
import com.sadxlab.notescompose.presentation.debouncedClick
import com.sadxlab.notescompose.presentation.viewmodel.NoteViewModel
import com.sadxlab.notescompose.ui.theme.NoteColorPalette
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    navController: NavController,
    noteId: Int,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadNoteById(noteId)
    }
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            isSaving = false

            when (event) {
                is UiEvent.SaveSuccess -> {
                    navController.popBackStack()
                }

                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val note by viewModel.editingNote.collectAsState()

    val contentFocusRequester = remember { FocusRequester() }
    val titleFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

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


            val  noteColors = NoteColorPalette

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


                        val isTitleChanged = title != noteValue.title
                        val isContentChanged = content != noteValue.content
                        val isColorChanged = selectedColor != noteValue.color



                        debouncedClick {
                            if (!isTitleChanged && !isContentChanged && !isColorChanged) {
                                isSaving = false
                                Toast.makeText(context, "No changes made", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            isSaving = true
                            viewModel.updateNote(
                                Note(
                                    id = noteId,
                                    title = title,
                                    content = content,
                                    color = selectedColor,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }

                    }) {

                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
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
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { contentFocusRequester.requestFocus() }),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(titleFocusRequester)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .focusRequester(contentFocusRequester)
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
                                        width = if (selectedColor == color.toArgb()) 2.dp else 1.dp,
                                        color = if (selectedColor == color.toArgb()) Color.Black else Color.Gray,
                                        shape = CircleShape,
                                    )
                                    .clickable {
                                        selectedColor = color.toArgb()
                                    }
                            ) {
                                if (selectedColor == color.toArgb()) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected Color",
                                        tint = Color.Black,
                                        modifier = Modifier.align(alignment = Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    val formattedDate = remember(noteValue.timestamp) {
                        SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(
                            Date(
                                noteValue.timestamp
                            )
                        )
                    }
                    Text(
                        text = "Last edited: $formattedDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )


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
                                showDeleteDialog = false
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