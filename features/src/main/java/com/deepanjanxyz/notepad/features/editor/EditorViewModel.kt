package com.deepanjanxyz.notepad.features.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepanjanxyz.notepad.core.database.CategoryDao
import com.deepanjanxyz.notepad.core.database.NoteDao
import com.deepanjanxyz.notepad.core.database.toDomain
import com.deepanjanxyz.notepad.core.database.toEntity
import com.deepanjanxyz.notepad.core.model.Category
import com.deepanjanxyz.notepad.core.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EditorMode {
    EDIT, PREVIEW, SPLIT
}

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ -> }

    val noteId: Long = savedStateHandle.get<Long>("noteId") ?: 0L

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(1L)
    val selectedCategoryId: StateFlow<Long> = _selectedCategoryId.asStateFlow()

    private val _isPinned = MutableStateFlow(false)
    val isPinned: StateFlow<Boolean> = _isPinned.asStateFlow()

    private val _isArchived = MutableStateFlow(false)
    val isArchived: StateFlow<Boolean> = _isArchived.asStateFlow()

    private val _editorMode = MutableStateFlow(EditorMode.EDIT)
    val editorMode: StateFlow<EditorMode> = _editorMode.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryDao.getAllCategories()
        .map { list -> list.map { it.toDomain() } }
        .catch { emit(emptyList<Category>()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<Category>())

    init {
        if (noteId > 0L) {
            viewModelScope.launch(exceptionHandler) {
                val existing = noteDao.getNoteById(noteId)
                if (existing != null) {
                    _title.value = existing.title
                    _content.value = existing.content
                    _selectedCategoryId.value = existing.categoryId
                    _isPinned.value = existing.isPinned
                    _isArchived.value = existing.isArchived
                }
            }
        }
    }

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun setContent(newContent: String) {
        _content.value = newContent
    }

    fun setCategoryId(categoryId: Long) {
        _selectedCategoryId.value = categoryId
    }

    fun togglePin() {
        _isPinned.value = !_isPinned.value
    }

    // Toggle note archive state
    fun toggleArchive() {
        _isArchived.value = !_isArchived.value
    }

    fun setEditorMode(mode: EditorMode) {
        _editorMode.value = mode
    }

    fun insertMarkdown(syntaxPrefix: String, syntaxSuffix: String = "") {
        _content.value = _content.value + syntaxPrefix + syntaxSuffix
    }

    fun saveNote(onSaved: () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            runCatching {
                val currentNote = Note(
                    id = noteId,
                    title = _title.value.ifBlank { "Untitled Note" },
                    content = _content.value,
                    categoryId = _selectedCategoryId.value,
                    isPinned = _isPinned.value,
                    isArchived = _isArchived.value,
                    updatedAt = System.currentTimeMillis()
                )
                noteDao.insertNote(currentNote.toEntity())
            }
            onSaved()
        }
    }
}
