package com.kemalakkus.notes.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.widget.Toast
import com.kemalakkus.notes.R
import java.io.File

class Recorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var fileName: String? = null
    var test: Int? = null
    var file: File? = null
    val md = MediaPlayer()


    init {
        test
        file
        md
    }

    fun startRecording(){

        fileName = "${System.currentTimeMillis()}.mp3"
        file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName!!)
        recorder = MediaRecorder().apply {

            try {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file?.absolutePath)
                prepare()
                start()
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun stoprecording(): String?{
        recorder?.apply {
           try {
               stop()
               release()
           }catch (e:Exception){
               e.printStackTrace()
           }
        }
        return fileName
    }

    fun playAudio(filex: String) {

        if (filex.isNotEmpty()) {

            try {
                if (!md.isPlaying) {
                    md.reset()
                    md.setDataSource(filex)
                    md.prepare()
                    md.start()
                    test = R.drawable.ic_pause_audio
                } else {
                    md.pause()
                    test = R.drawable.ic_play_audio
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopPlaying() {
        md.stop()
        md.release()

    }

}