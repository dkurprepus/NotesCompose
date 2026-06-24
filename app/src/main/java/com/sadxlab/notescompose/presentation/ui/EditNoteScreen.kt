package com.sadxlab.notescompose.presentation.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.presentation.UiEvent
import com.sadxlab.notescompose.presentation.debouncedClick
import com.sadxlab.notescompose.presentation.viewmodel.NoteViewModel
import com.sadxlab.notescompose.ui.theme.LocalAppDarkMode
import com.sadxlab.notescompose.ui.theme.NoteColorPalette
import java.text.SimpleDateFormat
import java.util.Calendar
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
    val isDark = LocalAppDarkMode.current

    LaunchedEffect(Unit) { viewModel.loadNoteById(noteId) }
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            isSaving = false
            when (event) {
                is UiEvent.SaveSuccess -> navController.popBackStack()
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val note by viewModel.editingNote.collectAsState()
    val contentFocusRequester = remember { FocusRequester() }
    val titleFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    when (val noteValue = note) {
        null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        else -> {
            var title by rememberSaveable { mutableStateOf(noteValue.title) }
            var content by rememberSaveable { mutableStateOf(noteValue.content) }
            var selectedColor by rememberSaveable { mutableStateOf(noteValue.color) }
            var reminder by remember { mutableStateOf(noteValue.reminder) }
            var showDeleteDialog by remember { mutableStateOf(false) }

            val wordCount = remember(content) { if (content.isBlank()) 0 else content.trim().split(Regex("\\s+")).size }
            val charCount = content.length

            val formattedReminder = remember(reminder) {
                reminder?.let { SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault()).format(Date(it)) }
            }
            val formattedDate = remember(noteValue.timestamp) {
                SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()).format(Date(noteValue.timestamp))
            }

            val bgColor by animateColorAsState(
                targetValue = run {
                    val base = Color(selectedColor)
                    if (isDark) Color(base.red * 0.3f, base.green * 0.3f, base.blue * 0.3f)
                    else base.copy(alpha = 0.28f)
                },
                animationSpec = tween(300), label = "bgColor"
            )
            val transparentFieldColors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )

            val notifPermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

            fun showDateTimePicker() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                val cal = Calendar.getInstance().apply { reminder?.let { timeInMillis = it } }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        cal.set(year, month, day)
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                cal.set(Calendar.HOUR_OF_DAY, hour)
                                cal.set(Calendar.MINUTE, minute)
                                cal.set(Calendar.SECOND, 0)
                                cal.set(Calendar.MILLISECOND, 0)
                                if (cal.timeInMillis > System.currentTimeMillis()) {
                                    reminder = cal.timeInMillis
                                } else {
                                    Toast.makeText(context, "Please choose a future time", Toast.LENGTH_SHORT).show()
                                }
                            },
                            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
                        ).show()
                    },
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                ).apply { datePicker.minDate = System.currentTimeMillis() }.show()
            }

            Scaffold(
                containerColor = bgColor,
                topBar = {
                    TopAppBar(
                        title = { Text("Edit Note") },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "${noteValue.title}\n\n${noteValue.content}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share note"))
                            }) { Icon(Icons.Default.Share, contentDescription = "Share") }
                            IconButton(onClick = { viewModel.togglePin(noteValue) }) {
                                Icon(
                                    if (noteValue.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = if (noteValue.isPinned) "Unpin" else "Pin"
                                )
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        debouncedClick {
                            if (title.isBlank() || content.isBlank()) {
                                Toast.makeText(context, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
                                return@debouncedClick
                            }
                            if (title == noteValue.title && content == noteValue.content && selectedColor == noteValue.color && reminder == noteValue.reminder) {
                                Toast.makeText(context, "No changes made", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                                return@debouncedClick
                            }
                            isSaving = true
                            viewModel.updateNote(
                                Note(
                                    id = noteId,
                                    title = title,
                                    content = content,
                                    color = selectedColor,
                                    timestamp = System.currentTimeMillis(),
                                    isPinned = noteValue.isPinned,
                                    reminder = reminder
                                )
                            )
                        }
                    }) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                },
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .background(bgColor)
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Edited: $formattedDate",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                            )
                            Text(
                                "$wordCount words · $charCount chars",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDateTimePicker() }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    if (reminder != null) Icons.Default.Alarm else Icons.Default.AlarmOff,
                                    contentDescription = null,
                                    tint = if (reminder != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = formattedReminder ?: "Set reminder",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (reminder != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            if (reminder != null) {
                                IconButton(onClick = { reminder = null }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.AlarmOff, contentDescription = "Clear reminder", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(Modifier.height(8.dp))
                        val initialIndex = remember {
                            NoteColorPalette.indexOfFirst { it.toArgb() == selectedColor }.coerceAtLeast(0)
                        }
                        @OptIn(ExperimentalFoundationApi::class)
                        val colorPickerState = rememberLazyListState()
                        LaunchedEffect(Unit) {
                            if (initialIndex > 0) colorPickerState.scrollToItem(initialIndex)
                        }
                        @OptIn(ExperimentalFoundationApi::class)
                        LazyRow(
                            state = colorPickerState,
                            flingBehavior = rememberSnapFlingBehavior(colorPickerState),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(NoteColorPalette) { color ->
                                val isSelected = color.toArgb() == selectedColor
                                val circleSize by animateDpAsState(
                                    targetValue = if (isSelected) 42.dp else 32.dp,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
                                    label = "size_${color.value}"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(circleSize)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Gray.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor = color.toArgb() }
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check, contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(16.dp).align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = {
                            Text(
                                "Title",
                                style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        },
                        textStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { contentFocusRequester.requestFocus() }),
                        singleLine = true,
                        colors = transparentFieldColors,
                        modifier = Modifier.fillMaxWidth().focusRequester(titleFocusRequester)
                    )
                    TextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = {
                            Text("Start writing…", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        colors = transparentFieldColors,
                        modifier = Modifier.fillMaxWidth().weight(1f).focusRequester(contentFocusRequester)
                    )
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Note?") },
                    text = { Text("Are you sure? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteNote(noteValue)
                            showDeleteDialog = false
                            navController.popBackStack()
                        }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}
