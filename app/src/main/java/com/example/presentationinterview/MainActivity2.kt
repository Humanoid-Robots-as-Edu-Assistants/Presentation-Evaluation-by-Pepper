package com.example.presentationinterview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.SayBuilder
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity2 : AppCompatActivity(), RobotLifecycleCallbacks {

    private val audioFolderPath =
        "${Environment.getExternalStorageDirectory().absolutePath}/MyAudioFolder/"
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var countdownTimer: CountDownTimer? = null


    private lateinit var timerTextView: TextView

    private var recordingDurationMillis: Long = 30000 // 30 seconds
    private var startMusicPlayer: MediaPlayer? = null
    private var endMusicPlayer: MediaPlayer? = null
    private var record = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
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

        val say3 = SayBuilder.with(qiContext)
            .withText("Die 7 Minuten Vorbereitungszeit sind um. Hoffentlich konntet ihr alle eine gute Präsentation vorbereiten. " )
            .build()

        val say31 = SayBuilder.with(qiContext)
            .withText("Ich möchte mit euch gerne etwas ausprobieren." )
            .build()

        val say32 = SayBuilder.with(qiContext)
            .withText("Gerne würde ich erkennen können, wie gut euch die Präsentationen gefallen. Dafür versuche ich eurer " +
                    "Klatschen nach der Präsentation auszuwerten." )
            .build()

        val say33 = SayBuilder.with(qiContext)
            .withText("Leises Klatschen bedeutet, die Präsentation hat euch nicht so gut gefallen. und lautes Klatschen " +
                    "bedeutet, dass euch die Präsentation sehr gut gefallen hat." )
            .build()

        val say34 = SayBuilder.with(qiContext)
            .withText("Ich nehme das Klatschen nach jeder Präsentation auf, nachdem ich euch " +
                    "das Startsignal gebe und versuche, sie miteinander zu vergleichen. " )
            .build()

        val say35 = SayBuilder.with(qiContext)
            .withText("Vielleicht erkenne ich so, welche Präsentation euch am besten gefallen hat. Klatscht nach den " +
                    "Vorträgen erst, wenn ich sage. Bitte klatscht jetzt." )
            .build()

        val say6 = SayBuilder.with(qiContext)
            .withText("Fangen wir an! Die erste Gruppe präsentiert nun ihre Ergebnisse. Alle Teilnehmenden der Gruppe 1 kommt bitte nach vorne." +
                    " In einer Minute starte ich den Timer für eure 5 Minuten Vortragszeit.")
            .build()

        val say4 = SayBuilder.with(qiContext)
            .withText("Die Vorbereitungszeit ist um. Gruppe 1 startet nun mit eurer Präsentation.")
            .build()

        val say5 = SayBuilder.with(qiContext)
            .withText("Die 5 Minuten sind um. Studierende, wie hat euch die Präsentation gefallen? Ihr " +
                    " wisst, ich versuche das, durch euer Klatschen zu erkennen. Je lauter ihr klatscht, desto besser" +
                    "hat euch die Präsentation gefallen.")
            .build()

        val say7 = SayBuilder.with(qiContext)
            .withText("Bitte klatscht jetzt")
            .build()




        timerTextView = findViewById(R.id.timerTextView)

        // Request permission to record audio and write to external storage
        requestPermissions()


        runBlocking {
            say3.run()
            Thread.sleep(1000L)
            say31.run()
            Thread.sleep(1000L)
            say32.run()
            Thread.sleep(1000L)
            say33.run()
            Thread.sleep(1000L)
            say34.run()
            Thread.sleep(1000L)
            say35.run()
            Thread.sleep(1000L)
            say6.run()
            Thread.sleep(1000L)
            recordingDurationMillis = 60000L // 1 min preparation
            runOnUiThread { startCountdownTimer() }
            Thread.sleep(62000L)
            say4.run()
            Thread.sleep(2000L)
            record = "Group_1_Speech"
            recordingDurationMillis = 300000L //5min speech record
            runOnUiThread { parallel() }
            Thread.sleep(301000L)
            say5.run()
            Thread.sleep(2000L)
            say7.run()
            record = "Group_1_Reaction"
            recordingDurationMillis = 30000L // 30 sec clapping record
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
        mediaPlayer = MediaPlayer()
        val audioFilePath = "$audioFolderPath $record.3gp"


        try {
            mediaPlayer?.setDataSource(audioFilePath)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startCountdownTimer() {

        val recordStatus:TextView = findViewById(R.id.status)
        cancelCountdownTimer()
        playStartMusic()
        if ( record == "Group_1_Speech" || record == "Group_1_Reaction"){
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
                if ( record == "Group_1_Speech" || record == "Group_1_Reaction"){
                    stopRecording()
                    recordStatus.text = "Stop"
                }

                Thread.sleep(2000L)

                startMusicPlayer?.release()
                startMusicPlayer = null

                mediaPlayer?.release()
                mediaPlayer=null

                endMusicPlayer?.release()
                endMusicPlayer=null

            }
        }.start()
    }

    private fun cancelCountdownTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
        timerTextView.text = "Timer: 00:00"
    }

    private fun playStartMusic() {
        startMusicPlayer = MediaPlayer.create(this, R.raw.bell)
        startMusicPlayer?.start()
    }

    private fun playEndMusic() {
        endMusicPlayer = MediaPlayer.create(this, R.raw.gong)
        endMusicPlayer?.start()
    }


    private fun move(){

        val intent = Intent(this, MainActivity3::class.java)
        startActivity(intent)

    }

}