package com.example.thirdexperiment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdexperiment.databinding.ActivityWeatherContentBinding

/*
* 天气细节Activity*/
class WeatherContentActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWeatherContentBinding
    companion object {
        fun actionStart(context: Context, weather: Weather) {
            val intent = Intent(context, WeatherContentActivity::class.java).apply {
                putExtra("weather", weather)
            }
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title="Details"
        binding= ActivityWeatherContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val weather = intent.getSerializableExtra("weather") as Weather // 获取传入的天气
        if (weather != null) {
            val fragment = supportFragmentManager.findFragmentById(R.id.weatherContentFrag) as WeatherContentFragment
            fragment.refresh(weather) //刷新WeatherContentFragment界面
        }
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}