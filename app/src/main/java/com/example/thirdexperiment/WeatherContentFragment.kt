package com.example.thirdexperiment

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.caverock.androidsvg.SVG
import com.example.thirdexperiment.databinding.WeatherContentFragBinding
import java.io.Serializable
import java.util.Calendar

/*
* 存放天气细节信息的fragment
* 可以进行复用
* 适应手机和平板不同宽度设备*/
class WeatherContentFragment:Fragment() {
    private lateinit var binding: WeatherContentFragBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= WeatherContentFragBinding.inflate(inflater,container,false)
        return binding.root
    }
    @SuppressLint("DiscouragedApi")
    fun refresh(weather: Weather) {
        binding.contentLayout.visibility = View.VISIBLE
        binding.day.text=weather.dayOfWeek
        binding.date.text=weather.date
        binding.maxTemp.text=weather.max_temp
        binding.minTemp.text=weather.min_temp
        binding.humidity.text=weather.humidity
        binding.pressure.text=weather.pressure
        binding.wind.text="Wind: "+weather.windSpeedDay+" "+weather.windDirDay
        val time=System.currentTimeMillis()
        val mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        val hour = mCalendar.get(Calendar.HOUR);
        val apm = mCalendar.get(Calendar.AM_PM);
        var imageCode=""
        if(hour>=6 && apm==1){
            binding.desc.text=weather.textNight
            imageCode="black_"+weather.iconNight
        }else{
            binding.desc.text=weather.textDay
            imageCode="black_"+weather.iconDay
        }
        val resId:Int=resources.getIdentifier(imageCode, "raw", requireActivity().packageName)
        val svgInputStream = resources.openRawResource(resId)
        val svg = SVG.getFromInputStream(svgInputStream)
        val pictureDrawable = PictureDrawable(svg.renderToPicture())
        binding.icon.setImageDrawable(pictureDrawable)
    }
}