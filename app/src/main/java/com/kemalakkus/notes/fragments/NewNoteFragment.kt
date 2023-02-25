package com.kemalakkus.notes.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
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
import com.google.android.material.snackbar.Snackbar
import com.kemalakkus.notes.MainActivity
import com.kemalakkus.notes.R
import com.kemalakkus.notes.audio.Recorder
import com.kemalakkus.notes.databinding.FragmentNewNoteBinding
import com.kemalakkus.notes.model.NoteModel
import com.kemalakkus.notes.toast
import com.kemalakkus.notes.viewmodel.NotesViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel
    private lateinit var mView: View
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null
    var byteArray: ByteArray? = null
    private lateinit var note: NoteModel
    private lateinit var toolBar: Toolbar
    private var color = "#FFFFFF"
    private lateinit var alertBuilder: AlertDialog.Builder
    lateinit var recorder: Recorder
    val handler= Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        colorPick()
        mView = view
        selectImage()
        registerLauncher()
        onPermissionControl()
        toolBar = requireActivity().findViewById(R.id.toolbar)
        context?.let {
            recorder = Recorder(it)
        }

        saveAudioDialog()
        recorder.md.setOnCompletionListener {
            Handler().postDelayed({
                binding.playButton.setImageResource(R.drawable.ic_play_audio)
            },  500L)

        }

        binding.playButton.setOnClickListener {
            if (recorder.file?.exists()!!){
                recorder.file?.absolutePath?.let { it1 -> recorder.playAudio(it1) }
                binding.playButton.setImageResource(recorder.test!!)
                //binding.playButton.isEnabled = false
                //binding.stopButton.isEnabled = true
                //binding.recordButton.isEnabled = true
            }else{
                Toast.makeText(context,"File is empty", Toast.LENGTH_SHORT).show()

            }

        }


    }


    @SuppressLint("ClickableViewAccessibility")
    private fun saveAudioDialog() {
        binding.recordButton.setOnLongClickListener {
            val view =LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_for_audio,null)
            alertBuilder= AlertDialog.Builder(requireContext())
            alertBuilder.setView(view)
            val dialog=alertBuilder.create()
            onPermissionControl()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            var timeText=view.findViewById<TextView>(R.id.timerText)
            var startTime = System.currentTimeMillis()
            recorder.startRecording()
            handler.post(object : Runnable {
                override fun run() {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    timeText.text = formatTime(elapsedTime)
                    handler.postDelayed(this, 1000)
                }
            })
            binding.recordButton.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        recorder.stoprecording()
                        handler.removeCallbacksAndMessages(null)
                        dialog.dismiss()
                        binding.recordButton.setOnTouchListener(null)
                        binding.playButton.visibility=View.VISIBLE
                    }
                }
                true
            }
            true
        }
    }

    fun formatTime(timeInMillis: Long): String {
        val seconds = timeInMillis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun onPermissionControl() {
        val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),permissions,0)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                Toast.makeText(context, "Request Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun colorPick() {
        binding.colorPicker.setOnClickListener {
            ColorPickerDialog.Builder(requireContext()).setTitle("Product Color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let{
                            color="#${Integer.toHexString(it.color).substring(2)}"
                            binding.cardView.setCardBackgroundColor(Color.parseColor(color))

                        }
                    }
                })
                .setNegativeButton("Cancel"){colorPicker,_->
                    colorPicker.dismiss()
                }.show()
        }
    }

    private fun saveNote(view: View){
        val noteTitle = binding.etNoteTitle.text.toString().trim()
        val noteBody = binding.etNoteBody.text.toString().trim()
        val date=SimpleDateFormat("d MMM yyyy HH:mm", Locale("tr","tr")).format(Date())

        /*val audioPath = recorder?.let {
            val outputFile = File(requireContext().externalCacheDir?.absolutePath, "recording.mp4")
            it.stop()
            it.reset()
            it.release()
            recorder.file?.absolutePath
        }*/


        if (noteTitle.isNotEmpty()){

            if(selectedBitmap != null) {
                val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

                val outputStream = ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                byteArray = outputStream.toByteArray()
            }

            note = NoteModel(0,noteTitle,noteBody,byteArray,color,date, recorder.file?.absolutePath)

            viewModel.addNote(note)

            Snackbar.make(view, "Note saved successfully", Snackbar.LENGTH_LONG).show()

            view.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)

        }else{
            Snackbar.make(view,"Please enter note title",Snackbar.LENGTH_SHORT).show()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_menu ->{
                saveNote(mView)
            }
            else -> findNavController().navigateUp()
        }
        return true
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.new_note_menu, menu)
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
                            binding.selectedImage.setImageBitmap(selectedBitmap)
                            binding.selectedImage.visibility = View.VISIBLE
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageData)
                            binding.selectedImage.setImageBitmap(selectedBitmap)
                            binding.selectedImage.visibility=View.VISIBLE

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

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
        recorder.stopPlaying()
        _binding = null
    }

}
