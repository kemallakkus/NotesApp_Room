package com.kemalakkus.notes.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kemalakkus.notes.model.NoteModel

@Database(entities = [NoteModel::class], version = 3)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun getAllNoteDao(): NotesDao

    companion object{

        @Volatile
        private var instance: NoteDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `notesTableName` (`id` INTEGER, `noteTitle` STRING, `noteBody` STRING , `photo` BYTEARRAY, `colors` STRING , `date` STRING ," +
                        "PRIMARY KEY(`id`))")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `notesTableName` (`id` INTEGER, `noteTitle` STRING, `noteBody` STRING , `photo` BYTEARRAY, `colors` STRING , `date` STRING ," +
                        "PRIMARY KEY(`id`))")
            }
        }




        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "note_db"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()



    }



}