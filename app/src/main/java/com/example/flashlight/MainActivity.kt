package com.example.flashlight

//aggiunta
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {

    //aggiunta
    var flashLightStatus: Boolean = false
    var a: Boolean = false

    // REGION New Variables

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
    // counter to stop blink after x repetitions (for debug)
    var counter : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //aggiunta
        //openFlashLight()

        // new init method to initialise public handles and onClicks
        initFlashLight()
    }

    private fun initFlashLight(){
        // Setup the handles
        onOffButton = findViewById<ImageButton>(R.id.on_off_button)
        seekBar = findViewById<SeekBar>(R.id.seekBar)
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
        stopTimer()
        if(isFlashLightOn){
            switchFlashLight()
        }
        startTimer()
    }

    fun onFlashLightClicked() {
        Log.i("flashLight", "FlashLight clicked")

        // First invert the state of the flashLight
        switchFlashLight()

        // Then start (or stop) the timer
        if (isFlashLightOn){
            startTimer()
        } else {
            stopTimer()
        }

        // TODO: When a timer is running
        //  if i press the button while flashlight is on, it will turn off (correct)
        //  but if i press it while it's off, it will turn it on and restart the timer (wrong)
        // Solution 1: have a second bool for the status of the button not touched by the timer
        // Solution 2: decouple button for starting flashlight from the image

    }

    private fun switchFlashLight(){
        isFlashLightOn = !isFlashLightOn
        Log.i("flashLight", "Light is $isFlashLightOn")
        // After inverting the value, we then execute our logic
        // In this case, we change source of the image
        // and the torch status

        //TODO: consider if toggling visibility of two images
        // might be better than setImgeResource every time
        if(isFlashLightOn){
            Log.i("flashLight", "SHOW power ON")
            onOffButton.setImageResource(R.drawable.power_on)
            // cameraManager.setTorchMode(cameraId, true)
        }
        else{
            Log.i("flashLight", "SHOW power off")
            onOffButton.setImageResource(R.drawable.power_off)
            // cameraManager.setTorchMode(cameraId, false)
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
        isTimerRunning = true;

        // Repetition value here so it's exposed, we can change it print it etc
        // We add +1 to account for the seekbar starting to count from 0 and then transform in milliseconds
        var repetitionValue = (seekBar.progress.toLong() + 1 ) * 1000
        Log.i("flashLight", "repetition value is $repetitionValue")

        timerHandle.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Remover counter's if when finished debugging
                if(counter == 10){
                    stopTimer()
                } else {
                    Log.i("flashLight", "scheduled task PING $counter")
                    switchFlashLight()
                    counter++
                }
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
            isTimerRunning = false;
            timerHandle = Timer()
            counter = 0
        }
    }

    // TODO: remove
    // OLD CODE
    //aggiunta
    private fun openFlashLight() {

        //val on_off = findViewById<Button>(R.id.on_off)
        val onOffButton = findViewById<ImageButton>(R.id.on_off_button)

        val seekBar = findViewById<SeekBar>(R.id.seekBar)


        onOffButton.setOnClickListener {
            a = !a
            blinkingled(seekBar.progress.toLong())
            onOffButton.setImageResource(R.drawable.power_on)
        }
    }



    fun blinkingled(progress: Long) {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        val t1 = Thread(
            kotlinx.coroutines.Runnable {
                while (a) {
                    cameraManager.setTorchMode(cameraId, true)
                    flashLightStatus = true
                    if (progress > 0) {
                    Thread.sleep(progress*500)
                    cameraManager.setTorchMode(cameraId, false)
                    flashLightStatus = false
                    Thread.sleep(progress*500)
                    }

                }
                cameraManager.setTorchMode(cameraId, false)
                flashLightStatus = false
                val on_off = findViewById<ImageButton>(R.id.on_off_button)

                on_off.setImageResource(R.drawable.power_off)

            }
        )
        t1.start()
    }

}