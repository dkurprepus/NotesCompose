package com.sadxlab.notescompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.sadxlab.notescompose.presentation.ui.AddNoteScreen
import com.sadxlab.notescompose.presentation.ui.EditNoteScreen
import com.sadxlab.notescompose.presentation.ui.NoteScreen
import com.sadxlab.notescompose.presentation.viewmodel.NoteViewModel
import com.sadxlab.notescompose.ui.theme.LocalAppDarkMode
import com.sadxlab.notescompose.ui.theme.NotesComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels()

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateDownloaded = MutableStateFlow(false)

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { /* result is handled by installStateListener */ }

    private val installStateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            updateDownloaded.value = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installStateListener)
        checkForUpdate()

        val fromShortcut = intent?.action == "com.sadxlab.notescompose.ADD_NOTE"
        val noteIdFromNotif = intent?.getIntExtra("noteId", -1) ?: -1

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isUpdateDownloaded by updateDownloaded.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView)
                    .isAppearanceLightStatusBars = !isDarkMode
            }

            LaunchedEffect(isUpdateDownloaded) {
                if (isUpdateDownloaded) {
                    val result = snackbarHostState.showSnackbar(
                        message = "Update ready to install",
                        actionLabel = "Restart",
                        duration = SnackbarDuration.Indefinite
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        appUpdateManager.completeUpdate()
                    }
                }
            }

            NotesComposeTheme(darkTheme = isDarkMode, dynamicColor = false) {
                CompositionLocalProvider(LocalAppDarkMode provides isDarkMode) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            enterTransition = { slideInHorizontally { it } + fadeIn() },
                            exitTransition = { slideOutHorizontally { -it } + fadeOut() },
                            popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
                            popExitTransition = { slideOutHorizontally { it } + fadeOut() }
                        ) {
                            composable("home") {
                                NoteScreen(
                                    navController = navController,
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = { viewModel.toggleDarkMode() }
                                )
                            }
                            composable("addNote") { AddNoteScreen(navController) }
                            composable(
                                "editNote/{noteId}",
                                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                            ) {
                                val noteId = it.arguments?.getInt("noteId") ?: 0
                                EditNoteScreen(navController, noteId)
                            }
                        }

                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )

                        LaunchedEffect(Unit) {
                            when {
                                fromShortcut -> navController.navigate("addNote")
                                noteIdFromNotif != -1 -> navController.navigate("editNote/$noteIdFromNotif")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If the update was downloaded while the app was in the background, prompt again
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                updateDownloaded.value = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(installStateListener)
    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                )
            }
        }
    }
}
