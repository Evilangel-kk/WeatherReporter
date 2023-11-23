package com.example.thirdexperiment

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.caverock.androidsvg.SVG
import com.example.thirdexperiment.databinding.WeatherContentFragBinding
import java.io.Serializable

class WeatherContentFragment:Fragment() {
    private lateinit var binding: WeatherContentFragBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= WeatherContentFragBinding.inflate(inflater,container,false)
        return binding.root
    }
    fun refresh(weather: Weather) {
        binding.contentLayout.visibility = View.VISIBLE
        binding.day.text=weather.dayOfWeek
        binding.date.text=weather.date
        binding.maxTemp.text=weather.max_temp
        binding.minTemp.text=weather.min_temp
        binding.desc.text=weather.textDay
        binding.humidity.text=weather.humidity
        binding.pressure.text=weather.pressure
        binding.wind.text="Wind: "+weather.windSpeedDay+" "+weather.windDirDay

        var imageCode="black_"+weather.iconDay

        var resId:Int=resources.getIdentifier(imageCode, "raw", requireActivity().packageName)
        val svgInputStream = resources.openRawResource(resId)
        val svg = SVG.getFromInputStream(svgInputStream)
        val pictureDrawable = PictureDrawable(svg.renderToPicture())
        binding.icon.setImageDrawable(pictureDrawable)
    }
}