package com.deepanjanxyz.notepad.features.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepanjanxyz.notepad.core.ui.components.MarkdownRenderer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.imePadding
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalContext

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val isPinned by viewModel.isPinned.collectAsState()
    val editorMode by viewModel.editorMode.collectAsState()

    val wordCount = content.trim().let { if (it.isEmpty()) 0 else it.split("\\s+".toRegex()).size }
    val charCount = content.length

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Navigation debounce guard to prevent double-tap glitches
    var isNavigating by remember { mutableStateOf(false) }

    // Auto-scroll to bottom of editor container as long notes are typed to keep cursor visible
    LaunchedEffect(content) {
        if (content.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

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
    BackHandler {
        handleAutoSaveAndBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.noteId == 0L) "New Note" else "Edit Note",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = handleAutoSaveAndBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Save and Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::togglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Default.Pin,
                            contentDescription = "Pin Note",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                    // Redesigned Save button with prominent Material 3 circle container
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
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        // Scrollable body with imePadding so TopAppBar stays fixed at top when keyboard opens
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 2.dp)
        ) {
            // Mode Selector: Edit | Preview | Split
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                SegmentedButton(
                    selected = editorMode == EditorMode.EDIT,
                    onClick = { viewModel.setEditorMode(EditorMode.EDIT) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text("Edit")
                }
                SegmentedButton(
                    selected = editorMode == EditorMode.PREVIEW,
                    onClick = { viewModel.setEditorMode(EditorMode.PREVIEW) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Text("Preview")
                }
                SegmentedButton(
                    selected = editorMode == EditorMode.SPLIT,
                    onClick = { viewModel.setEditorMode(EditorMode.SPLIT) },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Text("Split")
                }
            }

            // Note Title Input Field
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::setTitle,
                placeholder = { Text("Note Title...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Markdown Formatting Bar
            if (editorMode != EditorMode.PREVIEW) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = { viewModel.insertMarkdown("# ") }) {
                        Icon(Icons.Default.Title, contentDescription = "Header 1")
                    }
                    IconButton(onClick = { viewModel.insertMarkdown("**", "**") }) {
                        Icon(Icons.Default.FormatBold, contentDescription = "Bold")
                    }
                    IconButton(onClick = { viewModel.insertMarkdown("*", "*") }) {
                        Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
                    }
                    IconButton(onClick = { viewModel.insertMarkdown("~~", "~~") }) {
                        Icon(Icons.Default.FormatStrikethrough, contentDescription = "Strikethrough")
                    }
                    IconButton(onClick = { viewModel.insertMarkdown("- ") }) {
                        Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List")
                    }
                    IconButton(onClick = { viewModel.insertMarkdown("> ") }) {
                        Icon(Icons.Default.FormatQuote, contentDescription = "Blockquote")
                    }
                    IconButton(onClick = { viewModel.insertMarkdown("```\n", "\n```") }) {
                        Icon(Icons.Default.Code, contentDescription = "Code Block")
                    }
                }
            }

            // Editor Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 350.dp)
            ) {
                when (editorMode) {
                    EditorMode.EDIT -> {
                        OutlinedTextField(
                            value = content,
                            onValueChange = viewModel::setContent,
                            placeholder = { Text("Type your Markdown memo here...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 350.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }
                    EditorMode.PREVIEW -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            MarkdownRenderer(markdown = content)
                        }
                    }
                    EditorMode.SPLIT -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 350.dp)
                        ) {
                            OutlinedTextField(
                                value = content,
                                onValueChange = viewModel::setContent,
                                placeholder = { Text("Edit...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 350.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 350.dp)
                            ) {
                                MarkdownRenderer(markdown = content)
                            }
                        }
                    }
                }
            }

            // Footer Word & Char Counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$wordCount words | $charCount chars",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Encrypted Local Note",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
