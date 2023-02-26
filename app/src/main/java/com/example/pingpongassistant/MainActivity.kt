package com.example.pingpongassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.pingpongassistant.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private lateinit var textField: TextView
    private lateinit var button: Button

    private var myScore: Int = 0
    private var theirScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }

        textField = findViewById(R.id.textview_first)
        button = findViewById(R.id.button_first)

        initSpeechRecogniser()
        initButtonLogic()
        initTextToSpeech()
    }

    private fun initTextToSpeech() {
        tts = TextToSpeech(applicationContext, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
            }
        })
        tts.language = Locale.UK
    }

    private fun initButtonLogic() {
        val button = findViewById<Button>(R.id.button_first)
        button.setOnClickListener(object : View.OnClickListener {
            private var listening: Boolean = false

            override fun onClick(p0: View?) {
                if (!listening) {
                    button.setBackgroundColor(Color.GREEN)
                    button.setText(R.string.button_text_recording)
                    speechRecognizer.startListening(intent)
                } else {
                    button.setBackgroundColor(Color.RED)
                    button.setText(R.string.button_text_start)
                    speechRecognizer.stopListening()
                }
                listening = !listening
            }
        })
    }

    private fun initSpeechRecogniser() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(p0: Int) {
            }

            override fun onResults(results: Bundle?) {
                speechRecognizer.stopListening()

                println("resultinggegeg")
                if (results != null) {
                    val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val recorded = data?.get(0)?.lowercase(Locale.getDefault())
                    recorded?.let {
                        if(recorded == "game") {
                            myScore = 0
                            theirScore = 0
                            tts.speak("STARTING NEW GAME", TextToSpeech.QUEUE_FLUSH, null)
                        } else if(recorded.startsWith("p")) {
                            myScore++
                            tts.speak("SCORE: $myScore to $theirScore", TextToSpeech.QUEUE_FLUSH, null)
                        } else if(recorded.startsWith("n")) {
                            theirScore++
                            tts.speak("SCORE: $myScore to $theirScore", TextToSpeech.QUEUE_FLUSH, null)
                        }

                        if(abs(myScore - theirScore) >= 2 && myScore >= 11) {
                            myScore = 0
                            theirScore = 0
                            tts.speak("GAME OVER, DIETER WON", TextToSpeech.QUEUE_FLUSH, null)
                        }
                        if(abs(myScore - theirScore) >= 2 && theirScore >= 11) {
                            myScore = 0
                            theirScore = 0
                            tts.speak("GAME OVER, FRANK WON", TextToSpeech.QUEUE_FLUSH, null)
                        }

                        textField.text = recorded;
//                        tts.speak(recorded, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }

                speechRecognizer.startListening(intent)
            }

            override fun onPartialResults(results: Bundle) {

            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted, thanks!", Toast.LENGTH_SHORT).show()
        }
    }
}