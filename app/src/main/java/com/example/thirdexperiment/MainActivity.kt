package com.example.thirdexperiment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdexperiment.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title="SunShine"
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var isTwoPane = this.findViewById<View>(R.id.todayWeatherFrag) != null
        if(isTwoPane) {
            var weather=WeatherList.weather.get(0)
            if(weather!=null){
                val fragment = supportFragmentManager.findFragmentById(R.id.todayWeatherFrag) as TodayContentFragment
                fragment.refresh(weather) //刷新TodayContentFragment界面
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(menu!=null){
            menuInflater.inflate(R.menu.menu_optionmenu,menu)
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> startActivity(Intent(this,SettingsActivity::class.java))
//            R.id.MapLocation -> startActivity(Intent(this,MapLocationActivity::class.java))
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

}