package com.sadxlab.notescompose.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.ui.theme.LocalAppDarkMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    onDelete: (Note) -> Unit,
    onClick: () -> Unit,
    isListView: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = LocalAppDarkMode.current
    val cardColor = remember(note.color, isDark) {
        val base = Color(note.color)
        if (isDark) Color(
            red = base.red * 0.45f,
            green = base.green * 0.45f,
            blue = base.blue * 0.45f,
            alpha = base.alpha
        ) else base
    }
    val textColor = if (isDark) Color(0xFFE8E8E8) else Color.Unspecified
    val subTextColor = if (isDark) Color(0xFFAAAAAA) else Color(0xFF666666)

    val formattedDate = remember(note.timestamp) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(note.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (isListView) 72.dp else 130.dp, max = if (isListView) 110.dp else 190.dp)
            .combinedClickable(onClick = onClick, onLongClick = { onDelete(note) }),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        note.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (note.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = subTextColor,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(13.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    maxLines = if (isListView) 2 else 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = subTextColor,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 6.dp)
            )
        }
    }
}
