package com.kemalakkus.notes.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kemalakkus.notes.repository.NotesRepository

class NotesViewModelProviderFactory(val app: Application, private val notesRepository: NotesRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(app,notesRepository) as T
    }

}