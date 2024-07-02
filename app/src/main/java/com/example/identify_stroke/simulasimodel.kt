package com.example.identify_stroke

import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class simulasimodel : AppCompatActivity() {
    private lateinit var interpreter: Interpreter
    private val mModelPath = "stroke.tflite"

    private lateinit var resultText: TextView
    private lateinit var gender: EditText
    private lateinit var age: EditText
    private lateinit var hypertension: EditText
    private lateinit var heart_disease: EditText
    private lateinit var ever_married: EditText
    private lateinit var work_type: EditText
    private lateinit var Residence_type: EditText
    private lateinit var avg_glucose_level: EditText
    private lateinit var bmi: EditText
    private lateinit var smoking_status: EditText
    private lateinit var checkButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasimodel)

        resultText = findViewById(R.id.txtResult)
        gender = findViewById(R.id.gender)
        age = findViewById(R.id.age)

        hypertension = findViewById(R.id.hypertension)
        heart_disease = findViewById(R.id.heartdisease)
        ever_married = findViewById(R.id.ever_married)

        work_type = findViewById(R.id.work_type)
        Residence_type = findViewById(R.id.Residence_type)
        avg_glucose_level = findViewById(R.id.avg_glucose_level)
        bmi = findViewById(R.id.bmi)
        smoking_status = findViewById(R.id.smoking_status)
        checkButton = findViewById(R.id.btnpredict)



        checkButton.setOnClickListener {
            var result = doInference(
                gender.text.toString(),
                age.text.toString(),
                hypertension.text.toString(),
                heart_disease.text.toString(),
                ever_married.text.toString(),
                work_type.text.toString(),
                Residence_type.text.toString(),
                avg_glucose_level.text.toString(),
                bmi.text.toString(),
                smoking_status.text.toString()
            )

            runOnUiThread {
                if (result < 1) {
                    resultText.text = "Pernah Stroke"
                } else if (result >= 1) {
                    resultText.text = "Tidak Pernah Stroke"
                }
            }

        }
        initInterpreter()

    }

    private fun initInterpreter() {
        val options = Interpreter.Options()
        options.setNumThreads(6)
        options.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(
        input1: String,
        input2: String,
        input3: String,
        input4: String,
        input5: String,
        input6: String,
        input7: String,
        input8: String,
        input9: String,
        input10: String


    ): Float {
        val inputVal = FloatArray(10)
        inputVal[0] = ((input1.toFloat()- 0.414286 ) /    0.493044).toFloat()

        inputVal[1] = ((input2.toFloat() -  43.226614) / 22.612647).toFloat()

        inputVal[2] = ((input3.toFloat()-  0.097456)/ 0.296607).toFloat()

        inputVal[3] = ((input4.toFloat()- 0.054012)/ 0.226063).toFloat()

        inputVal[4] = ((input5.toFloat()- 0.656164)/ 0.475034).toFloat()

        inputVal[5] = ((input6.toFloat()-    2.167710)/1.090293).toFloat()

        inputVal[6] = ((input7.toFloat()-  0.508023)/0.499985).toFloat()

        inputVal[7] = ((input8.toFloat()-   106.147677)/45.283560).toFloat()

        inputVal[8] = ((input9.toFloat()- 28.893237 ) /  7.698018).toFloat()

        inputVal[9] = ((input10.toFloat() - 1.376908) /1.071534   ).toFloat()

        val output = Array(1) {
            FloatArray(1)
        }
        Log.e("result", (output[0].toList() + "").toString())
        interpreter.run(inputVal, output)
        return output[0][0]
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
