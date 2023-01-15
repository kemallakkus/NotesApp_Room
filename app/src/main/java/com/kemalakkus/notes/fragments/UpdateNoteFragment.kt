package com.kemalakkus.notes.fragments

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.kemalakkus.notes.MainActivity
import com.kemalakkus.notes.R
import com.kemalakkus.notes.databinding.FragmentUpdateNoteBinding
import com.kemalakkus.notes.model.NoteModel
import com.kemalakkus.notes.toast
import com.kemalakkus.notes.viewmodel.NotesViewModel


class UpdateNoteFragment : Fragment() {

    private lateinit var binding: FragmentUpdateNoteBinding
    private val args : UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: NoteModel
    private lateinit var viewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateNoteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        currentNote = args.note!!

        binding.edNoteTitleUpdate.setText(currentNote.noteTitle)
        binding.edNoteBodyUpdate.setText(currentNote.noteBody)

        currentNote.photo?.let {
            binding.imageGalleryUpdateNote.setImageBitmap(BitmapFactory.decodeByteArray(currentNote.photo,0,it.size))

        }
        if (currentNote.photo != null){
            binding.imageGalleryUpdateNote.visibility=View.VISIBLE
        }

        binding.fabUpdate.setOnClickListener {

            val title = binding.edNoteTitleUpdate.text.toString().trim()
            val body = binding.edNoteBodyUpdate.text.toString().trim()

            if(title.isNotEmpty()){
                val note = NoteModel(currentNote.id, title,body,currentNote.photo)

                viewModel.updateNote(note)
                activity?.toast("Note updated!")

                view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)

            }else{
                activity?.toast("Please enter title name!")
            }

        }
    }

    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("Are you sure want to delete this note?")
            setPositiveButton("DELETE"){_,_->
                viewModel.deleteNote(currentNote)
                view?.findNavController()?.navigate(R.id.action_updateNoteFragment_to_homeFragment)
            }
            setNegativeButton("CANCEL",null)
        }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.clear()
        inflater.inflate(R.menu.update_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.delete_menu ->{
                deleteNote()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}