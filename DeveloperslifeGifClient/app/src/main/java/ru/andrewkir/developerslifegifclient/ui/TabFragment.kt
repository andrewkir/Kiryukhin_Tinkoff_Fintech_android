package ru.andrewkir.developerslifegifclient.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_tab.*
import ru.andrewkir.developerslifegifclient.R

class TabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(PAGE_NUMBER) }?.apply {
            val titlesArray = resources.getStringArray(R.array.tab_titles)
            textView.text = titlesArray[getInt(PAGE_NUMBER, 0) % titlesArray.size]
        }
    }

    companion object {
        private val PAGE_NUMBER = "page_number"

        fun newInstance(pageNumber: Int): TabFragment {
            val args = Bundle()
            args.putInt(PAGE_NUMBER, pageNumber)
            val fragment = TabFragment()
            fragment.arguments = args
            Log.d("CREATED FRAGMENT", "CREATED")
            return fragment
        }
    }
}