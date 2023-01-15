package com.kemalakkus.notes.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kemalakkus.notes.R
import com.kemalakkus.notes.databinding.NoteLayoutAdapterBinding
import com.kemalakkus.notes.fragments.HomeFragmentDirections
import com.kemalakkus.notes.model.NoteModel
import com.kemalakkus.notes.viewmodel.NotesViewModel
import kotlin.random.Random

class NotesAdapter: RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    class NotesViewHolder(val itemBinding: NoteLayoutAdapterBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<NoteModel>(){
        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteBody == newItem.noteBody &&
                    oldItem.noteTitle == newItem.noteTitle
        }

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            NoteLayoutAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )


    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemBinding.tvNoteTitle.text = currentNote.noteTitle
        holder.itemBinding.tvNoteBody.text = currentNote.noteBody

        if (currentNote.photo==null){
            holder.itemBinding.circularImage.visibility= View.GONE
        }
        currentNote.photo?.let {

            holder.itemBinding.circularImage.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    currentNote.photo,
                    0,
                    currentNote.photo.size
                )
            )
            //holder.binding.homeImage.visibility = View.VISIBLE
        }


            val random = java.util.Random()
            val color = Color.argb(
                255, random.nextInt(256), random.nextInt(256), random.nextInt(256))

        holder.itemBinding.viewColor.setBackgroundColor(color)
        holder.itemView.setOnClickListener { mView ->
            //it.findNavController().navigate(R.id.action_homeFragment_to_updateNoteFragment)

            val direction = HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(currentNote)

            mView.findNavController().navigate(direction)

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}