package com.example.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//c0709d90051847bd7594da6f7cad1030
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Goa")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView= binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Apiinterface::class.java)
        var response =retrofit.getWeatherData(cityName,"","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody =response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature =responseBody.main.temp.toString()
                    val humidity =responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed.toString()
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet= responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure.toString()
                    val condition= responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp= responseBody.main.temp_max.toString()
                    val minTemp =responseBody.main.temp_min.toString()
                    binding.temperature.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxtemp.text="Max Temp: $maxTemp °C"
                    binding.mintemp.text="Min Temp: $minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.windSpeed.text="$windSpeed m/s"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityname.text="$cityName"
                    //Log.d("TAG", "onResponse: $temperature ")
                    changeImagesAccordingToWeatherConditions(condition)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImagesAccordingToWeatherConditions(conditions:String) {
        when(conditions){
            "Clouds", "Partly Clouds", "Overcast", "Mist", "Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear", "Sunny", "Clear Sky"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" ,"Rain","Rainy"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard","Snow" , "Snowy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {
        val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp:Long):String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
