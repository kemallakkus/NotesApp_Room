package com.kemalakkus.notes.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.kemalakkus.notes.MainActivity
import com.kemalakkus.notes.R
import com.kemalakkus.notes.databinding.FragmentUpdateNoteBinding
import com.kemalakkus.notes.model.NoteModel
import com.kemalakkus.notes.toast
import com.kemalakkus.notes.viewmodel.NotesViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class UpdateNoteFragment : Fragment() {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!
    private val args : UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: NoteModel
    private lateinit var viewModel: NotesViewModel
    private lateinit var toolBar: Toolbar
    private var color=""

    private var selectedBitmap : Bitmap? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var byteArray:ByteArray?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateNoteBinding.inflate(
            inflater,
            container,
            false
        )
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        currentNote = args.note!!
        colorPick()
        selectImage()
        registerLauncher()

        binding.etNoteTitleUpdate.setText(currentNote.noteTitle)
        binding.etNoteBodyUpdate.setText(currentNote.noteBody)

        currentNote.photo?.let {
            binding.loadSelectedImage.setImageBitmap(BitmapFactory.decodeByteArray(currentNote.photo,0,it.size))

        }
        binding.updtadeCardView.setCardBackgroundColor(Color.parseColor(currentNote.colors))

        if (currentNote.photo != null){
            binding.loadSelectedImage.visibility=View.VISIBLE
        }

        binding.fabDone.setOnClickListener {

            val title = binding.etNoteTitleUpdate.text.toString().trim()
            val body = binding.etNoteBodyUpdate.text.toString().trim()
            val date= SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale("tr","tr")).format(Date())

            if(color.isEmpty()){
                color=currentNote.colors!!
            }
            if(title.isNotEmpty()){
                if (selectedBitmap != null){
                    val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
                    val outputStream = ByteArrayOutputStream()
                    smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                    byteArray = outputStream.toByteArray()
                }else{
                    byteArray=currentNote.photo
                }
                val note = NoteModel(currentNote.id, title,body,byteArray,color,date)

                viewModel.updateNote(note)
                Snackbar.make(view,"Note Updated!",Snackbar.LENGTH_SHORT).show()

                view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)

            }else{
                Snackbar.make(view,"Enter a note title please",Snackbar.LENGTH_SHORT).show()
            }

        }
        toolBar= requireActivity().findViewById(R.id.toolbar)
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
            else->findNavController().navigateUp()
        }
        return true
    }

    private fun colorPick() {
        binding.updateColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(requireContext()).setTitle("Product Color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let{
                            color="#${Integer.toHexString(it.color).substring(2)}"
                            binding.updtadeCardView.setCardBackgroundColor(Color.parseColor(color))

                        }
                    }
                })
                .setNegativeButton("Cancel"){colorPicker,_->
                    colorPicker.dismiss()
                }.show()
        }

    }

    fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    private fun selectImage() {
        binding.imageControl.setOnClickListener{view->
            if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.loadSelectedImage.setImageBitmap(selectedBitmap)
                            binding.loadSelectedImage.visibility=View.VISIBLE
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageData)
                            binding.loadSelectedImage.setImageBitmap(selectedBitmap)
                            binding.loadSelectedImage.visibility=View.VISIBLE
                        }
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

}