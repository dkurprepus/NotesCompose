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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun AddNoteScreen(
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel()
) {

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedColor by remember { mutableStateOf(Color(0xFFFFF59D).toArgb()) }
    val notesColor = listOf(
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
            TopAppBar(title = { Text("Add note") })
        }, floatingActionButton = {
            FloatingActionButton(onClick = {

                when {
                    title.isBlank() && content.isBlank() -> {
                        Toast.makeText(
                            context,
                            "Please add note title and content",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    title.isBlank() -> {
                        Toast.makeText(context, "Please add note title", Toast.LENGTH_SHORT).show()
                    }

                    content.isBlank() -> {
                        Toast.makeText(context, "Please add content", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        viewModel.addNote(Note(title = title, content = content, color = selectedColor))
                        navController.popBackStack()
                    }
                }


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
            Spacer(modifier = Modifier.height( 15.dp))
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
                                width = if (color.toArgb() == selectedColor) 3.dp else 1.dp,
                                color = if (color.toArgb() == selectedColor) Color.Black else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = color.toArgb() }
                    )
                }
            }
        }
    }
}