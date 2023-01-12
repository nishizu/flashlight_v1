package com.example.flashlight

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {

    // REGION New Variables

    // Status of the button
    var isButtonActive: Boolean = false
    // Status of the flashlight
    var isFlashLightOn: Boolean = false
    // handler for the timer
    var timerHandle = Timer()
    // handler for the seekBar
    lateinit var seekBar: SeekBar
    // handler for the on/off button
    lateinit var onOffButton : ImageButton
    // bool for the timer
    var isTimerRunning : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // new init method to initialise public handles and onClicks
        initFlashLight()
    }

    private fun initFlashLight(){
        // Setup the handles
        onOffButton = findViewById(R.id.on_off_button)
        seekBar = findViewById(R.id.seekBar)
        // Setup the onCLick listeners

        onOffButton.setOnClickListener {
            onFlashLightClicked()
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                onSeekBarChanged()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })

    }

    fun onSeekBarChanged(){

        //when button is active, reset timer for blink speed change or stop timer and turn on light
        val repetitionValue = (seekBar.progress.toLong() + 1 )
        if (isButtonActive) {
            if(repetitionValue > 1){
                startTimer()
            } else {
                stopTimer()
                ledon()
            }
        }
    }

    fun onFlashLightClicked() {

        val repetitionValue = (seekBar.progress.toLong() + 1 )

        Log.i("flashLight", "FlashLight clicked")

        //if button is active - stop and turn off
        if (isButtonActive) {
            stopTimer()
            ledoff()
            onOffButton.setImageResource(R.drawable.power_off)

        //if button is inactive - turn on and start timer if blink is needed
        } else {
            ledon()
            onOffButton.setImageResource(R.drawable.power_on)
            if(repetitionValue > 1) {
                startTimer()
            }
        }
        isButtonActive = !isButtonActive
    }

    //turn led on and record status
    private fun ledon(){

        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]

        Log.i("flashLight", "SHOW power ON")
        cameraManager.setTorchMode(cameraId, true)
        isFlashLightOn = true

    }

    //turn led off and record status
    private fun ledoff(){

        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]

        Log.i("flashLight", "SHOW power off")
        cameraManager.setTorchMode(cameraId, false)
        isFlashLightOn = false

    }

    //invert hardware led status
    private fun switchFlashLight() {
        if(isFlashLightOn) {
            ledoff()
        } else {
            ledon()
        }
    }

    private  fun startTimer(){
        Log.i("flashLight", "startTimer")

        if(isTimerRunning){
            // Remember to cancel the previous timer if we start timer without stopping
            Log.i("flashLight", "timer was running. cancel")
            stopTimer()
        }

        Log.i("flashLight", "timer now true")
        isTimerRunning = true

        // Repetition value here so it's exposed, we can change it print it etc
        // We add +1 to account for the seekbar starting to count from 0 and then transform in milliseconds

        val repetitionValue = (seekBar.progress.toLong() + 1 ) * 300
        Log.i("flashLight", "repetition value is $repetitionValue")

        timerHandle.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                    switchFlashLight()
            }
        }, repetitionValue, repetitionValue)
        // first repetitionValue is the delay after which to run the callback
        // second is the actual repetition value for the interval

    }

    private fun stopTimer(){

        Log.i("flashLight", "stopTimer")
        if(isTimerRunning){
            // Cancel and reset the handle + set bool to false
            timerHandle.cancel()
            isTimerRunning = false
            timerHandle = Timer()
        }
    }



}