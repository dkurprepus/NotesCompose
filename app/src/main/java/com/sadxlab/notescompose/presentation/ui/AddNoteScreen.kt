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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel()
) {

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedColor by remember { mutableStateOf(Color(0xFFFFF59D).toArgb()) }
    val notesColor = NoteColorPalette
    val contentFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val clickLocked = remember { mutableStateOf(false) }
    var lastAddTime = 0L
    val debounceInterval = 1000L
    var isSaving by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.SaveSuccess -> {
                    isSaving = false
                    clickLocked.value = false
                    navController.popBackStack()
                }

                is UiEvent.ShowToast -> {
                    isSaving = false
                    clickLocked.value = false
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add note") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {

                if (title.isBlank() && content.isBlank()) {
                    Toast.makeText(
                        context,
                        "Please add note title and content",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@FloatingActionButton
                }
                if (title.isBlank()) {
                    Toast.makeText(
                        context,
                        "Please add note title and content",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@FloatingActionButton
                }
                if (content.isBlank()) {
                    Toast.makeText(context, "Please add content", Toast.LENGTH_SHORT).show()
                    return@FloatingActionButton
                }
                debouncedClick {
                    isSaving = true
                    viewModel.addNote(
                        Note(
                            title = title,
                            content = content,
                            color = selectedColor,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
            ) {
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
        },
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
                    .focusRequester(contentFocusRequester)

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
            Spacer(modifier = Modifier.height(15.dp))
            Text("Select Note Color", style = MaterialTheme.typography.labelLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 12.dp)

            ) {
                notesColor.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (color.toArgb() == selectedColor) 2.dp else 1.dp,
                                color = if (color.toArgb() == selectedColor) Color.Black else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = color.toArgb() }
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
        }
    }
}