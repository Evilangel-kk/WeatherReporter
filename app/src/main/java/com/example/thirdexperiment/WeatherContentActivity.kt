package com.example.thirdexperiment

import android.content.Context
import android.content.Intent
import android.net.Uri
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


    // 添加菜单栏
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(menu!=null){
            menuInflater.inflate(R.menu.menu_share,menu)
            menuInflater.inflate(R.menu.menu_optionmenu,menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
            R.id.mail_share->{
                sendEML(getWeatherInfo())
            }
            R.id.message_share->{
                sendSMS(getWeatherInfo())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendSMS(message: String) {
        var smsToUri:Uri = Uri.parse("smsto:");
        var intent = Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }

    private fun sendEML(message: String){
        var uri = Uri.parse("mailto:")
        var email = "2472047312@qq.com"
        var intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(Intent.EXTRA_CC, email) // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, "Weather Update"); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, message); // 正文
        startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }

    private fun getWeatherInfo():String{
        var w = WeatherList.weather[0];
        return w.dayOfWeek+"\nMaxTemp: "+w.max_temp+"\nMinTemp: "+w.min_temp+"\n"+w.textDay
    }
}