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
import com.aldebaran.qi.sdk.builder.SayBuilder
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException

class MainActivity3 : AppCompatActivity() , RobotLifecycleCallbacks{

    private val audioFolderPath =
        "${Environment.getExternalStorageDirectory().absolutePath}/MyAudioFolder/"
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var countdownTimer: CountDownTimer? = null


    private lateinit var timerTextView: TextView

    private var recordingDurationMillis: Long = 30000 // 30 seconds
    private var startMusicPlayer2: MediaPlayer? = null
    private var endMusicPlayer2: MediaPlayer? = null
    private var record = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        hideSystemUI()
        QiSDK.register(this, this)
    }
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

    override fun onRobotFocusGained(qiContext: QiContext?) {
        val say6 = SayBuilder.with(qiContext)
            .withText("Danke, ich habe eure Klatsch Daten gespeichert. und wenn alle " +
                    "4 Präsentationen durch sind, werde ich euch sagen, welche euch am besten gefallen hat.")
            .build()

        val say61 = SayBuilder.with(qiContext)
            .withText("Kommen wir nun zur Gruppe 2.")
            .build()

        val say9 = SayBuilder.with(qiContext)
            .withText(
                "Mitglieder der Gruppe 1 setzt euch " +
                        "bitte wieder und Mitglieder der Gruppe 2, kommt nach vorne und bereitet eure Präsentation vor. Ihr " +
                        " habt eine Minute Zeit."
            )
            .build()

        val say7 = SayBuilder.with(qiContext)
            .withText("Die Vorbereitungszeit ist um. Gruppe 2 startet nun mit eurer Präsentation.")
            .build()

        val say8 = SayBuilder.with(qiContext)
            .withText("Die 5 Minuten sind um. Studierende, wie hat euch die Präsentation gefallen?")
            .build()

        val say81 = SayBuilder.with(qiContext)
            .withText("Es gilt weiterhin, je lauter ihr klatscht, desto besser hat euch die Präsentation gefallen." +
                    "Bitte klatscht jetzt.")
            .build()

        timerTextView = findViewById(R.id.timerTextView2)

        // Request permission to record audio and write to external storage
        requestPermissions()


        runBlocking {
            say6.run()
            Thread.sleep(1000L)
            say61.run()
            Thread.sleep(1000L)
            say9.run()
            Thread.sleep(1000L)
            recordingDurationMillis = 60000L // 1 min preparation
            runOnUiThread { startCountdownTimer() }
            Thread.sleep(61000L)
            say7.run()
            Thread.sleep(1000L)
            record = "Group_2_Speech"
            recordingDurationMillis = 300000L //5min record use extra 3 min for the perfect visualization
            runOnUiThread { parallel() }
            Thread.sleep(301000L)
            say8.run()
            Thread.sleep(1000L)
            say81.run()
            Thread.sleep(1500L)
            record = "Group_2_Reaction"
            recordingDurationMillis = 30000L
            runOnUiThread { parallel() }
            Thread.sleep(31000L)
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
        mediaPlayer2 = MediaPlayer()
        val audioFilePath = "$audioFolderPath $record.3gp"


        try {
            mediaPlayer2?.setDataSource(audioFilePath)
            mediaPlayer2?.prepare()
            mediaPlayer2?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startCountdownTimer() {

        val recordStatus:TextView = findViewById(R.id.status2)
        cancelCountdownTimer()
        playStartMusic()
        if ( record == "Group_2_Speech" || record == "Group_2_Reaction"){
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
                if ( record == "Group_2_Speech" || record == "Group_2_Reaction"){
                    stopRecording()
                    recordStatus.text = "Stop"
                }

                Thread.sleep(2000L)

                startMusicPlayer2?.release()
                startMusicPlayer2= null

                mediaPlayer2?.release()
                mediaPlayer2=null

                endMusicPlayer2?.release()
                endMusicPlayer2= null

            }
        }.start()
    }

    private fun cancelCountdownTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
        timerTextView.text = "Timer: 00:00"
    }

    private fun playStartMusic() {
        startMusicPlayer2 = MediaPlayer.create(this, R.raw.bell)
        startMusicPlayer2?.start()
    }

    private fun playEndMusic() {
        endMusicPlayer2 = MediaPlayer.create(this, R.raw.gong)
        endMusicPlayer2?.start()
    }


    private fun move(){


        val intent = Intent(this, MainActivity4::class.java)
        startActivity(intent)
    }
}