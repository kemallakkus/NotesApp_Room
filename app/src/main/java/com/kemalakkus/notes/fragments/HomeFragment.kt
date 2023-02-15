package com.kemalakkus.notes.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.OnClickListener
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.getPermissionCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kemalakkus.notes.MainActivity
import com.kemalakkus.notes.R
import com.kemalakkus.notes.SharedPref
import com.kemalakkus.notes.adapter.NotesAdapter
import com.kemalakkus.notes.databinding.FragmentHomeBinding
import com.kemalakkus.notes.model.NoteModel
import com.kemalakkus.notes.viewmodel.NotesViewModel


class HomeFragment : Fragment(), SearchView.OnQueryTextListener {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel
    private lateinit var notesAdapter: NotesAdapter
    lateinit var sharedPref: SharedPref
    //var control :Boolean=true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        sharedPref= SharedPref(requireContext().applicationContext)
        setupRecyclerView()

        if (sharedPref.loadLayoutModeState()==true){
            linear()
        }else{
            grid()
        }


        //darkModeOnOff()

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }

        if (sharedPref.loadNightModeState() == true) {
            binding.switchTheme.isChecked = true
        }
        binding.switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sharedPref.setNightModeState(true)
                restarApp()
                //binding.switchTheme.text = "On"

            } else {
                sharedPref.setNightModeState(false)
                restarApp()
                //binding.switchTheme.text = "Off"

            }
        }
    }

    private fun setupRecyclerView(){

        /*grid()
        linear()

        notesAdapter = NotesAdapter()

        binding.rvNote.apply {

            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

            setHasFixedSize(true)
            adapter = notesAdapter
        }*/
        binding.gridImage.setOnClickListener {
            grid()
            sharedPref.setLayoutModeState(true)
        }
        binding.linearImage.setOnClickListener {
            linear()
            sharedPref.setLayoutModeState(false)
        }
    }



    private fun linear() {
        notesAdapter = NotesAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            setHasFixedSize(true)
            adapter = notesAdapter
        }
        binding.gridImage.visibility = View.VISIBLE
        binding.linearImage.visibility = View.GONE

        activity?.let {
            viewModel.getAllNotes().observe(viewLifecycleOwner, { note ->
                notesAdapter.differ.submitList(note)
                //updateUI(note)
            })
        }

    }

    private fun grid() {
        notesAdapter = NotesAdapter()
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = notesAdapter
        }
        binding.linearImage.visibility = View.VISIBLE
        binding.gridImage.visibility = View.GONE

        activity?.let {
            viewModel.getAllNotes().observe(viewLifecycleOwner, { note ->
                notesAdapter.differ.submitList(note)
                //updateUI(note)
            })
        }
    }

    /*private fun updateUI(note: List<NoteModel>){
        if (note.isNotEmpty()){
            binding.rvNote.visibility = View.VISIBLE
            //binding.tvNoNotesAvailable.visibility = View.GONE
        }else{
            binding.rvNote.visibility = View.GONE
            //binding.tvNoNotesAvailable.visibility = View.VISIBLE
        }
    }*/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)

        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)
    }


    override fun onQueryTextSubmit(query: String?): Boolean {

        /*if (query != null){
            searchNotes(query)
        }*/

        return false
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


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun restarApp(){
        val intent= Intent(requireContext().applicationContext,MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


}
