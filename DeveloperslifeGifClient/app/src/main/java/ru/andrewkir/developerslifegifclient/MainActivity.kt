package ru.andrewkir.developerslifegifclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import ru.andrewkir.developerslifegifclient.ui.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ViewPagerAdapter(this)
        fragmentsViewPager.adapter = adapter
        TabLayoutMediator(tabLayout, fragmentsViewPager) { tab, position ->
            val titlesArray = resources.getStringArray(R.array.tab_titles)
            tab.text = titlesArray[position % titlesArray.size]
        }.attach()
    }
}