package com.deepanjanxyz.notepad.features.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import android.widget.Toast
import com.deepanjanxyz.notepad.core.ui.components.MarkdownRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val isPinned by viewModel.isPinned.collectAsState()
    val isArchived by viewModel.isArchived.collectAsState()
    val editorMode by viewModel.editorMode.collectAsState()

    val wordCount = content.trim().let { if (it.isEmpty()) 0 else it.split("\\s+".toRegex()).size }
    val charCount = content.length

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Navigation debounce guard to prevent double-tap glitches
    var isNavigating by remember { mutableStateOf(false) }

    // Auto-save logic when navigating back with debounce guard
    val handleAutoSaveAndBack: () -> Unit = {
        if (!isNavigating) {
            isNavigating = true
            if (title.isNotBlank() || content.isNotBlank()) {
                viewModel.saveNote {
                    Toast.makeText(context, "Auto Saved", Toast.LENGTH_SHORT).show()
                    onBack()
                }
            } else {
                onBack()
            }
        }
    }

    // Manual save button action
    val handleManualSave: () -> Unit = {
        if (!isNavigating) {
            isNavigating = true
            viewModel.saveNote {
                Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
                onBack()
            }
        }
    }

    // Intercept system back gestures to trigger auto-save
    BackHandler(enabled = !isNavigating) {
        handleAutoSaveAndBack()
    }

    Scaffold(
        topBar = {
            // Google Keep style clean transparent top app bar
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = handleAutoSaveAndBack,
                        enabled = !isNavigating
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Pin Action
                    IconButton(onClick = viewModel::togglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Default.Pin,
                            contentDescription = "Pin Note",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                    // Reminder Action
                    IconButton(onClick = {
                        Toast.makeText(context, "Reminder feature coming soon", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Reminder",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                    // Archive Action
                    IconButton(onClick = {
                        viewModel.toggleArchive()
                        Toast.makeText(
                            context,
                            if (!isArchived) "Note Archived" else "Note Unarchived",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(
                            imageVector = if (isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                            contentDescription = "Archive Note",
                            tint = if (isArchived) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                    // Manual Save Tick Badge
                    Surface(
                        onClick = handleManualSave,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save Note",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            // Material 3 expressive bottom formatting bar anchored directly above software keyboard
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 3.dp,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Formatting shortcuts row with M3 IconButton styling
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.insertMarkdown("**", "**") },
                            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.FormatBold, contentDescription = "Bold")
                        }
                        IconButton(
                            onClick = { viewModel.insertMarkdown("*", "*") },
                            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
                        }
                        IconButton(
                            onClick = { viewModel.insertMarkdown("# ") },
                            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.Title, contentDescription = "Header")
                        }
                        IconButton(
                            onClick = { viewModel.insertMarkdown("- ") },
                            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List")
                        }
                        IconButton(
                            onClick = { viewModel.insertMarkdown("1. ") },
                            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = "Numbered List")
                        }
                        IconButton(
                            onClick = { viewModel.insertMarkdown("`", "`") },
                            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.Code, contentDescription = "Code")
                        }
                    }

                    // 3-dot overflow menu
                    Box {
                        var showOverflowMenu by remember { mutableStateOf(false) }
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (editorMode == EditorMode.PREVIEW) "Edit Mode" else "Markdown Preview") },
                                leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.setEditorMode(if (editorMode == EditorMode.PREVIEW) EditorMode.EDIT else EditorMode.PREVIEW)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("$wordCount words | $charCount chars") },
                                onClick = { showOverflowMenu = false }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        // Borderless, seamless distraction-free Google Keep writing container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            // Seamless Title Input Field
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::setTitle,
                placeholder = {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Seamless Content / Preview Area
            if (editorMode == EditorMode.PREVIEW) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    MarkdownRenderer(markdown = content.ifBlank { "*No content to preview*" })
                }
            } else {
                OutlinedTextField(
                    value = content,
                    onValueChange = viewModel::setContent,
                    placeholder = {
                        Text(
                            text = "Note",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }
    }
}
