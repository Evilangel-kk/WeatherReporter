package com.example.thirdexperiment

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

class TodayContentFragment:Fragment() {
    private lateinit var binding:TodayContentFragBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= TodayContentFragBinding.inflate(inflater,container,false)
        return binding.root
    }

    fun refresh(weather: Weather) {
        binding.dayAnddate.text=weather.dayOfWeek+","+weather.date
        binding.maxTemp.text=weather.max_temp
        binding.minTemp.text=weather.min_temp
        binding.desc.text=weather.textDay

        var imageCode="white_"+weather.iconDay
        var resId:Int=resources.getIdentifier(imageCode, "raw", requireActivity().packageName)
        val svgInputStream = resources.openRawResource(resId)
        val svg = SVG.getFromInputStream(svgInputStream)
        val pictureDrawable = PictureDrawable(svg.renderToPicture())
        binding.icon.setImageDrawable(pictureDrawable)
    }
}