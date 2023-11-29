package com.example.thirdexperiment

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.caverock.androidsvg.SVG
import com.example.thirdexperiment.databinding.TodayContentFragBinding
import com.example.thirdexperiment.databinding.WeatherContentFragBinding
import java.io.InputStream
import java.util.Calendar
import kotlin.text.Typography.amp

/*
* 只有在手机上才会显示的fragment
* 展示今天的天气信息*/
class TodayContentFragment:Fragment() {
    private lateinit var binding:TodayContentFragBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= TodayContentFragBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("DiscouragedApi")
    fun refresh(weather: Weather) {
        binding.dayAnddate.text=weather.dayOfWeek+","+weather.date
        binding.maxTemp.text=weather.max_temp
        binding.minTemp.text=weather.min_temp
        val time=System.currentTimeMillis()
        val mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        val hour = mCalendar.get(Calendar.HOUR);
        val apm = mCalendar.get(Calendar.AM_PM);
        var imageCode=""
        if(hour>=6 && apm==1){
            binding.desc.text=weather.textNight
            imageCode="white_"+weather.iconNight
        }else{
            binding.desc.text=weather.textDay
            imageCode="white_"+weather.iconDay
        }
        val resId:Int=resources.getIdentifier(imageCode, "raw", requireActivity().packageName)
        val svgInputStream = resources.openRawResource(resId)
        val svg = SVG.getFromInputStream(svgInputStream)
        val pictureDrawable = PictureDrawable(svg.renderToPicture())
        binding.icon.setImageDrawable(pictureDrawable)
    }
}