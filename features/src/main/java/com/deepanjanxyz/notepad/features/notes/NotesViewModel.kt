package com.deepanjanxyz.notepad.features.notes

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        // Defensive error handling to prevent app crashes
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryDao.getAllCategories()
        .catch { emit(emptyList()) }
        .combine(_selectedCategoryId) { list, selectedId ->
            list.map { it.toDomain() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<Note>> = combine(
        noteDao.getAllNotes(),
        _searchQuery,
        _selectedCategoryId
    ) { noteEntities, query, categoryId ->
        var filtered = noteEntities.map { it.toDomain() }
        if (categoryId != null) {
            filtered = filtered.filter { it.categoryId == categoryId }
        }
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
        filtered
    }
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun togglePin(note: Note) {
        viewModelScope.launch(exceptionHandler) {
            runCatching {
                val updated = note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis())
                noteDao.updateNote(updated.toEntity())
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(exceptionHandler) {
            runCatching {
                noteDao.deleteNote(note.toEntity())
            }
        }
    }
}
