package com.sadxlab.notescompose.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.presentation.viewmodel.NoteViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.sadxlab.notescompose.core.utils.milliSecondsToTime
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val notes by viewModel.notes
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
            TopAppBar(title = { Text("Notes") })
        }, floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addNote")
            }) { Icon(Icons.Default.Add, contentDescription = null) }
        }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note ->
                    NoteItem(
                        note = note,
                        onClick = { navController.navigate("editNote/${note.id}") },
                        onDelete = {
                            noteToDelete = note
                        })
                }
            }

            noteToDelete?.let { note ->

                AlertDialog(
                    onDismissRequest = { noteToDelete = null },
                    title = { Text("Delete this note?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteNote(note)
                            noteToDelete = null
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Note deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.addNote(note)
                                }


                            }

                        }) { Text("Delete") }
                    }, dismissButton = {
                        TextButton(onClick = { noteToDelete = null }) {
                            Text("Cancel")
                        }

                    })


            }
            LaunchedEffect(Unit) {
                viewModel.loadNotes()
            }
        }
    }


}