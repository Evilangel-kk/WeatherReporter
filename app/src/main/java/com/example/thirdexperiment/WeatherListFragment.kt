package com.example.thirdexperiment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdexperiment.databinding.WeatherItemBinding
import com.example.thirdexperiment.databinding.WeatherListFragBinding
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import org.json.JSONArray
import java.util.Calendar

/*
* 列表信息的fragment
* 可用于手机主页面下部以及平板主页面左侧*/
class WeatherListFragment:Fragment() {
    private lateinit var binding: WeatherListFragBinding
    private var isTwoPane=false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding=WeatherListFragBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isTwoPane = activity?.findViewById<View>(R.id.weatherContentLayout) != null
        val layoutManager = LinearLayoutManager(activity)
        binding.weatherListRecyclerView.layoutManager = layoutManager
        // 配置适配器
        val adapter = NewsAdapter(WeatherList.weather)
        binding.weatherListRecyclerView.adapter = adapter
    }

    inner class NewsAdapter(val weatherList: List<Weather>) :
        RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
        private var selectedItem=RecyclerView.NO_POSITION
        private lateinit var binding: WeatherItemBinding

        inner class ViewHolder(binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root) {
            val item=binding.item
            val icon=binding.icon
            val day = binding.day
            val desc = binding.desc
            val maxTemp = binding.maxTemp
            val minTemp = binding.minTemp
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            binding=WeatherItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            val holder = ViewHolder(binding)
            holder.itemView.setOnClickListener {
                val weather = weatherList[holder.adapterPosition]
                if (isTwoPane) {
                    // 获取被点击项的位置
                    val clickedPosition = holder.adapterPosition

                    // 更新选中项的位置
                    val previouslySelectedItem = selectedItem
                    selectedItem = clickedPosition

                    // 通知适配器刷新列表
                    notifyItemChanged(previouslySelectedItem)
                    notifyItemChanged(selectedItem)

                    // 如果是双页模式，则刷新WeatherContentFragment中的内容
                    val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.weatherContentFrag)  as WeatherContentFragment
                    fragment.refresh(weather)
                } else {
                    // 如果是单页模式，则直接启动WeatherContentActivity
                    WeatherContentActivity.actionStart(parent.context, weather)
                }
            }
            return holder
        }
        @SuppressLint("DiscouragedApi")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if(selectedItem==position){
                holder.item.background = resources.getDrawable(R.color.lightblue)
            }else{
                holder.item.background = resources.getDrawable(R.color.white)
            }
            val weather = weatherList[position]
            holder.day.text = weather.dayOfWeek
            holder.maxTemp.text=weather.max_temp
            holder.minTemp.text=weather.min_temp
            val time=System.currentTimeMillis()
            val mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(time);
            val hour = mCalendar.get(Calendar.HOUR);
            val apm = mCalendar.get(Calendar.AM_PM);
            var imageCode=""
            if(hour>=6 && apm==1){
                holder.desc.text=weather.textNight
                imageCode="black_"+weather.iconNight
            }else{
                holder.desc.text=weather.textDay
                imageCode="black_"+weather.iconDay
            }

            val resId:Int=resources.getIdentifier(imageCode, "raw", requireActivity().packageName)
            val svgInputStream = resources.openRawResource(resId)
            val svg = SVG.getFromInputStream(svgInputStream)
            val pictureDrawable = PictureDrawable(svg.renderToPicture())
            holder.icon.setImageDrawable(pictureDrawable)
        }
        override fun getItemCount() = weatherList.size
    }
}