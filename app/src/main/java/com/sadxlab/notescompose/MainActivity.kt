package com.sadxlab.notescompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sadxlab.notescompose.presentation.ui.AddNoteScreen
import com.sadxlab.notescompose.presentation.ui.EditNoteScreen
import com.sadxlab.notescompose.presentation.ui.NoteScreen
import com.sadxlab.notescompose.ui.theme.NotesComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    NoteScreen(navController)
                }
                composable("addNote") {
                    AddNoteScreen(navController)
                }
                composable(
                    "editNote/{noteId}",
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                )
                {
                    val noteId = it.arguments?.getInt("noteId") ?: 0
                    EditNoteScreen(navController, noteId)
                }

            }

        }
    }
}

