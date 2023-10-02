package com.example.presentationinterview

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.util.PhraseSetUtil
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.lang.Math.log10
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity6 : AppCompatActivity(), RobotLifecycleCallbacks {

    private var winner = "x"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        QiSDK.register(this, this)

    }

    private fun move(){

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }


    override fun onRobotFocusGained(qiContext: QiContext?) {

        val result:TextView = findViewById(R.id.resultTextView)

        val say151 = SayBuilder.with(qiContext)
            .withText("Ich habe die Ergebnisse ausgewertet. Bitte schreiben " +
                    "Sie meine Server IP Adresse und sagen Sie ja oder fertig, um die Ergebnisse zu sehen.")
            .build()

        val say152 = SayBuilder.with(qiContext)
            .withText("Bevor ich euch die Gruppe nenne, die " +
                    "die beste Präsentation abgeliefert hat, möchte ich klarstellen.")
            .build()

        val say18 = SayBuilder.with(qiContext)
            .withText("Ihr habt das alle sehr gut gemacht.")
            .build()

        val say19 = SayBuilder.with(qiContext)
            .withText( "Ihr hattet wenig Zeit, musstet spontan planen und habt die Aufgabe trotzdem gut gemeistert.")
            .build()


        val say20 = SayBuilder.with(qiContext)
            .withText( "Ich hoffe, ihr hattet Spaß und konntet ein bisschen was lernen. Bis zum nächsten Mal.")
            .build()

        val animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.hello_a001) // Replace with your animation resource
            .build()

        val animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation)
            .build()

        val phraseSetYes = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
            .withTexts("fertig", "abgeschlossen", "fertiggestellt", "ja") // Add the phrases Pepper will listen to.
            .build()

        val listen = ListenBuilder.with(qiContext) // Create the builder with the QiContext.
            .withPhraseSets(phraseSetYes) // Set the PhraseSets to listen to.
            .build()


        say151.run()

        /***runBlocking {
            say151.run()
            delay(2000)
            say152.run()
            delay(2000)
            say18.run()
            delay(2000)
            say19.run()
            delay(2000)
            resultIntensity()
            delay(2000)
            winner = result.text[8].toString()
            delay(1000)
            val say191 = SayBuilder.with(qiContext)
                .withText( "Am lautesten geklatscht wurde bei Gruppe $winner. Herzlichen Glückwunsch!")
                .build()
            delay(1000)
            say191.run()
            delay(1000)
            say20.run()
            delay(1000)
            // ending
            animate.run()
        }***/

        val listenResult = listen.run()

        val humanText = listenResult.heardPhrase.text

        val matchedPhraseSet = listenResult.matchedPhraseSet


        if (PhraseSetUtil.equals(matchedPhraseSet, phraseSetYes)){
            runBlocking {
                say152.run()
                delay(1000)
                say18.run()
                delay(1000)
                say19.run()
                delay(1000)
                resultIntensity()
                delay(7000)
                winner = result.text[8].toString()
                delay(1000)
                val say191 = SayBuilder.with(qiContext)
                    .withText( "Am lautesten geklatscht wurde bei Gruppe $winner. Herzlichen Glückwunsch!")
                    .build()
                delay(1000)
                say191.run()
                delay(2000)
                say20.run()
                delay(2000)
                // ending
                animate.run()
                Thread.sleep(7000L)
                move()
            }

        }


    }

    override fun onRobotFocusLost() {
        TODO("Not yet implemented")
    }

    override fun onRobotFocusRefused(reason: String?) {
        TODO("Not yet implemented")
    }

    private fun resultIntensity(){
        val audioFolderPath =
            "${Environment.getExternalStorageDirectory().absolutePath}/MyAudioFolder/"

        val audioFiles = File(audioFolderPath).listFiles { file -> file.isFile && file.name.endsWith(".3gp") }

        val client = OkHttpClient()
        val server:EditText = findViewById(R.id.serverText)
        val serverIP = server.text.toString()
        val serverUrl = "http://$serverIP:5000/upload_audio"

        if (audioFiles.isNotEmpty()) {
            for (audioFile in audioFiles) {
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", audioFile.name, audioFile.asRequestBody("audio/3gpp".toMediaType()))
                    .build()

                val request = Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle the failure (e.g., network error)
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call, response: Response) {
                        val xmlResponse = response.body?.string()
                        // Parse the XML response as needed
                        runOnUiThread {
                            val result:TextView = findViewById(R.id.resultTextView)
                            val winGroup:TextView = findViewById(R.id.winner)
                            result.text = xmlResponse
                            winner = result.text[8].toString()
                            winGroup.text = "Herzlichen Glückwunsch! Gruppe: $winner"
                            // Update UI with the response
                        }
                    }
                })
            }
        }
    }

}