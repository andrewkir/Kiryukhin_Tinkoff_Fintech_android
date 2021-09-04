package ru.andrewkir.developerslifegifclient.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.andrewkir.developerslifegifclient.R

class ViewPagerAdapter(private val fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int =
        fragmentActivity.resources.getStringArray(R.array.tab_titles).size

    override fun createFragment(position: Int): Fragment = TabFragment.newInstance(position)
}