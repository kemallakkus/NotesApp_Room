package com.kemalakkus.notes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kemalakkus.notes.model.NoteModel
import com.kemalakkus.notes.repository.NotesRepository
import kotlinx.coroutines.launch

class NotesViewModel(application: Application, private val notesRepository: NotesRepository): AndroidViewModel(application) {

    fun addNote(note: NoteModel) = viewModelScope.launch {
        notesRepository.addNote(note)
    }

    fun updateNote(note: NoteModel) = viewModelScope.launch {
        notesRepository.updateNote(note)
    }

    fun deleteNote(note: NoteModel) = viewModelScope.launch {
        notesRepository.deleteNote(note)
    }

    fun getAllNotes() = notesRepository.getAllNotes()

    fun searchNote(query: String?) = notesRepository.searchNote(query)

}