package ru.andrewkir.developerslifegifclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import ru.andrewkir.developerslifegifclient.databinding.ActivityMainBinding
import ru.andrewkir.developerslifegifclient.ui.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ViewPagerAdapter(this)
        binding.fragmentsViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.fragmentsViewPager) { tab, position ->
            val titlesArray = resources.getStringArray(R.array.tab_titles)
            tab.text = titlesArray[position % titlesArray.size]
        }.attach()
    }
}