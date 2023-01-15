package com.kemalakkus.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.kemalakkus.notes.databinding.ActivityMainBinding
import com.kemalakkus.notes.db.NoteDatabase
import com.kemalakkus.notes.repository.NotesRepository
import com.kemalakkus.notes.viewmodel.NotesViewModel
import com.kemalakkus.notes.viewmodel.NotesViewModelProviderFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NotesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupViewModel()
    }

    private fun setupViewModel(){
        
        val notesRepository = NotesRepository(NoteDatabase(this))

        val viewModelProviderFactory = NotesViewModelProviderFactory(application, notesRepository)

        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NotesViewModel::class.java)
        
    }

}