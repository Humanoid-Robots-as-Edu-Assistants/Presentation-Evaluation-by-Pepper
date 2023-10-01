package com.example.presentationinterview

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.util.PhraseSetUtil
import kotlinx.coroutines.*
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.lang.Runnable
import java.sql.Time
import kotlin.math.log10
import android.content.Intent as Intent


class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks {

    private lateinit var timerTextView: TextView
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var initialTimeInMillis: Long = 420000 // 7 min
    var timeText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    private fun move(){

        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)

    }

    private fun startTimer() {
        countDownTimer?.cancel() // Cancel any existing timer
        val mediaPlayerBell = MediaPlayer.create(this, R.raw.bell)
        val mediaPlayerGong = MediaPlayer.create(this, R.raw.gong)
        val mediaPlayerBack = MediaPlayer.create(this, R.raw.back)
        val mediaPlayerTwo = MediaPlayer.create(this, R.raw.two)

        mediaPlayerBell?.start()
        mediaPlayerBack?.start()



        countDownTimer = object : CountDownTimer(initialTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
                //if (timeText == "00:10") {
                    ///mediaPlayerTwo?.start()
                //}

            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimerText()
                mediaPlayerBack.stop()
                mediaPlayerGong.start()
                Thread.sleep(2000L)
                mediaPlayerGong.stop()
                mediaPlayerBack.release()
                mediaPlayerGong.release()
                Thread.sleep(1000L)
                move()
                Thread.sleep(1000L)


            }
        }.start()
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        timeText = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text = timeText
        //println("Time: $timeText")

    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        val say = SayBuilder.with(qiContext)
            .withText("Können wir starten?")
            .build()

        val animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.hello_a001) // Replace with your animation resource
            .build()

        val animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation)
            .build()

// running function sequentially one after another
        /***runBlocking {
            launch(Dispatchers.IO) {
                //animate.run()
                say.async().run()
                runOnUiThread(Runnable { startTimer() })
            }
        }***/

        timerTextView = findViewById(R.id.timerTextView)



        // Create the PhraseSet 1.
        val phraseSetYes = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
            .withTexts("ja") // Add the phrases Pepper will listen to.
            .build() // Build the PhraseSet.

        // Create the PhraseSet 2.
        val phraseSetGroup = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
            .withTexts("Gruppe eins", "Gruppe zwei", "Gruppe drei", "Gruppe vier") // Add the phrases Pepper will listen to.
            .build()

        val listen = ListenBuilder.with(qiContext) // Create the builder with the QiContext.
            .withPhraseSets(phraseSetYes, phraseSetGroup) // Set the PhraseSets to listen to.
            .build() // Build the listen action.

        say.run()
        //move()

        val listenResult = listen.run()

        val humanText = listenResult.heardPhrase.text

        val matchedPhraseSet = listenResult.matchedPhraseSet


        if (PhraseSetUtil.equals(matchedPhraseSet, phraseSetYes)) {
            val say2 = SayBuilder.with(qiContext)
                .withText("Hallo, Studierende! Ich bin Pepper. Auch ich bin ein humanoider Roboter. Meine Aufgabe heute ist es, mit euch im kleinen Rahmen das Präsentieren zu üben.")
                .build()


            val say21 = SayBuilder.with(qiContext)
                .withText("Vorträge zu halten, muss geübt sein und ich weiß aus Erfahrung, dass manche Studierende damit ihre Schwierigkeiten haben.")
                .build()

            val say23 = SayBuilder.with(qiContext)
                .withText("Keine Sorge, das ist heute nur eine kleine Übung, die euch zeigen soll: Vor anderen Menschen sprechen, ist gar nicht schlimm. Wir sind doch alles nur Menschen, naja, ich und die anderen Roboter hier ausgenommen.")
                .build()

            val say24 = SayBuilder.with(qiContext)
                .withText("Dann erkläre ich mal, was ihr nun tun sollt. Im letzten Teil habt ihr in Gruppen Informationen über verschiedene Themen herausgefunden. Jeweils zwei Gruppen haben dabei Informationen über das gleiche Thema herausgefunden.")
                .build()

            val say25 = SayBuilder.with(qiContext)
                .withText("Findet euch in den 4 Themengruppen zusammen und tauscht eure Informationen aus. Habt ihr das Gleiche herausgefunden oder ergänzen sich eure Informationen?")
                .build()

            val say26 = SayBuilder.with(qiContext)
                .withText("Gruppe 1a findet sich mit Gruppe 1b zusammen. Gruppe 2a mit Gruppe 2b. Gruppe 3a mit Gruppe 3b. und Gruppe 4a mit Gruppe 4b.")
                .build()

            val say27 = SayBuilder.with(qiContext)
                .withText("Tauscht euch aus und fasst die Informationen so zusammen, dass ihr als Gruppe 5 Minuten eure Informationen vor allen vortragen könnt.")
                .build()

            val say28 = SayBuilder.with(qiContext)
                .withText("Ihr seid ziemlich viele Personen in einer Gruppe. Deshalb dürft ihr selbst entscheiden, wer vorträgt. Das kann nur eine Person sein, oder auch alle.")
                .build()

            val say29 = SayBuilder.with(qiContext)
                .withText("Am Ende der Präsentationen haben eure Mitstudierenden die Möglichkeit, euch kurz Feedback zu geben.")
                .build()

            val say210 = SayBuilder.with(qiContext)
                .withText("Ihr habt nun genau 7 Minuten, um euch mit eurer Partnergruppe auszutauschen und die Präsentation vorzutragen.")
                .build()

            val say211 = SayBuilder.with(qiContext)
                .withText("Ich habe ein Handout für euch vorbereitet. auf welchem wichtige Tipps " +
                        "und Tricks zu einer erfolgreichen Präsentation stehen. Nutzt diese Tipps gerne")
                .build()


            val say212 = SayBuilder.with(qiContext)
                .withText("Wenn die Zeit um ist. rufe ich die erste Gruppe auf. Ihr " +
                        " habt dann 1 Minute um euch aufzustellen. und dann 5 Minuten um zu präsentieren.")
                .build()


            val say213 = SayBuilder.with(qiContext)
                .withText("Ich fange gleich an, die 7 Minuten Vorbereitungszeit zu stoppen.")
                .build()

            val say216 = SayBuilder.with(qiContext)
                .withText("Ich gebe euch einen Hinweis, wenn ihr nur noch 2 Minuten Zeit habt, indem ich mit meinen beiden Armen winke und einen Hinweiston von mir gebe.")
                .build()

            val say214 = SayBuilder.with(qiContext)
                .withText("Die Zeit startet, jetz")
                .build()

            val say215 = SayBuilder.with(qiContext)
                .withText("ihr habt noch zwei minuten")
                .build()


            // running function sequentially one after another
            runBlocking {
                launch(Dispatchers.IO) {
                    animate.async().run()
                    say2.run()
                    Thread.sleep(1000L)
                    say21.run()
                    Thread.sleep(1000L)
                    say23.run()
                    Thread.sleep(1000L)
                    say24.run()
                    Thread.sleep(1000L)
                    say25.run()
                    Thread.sleep(1000L)
                    say26.run()
                    Thread.sleep(1000L)
                    say27.run()
                    Thread.sleep(1000L)
                    say28.run()
                    Thread.sleep(1000L)
                    say29.run()
                    Thread.sleep(1000L)
                    say210.run()
                    Thread.sleep(1000L)
                    say211.run()
                    Thread.sleep(1000L)
                    say212.run()
                    Thread.sleep(1000L)
                    say213.run()
                    Thread.sleep(1000L)
                    say216.run()
                    Thread.sleep(1000L)
                    say214.run()
                    runOnUiThread(Runnable {startTimer() })
                    Thread.sleep(295000L)
                    animate.run()
                    say215.async().run()

                }
            }

        }

    }

    override fun onRobotFocusLost() {
        TODO("Not yet implemented")
    }

    override fun onRobotFocusRefused(reason: String?) {
        TODO("Not yet implemented")
    }

}