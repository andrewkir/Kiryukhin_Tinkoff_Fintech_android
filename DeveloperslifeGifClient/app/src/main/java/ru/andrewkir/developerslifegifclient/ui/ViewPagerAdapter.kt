package ru.andrewkir.developerslifegifclient.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.andrewkir.developerslifegifclient.utils.SectionsEnum

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = SectionsEnum.values().size

    override fun createFragment(position: Int): Fragment = TabFragment.newInstance(position)
}