package com.heltonbustos.ejemploretrofit01

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.heltonbustos.ejemploretrofit01.databinding.ActivityMainBinding
import com.heltonbustos.ejemploretrofit01.retrofit.Raza
import com.heltonbustos.ejemploretrofit01.retrofit.RazaAPIService
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBuscar.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            //consultar API REST DOG
            buscarPorRaza(binding.txtRaza.text.toString())
        }
    }

    private fun buscarPorRaza(x: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val llamada: Response<Raza> =
                getRetrofit().create(RazaAPIService::class.java)
                    .getRecurso("$x/images")
            val objeto: Raza? = llamada.body()

            if (llamada.isSuccessful){
                Log.e("STATUS", objeto!!.status)
                Log.e("IMAGENES", objeto!!.imagenes.toString())

                binding.txtStatus.text = objeto!!.status

                val urlImage = URL(objeto!!.imagenes[0])
                val resultado: Deferred<Bitmap?> = async {
                    BitmapFactory.decodeStream(urlImage.openStream())
                }

                val bit: Bitmap? = resultado.await()

                runOnUiThread {
                    binding.imageView.setImageBitmap(bit)
                    Toast.makeText(applicationContext
                        , "EXITO"
                        , Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
            else{
                runOnUiThread {
                    binding.txtStatus.text = "Error"
                    Toast.makeText(applicationContext
                    , "No se encuentró esa RAZA"
                    , Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}