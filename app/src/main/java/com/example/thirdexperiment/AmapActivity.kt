package com.example.thirdexperiment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.LatLng
import com.example.thirdexperiment.databinding.ActivityAmapBinding

/*
* AmapActivity页面用于展示地图
* 高德API获取地图*/
class AmapActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAmapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title="Map Location"
        binding=ActivityAmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.map.onCreate(savedInstanceState) // 此方法必须重写
        binding.map.map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Location.lat.toDouble(), Location.lon.toDouble()), 12f))
        // 添加返回按钮
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