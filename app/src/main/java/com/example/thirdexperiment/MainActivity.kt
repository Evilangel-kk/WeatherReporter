package com.example.thirdexperiment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdexperiment.databinding.ActivityMainBinding

/*
* 主页面，根据设备的屏幕宽度可以自动的从layout或者layout-sw600dp文件夹里取出布局文件activity-main.xml*/
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title="SunShine"
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 判断是否存在fragment即可判断是哪种屏幕宽度的设备
        var isTwoPane = this.findViewById<View>(R.id.todayWeatherFrag) != null
        if(isTwoPane) {
            var weather=WeatherList.weather.get(0)
            if(weather!=null){
                // 将第一天的信息放入
                val fragment = supportFragmentManager.findFragmentById(R.id.todayWeatherFrag) as TodayContentFragment
                fragment.refresh(weather) //刷新TodayContentFragment界面
            }
        }
    }

    // 菜单注入
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(menu!=null){
            menuInflater.inflate(R.menu.menu_optionmenu,menu)
        }
        return true
    }
    // 为菜单项添加点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> startActivity(Intent(this,SettingsActivity::class.java))
            R.id.MapLocation -> startActivity(Intent(this,AmapActivity::class.java))
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}