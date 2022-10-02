package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity() {

    var baseCurrency = "EUR"
    var convertedToCurrency = "USD"
    var conversionRates= 0f //float
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //setContentView(R.layout.activity_main)

        spinnerSetup()
        textChanged()
    }
    private fun textChanged()
    {
        binding.etFirstConversion.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(p0: Editable?) {
                try{
                    getApiResult()
                }catch (e:Exception){
                    Log.e("Main", "$e")
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Main", "Before Text Changed")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Main", "On Text Changed")
            }

        })
    }

    private fun getApiResult()
    {

        if (binding.etFirstConversion !=null && binding.etFirstConversion.text.isNotEmpty() && binding.etFirstConversion.text.isNotBlank()) {
            val API = "https://api.exchangerate.host/convert?from=$baseCurrency&to=$convertedToCurrency"

            if (baseCurrency == convertedToCurrency) {
                Toast.makeText(
                    applicationContext,
                    "Cannot convert to same currency",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    Log.d("Main", "test")
                    try {
                        val apiResult =
                            URL(API).readText() //takes URL from the internet reads it and give us the information each time we call it
                        val jsonObject = JSONObject(apiResult)
                        //conversionRates = jsonObject.getJSONObject("result").getString(convertedToCurrency).toFloat()/// NEED WORK HERE
                        //val conversionRates1 = jsonObject.getDouble("result")
                        conversionRates = jsonObject.getDouble("result").toFloat()
                        Log.d("Main", "$conversionRates")
                        Log.d("Main", apiResult)

                        withContext(Dispatchers.Main)
                        {
                            val text = ((binding.etFirstConversion.text.toString().toFloat()) * conversionRates).toString()
                            binding.etSecondConversion?.setText(text)
                        }//update the UI
                    } catch (e: Exception) {
                        Log.e("Main", "$e")
                        Log.d("Main", "test")
                    }
                }
            }
        }
    }
    private fun spinnerSetup()
    {

        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)
        ArrayAdapter.createFromResource(
            this,
            R.array.Currency1,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.Currency2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner2.adapter = adapter
        }
        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                baseCurrency = p0?.getItemAtPosition(p2).toString()
                getApiResult()
            }

        })
        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertedToCurrency = p0?.getItemAtPosition(p2).toString()
                getApiResult()
            }

        })

    }
}