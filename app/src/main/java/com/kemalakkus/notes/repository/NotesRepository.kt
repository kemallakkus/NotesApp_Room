package com.kemalakkus.notes.repository

import com.kemalakkus.notes.db.NoteDatabase
import com.kemalakkus.notes.model.NoteModel

class NotesRepository(private val db: NoteDatabase) {

    suspend fun addNote(note: NoteModel) = db.getAllNoteDao().addNote(note)
    suspend fun updateNote(note: NoteModel) = db.getAllNoteDao().updateNote(note)
    suspend fun deleteNote(note: NoteModel) = db.getAllNoteDao().deleteNote(note)
    fun getAllNotes() = db.getAllNoteDao().getAllNotes()
    fun searchNote(query: String?) = db.getAllNoteDao().searchNote(query)

}