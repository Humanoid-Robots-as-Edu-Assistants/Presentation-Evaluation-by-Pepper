package com.example.presentationinterview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException

class MainActivity5 : AppCompatActivity() , RobotLifecycleCallbacks {

    private val audioFolderPath =
        "${Environment.getExternalStorageDirectory().absolutePath}/MyAudioFolder/"
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer4: MediaPlayer? = null
    private var countdownTimer: CountDownTimer? = null


    private lateinit var timerTextView: TextView

    private var recordingDurationMillis: Long = 30000 // 30 seconds
    private var startMusicPlayer4: MediaPlayer? = null
    private var endMusicPlayer4: MediaPlayer? = null
    private var record = ""
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    private fun move(){

        val intent = Intent(this, MainActivity6::class.java)
        startActivity(intent)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)
        hideSystemUI()
        QiSDK.register(this, this)
    }


    override fun onRobotFocusGained(qiContext: QiContext?) {
        val say12 = SayBuilder.with(qiContext)
            .withText("Sehr gut, weiter gehts. Gruppe 4, ihr seid dran.")
            .build()

        val say16 = SayBuilder.with(qiContext)
            .withText("Kommt nach vorne und bereitet eure Präsentation vor. Ihr habt eine Minute Zeit.")
            .build()

        val say13 = SayBuilder.with(qiContext)
            .withText("Die Vorbereitungszeit ist um. Gruppe 4 startet nun mit eurer Präsentation.")
            .build()

        val say14 = SayBuilder.with(qiContext)
            .withText("Die 5 Minuten sind um. Ein letztes Mal versuche ich euer Klatschen zu erkennen. ")
            .build()

        val say17 = SayBuilder.with(qiContext)
            .withText("Bitte klatscht jetzt.")
            .build()

        val say15 = SayBuilder.with(qiContext)
            .withText("Gruppe 4 war die letzte Gruppe. Mitglieder der Gruppe 4, setzt euch nun wieder. dann gebe ich gleich die Ergebnisse bekannt.")
            .build()



        timerTextView = findViewById(R.id.timerTextView4)

        // Request permission to record audio and write to external storage
        requestPermissions()


        runBlocking {
            say12.run()
            Thread.sleep(1000L)
            say16.run()
            Thread.sleep(1000L)
            recordingDurationMillis = 60000L // 1 min preparation
            runOnUiThread { startCountdownTimer() }
            Thread.sleep(62000L)
            say13.run()
            Thread.sleep(2000L)
            record = "Group_4_Speech"
            recordingDurationMillis = 300000L //5min record use extra 3 min for the perfect visualization
            runOnUiThread { parallel() }
            Thread.sleep(301000L)
            say14.run()
            Thread.sleep(2000L)
            say17.run()
            Thread.sleep(2000L)
            record = "Group_4_Reaction"
            recordingDurationMillis = 20000L
            runOnUiThread { parallel() }
            Thread.sleep(21000L)
            say15.run()
            Thread.sleep(2000L)
            move()
        }
    }

    private fun parallel(){
        startCountdownTimer()
        Thread.sleep(1000L)
        startRecording()
    }


    override fun onRobotFocusLost() {
        TODO("Not yet implemented")
    }

    override fun onRobotFocusRefused(reason: String?) {
        TODO("Not yet implemented")
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val grantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (grantedPermissions.size < permissions.size) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    private fun startRecording() {


        val audioFile = File(audioFolderPath)
        if (!audioFile.exists()) {
            audioFile.mkdirs()
        }

        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val audioFilePath = "$audioFolderPath $record.3gp"
        mediaRecorder?.setOutputFile(audioFilePath)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {


        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun playAudio() {
        mediaPlayer4 = MediaPlayer()
        val audioFilePath = "$audioFolderPath $record.3gp"


        try {
            mediaPlayer4?.setDataSource(audioFilePath)
            mediaPlayer4?.prepare()
            mediaPlayer4?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startCountdownTimer() {

        val recordStatus:TextView = findViewById(R.id.status4)
        cancelCountdownTimer()
        playStartMusic()
        if ( record == "Group_4_Speech" || record == "Group_4_Reaction"){
            recordStatus.text = "Recording...."
        }


        countdownTimer = object : CountDownTimer(recordingDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val timeText = String.format("%02d:%02d", minutes, seconds)
                timerTextView.text = "$timeText"
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                playEndMusic()
                if ( record == "Group_4_Speech" || record == "Group_4_Reaction"){
                    stopRecording()
                    recordStatus.text = "Stop"
                }
                Thread.sleep(2000L)
                mediaPlayer4?.release()
                mediaPlayer4 = null
                startMusicPlayer4?.release()
                startMusicPlayer4 = null
                endMusicPlayer4?.release()
                endMusicPlayer4 = null

            }
        }.start()
    }

    private fun cancelCountdownTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
        timerTextView.text = "Timer: 00:00"
    }

    private fun playStartMusic() {
        startMusicPlayer4 = MediaPlayer.create(this, R.raw.bell)
        startMusicPlayer4?.start()
    }

    private fun playEndMusic() {
        endMusicPlayer4 = MediaPlayer.create(this, R.raw.gong)
        endMusicPlayer4?.start()
    }


    override fun onStop() {
        super.onStop()
        mediaPlayer4?.release()
        mediaPlayer4 = null
        startMusicPlayer4?.release()
        startMusicPlayer4 = null
        endMusicPlayer4?.release()
        endMusicPlayer4 = null

    }

}