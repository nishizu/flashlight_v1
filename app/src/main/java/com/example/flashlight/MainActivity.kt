package com.example.flashlight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar

//aggiunta
import android.content.Context
import android.hardware.camera2.CameraManager
import android.widget.ImageButton
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity() {

    //aggiunta
    var flashLightStatus: Boolean = false
    var a: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //aggiunta
        openFlashLight()

    }

    //aggiunta
    private fun openFlashLight() {

        //val on_off = findViewById<Button>(R.id.on_off)
        val on_off = findViewById<ImageButton>(R.id.on_off_img)

        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        on_off.setOnClickListener {
            a = !a
            blinkingled(seekBar.progress.toLong())
            on_off.setImageResource(R.drawable.power_on)
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
                val on_off = findViewById<ImageButton>(R.id.on_off_img)

                on_off.setImageResource(R.drawable.power_off)

            }
        )
        t1.start()
    }

}