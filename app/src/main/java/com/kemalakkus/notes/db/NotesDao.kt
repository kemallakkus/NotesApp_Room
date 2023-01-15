package com.kemalakkus.notes.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kemalakkus.notes.model.NoteModel

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: NoteModel)

    @Update
    suspend fun updateNote(note: NoteModel)

    @Delete
    suspend fun deleteNote(note: NoteModel)

    @Query("SELECT * FROM notesTableName ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM  notesTableName WHERE noteTitle LIKE :query OR noteBody LIKE :query ")
    fun searchNote(query: String?): LiveData<List<NoteModel>>

}