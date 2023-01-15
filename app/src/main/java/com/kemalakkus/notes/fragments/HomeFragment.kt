package com.kemalakkus.notes.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kemalakkus.notes.MainActivity
import com.kemalakkus.notes.R
import com.kemalakkus.notes.adapter.NotesAdapter
import com.kemalakkus.notes.databinding.FragmentHomeBinding
import com.kemalakkus.notes.model.NoteModel
import com.kemalakkus.notes.viewmodel.NotesViewModel


@Suppress("DEPRECATION")
class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        setupRecyclerView()

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }
    }

    private fun setupRecyclerView(){

        notesAdapter = NotesAdapter()

        binding.rvNote.apply {

            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

            setHasFixedSize(true)
            adapter = notesAdapter
        }

        activity?.let {
            viewModel.getAllNotes().observe(viewLifecycleOwner, Observer{ note->
                notesAdapter.differ.submitList(note)
                updateUI(note)
            })
        }
    }

    private fun updateUI(note: List<NoteModel>){
        if (note.isNotEmpty()){
            binding.rvNote.visibility = View.VISIBLE
            binding.tvNoNotesAvailable.visibility = View.GONE
        }else{
            binding.rvNote.visibility = View.GONE
            binding.tvNoNotesAvailable.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)

        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = true
        mMenuSearch.setOnQueryTextListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        if (query != null){
            searchNotes(query)
        }

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            searchNotes(newText)
        }

        return true
    }

    private fun searchNotes(query: String?){

        val searchQuery = "%$query%"
        viewModel.searchNote(searchQuery).observe(this, Observer { notemodel ->


            notesAdapter.differ.submitList(notemodel)
        })

    }

}