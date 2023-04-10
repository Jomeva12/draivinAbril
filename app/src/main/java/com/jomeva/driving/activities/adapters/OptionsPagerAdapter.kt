package com.jomeva.driving.activities.adapters

import android.content.Context
import android.view.ViewGroup

import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.fragment.app.FragmentStatePagerAdapter
import com.jomeva.driving.activities.activities.ImagePagerFragment


class OptionsPagerAdapter(
    context: Context?,
    fm: FragmentManager?,
    baseElevation: Float,
    data: ArrayList<String>,
    intencion:String
) :
    FragmentStatePagerAdapter(fm!!), CardAdapter {
    private val fragments: MutableList<ImagePagerFragment>
    private val data: ArrayList<String>
    private val baseElevation: Float
private val intencion:String

    init {
        fragments = ArrayList()
        this.baseElevation = baseElevation
        this.data = data
        this.intencion=intencion
        // AÑADIR VIEWS
        for (i in 0 until data.size) {
            addCardFragment(ImagePagerFragment())
        }
    }
    override fun getBaseElevation(): Float {
        return baseElevation
    }

    override fun getCardViewAt(position: Int): CardView? {
        return fragments[position].getCard()
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return ImagePagerFragment.newInstance(position, data[position],data.size,intencion)
    }

    override fun getItemPosition(`object`: Any): Int {
        val index = fragments.indexOf(`object`)
        return if (index == -1) POSITION_NONE else index
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        fragments[position] = fragment as ImagePagerFragment
        return fragment
    }

    fun addCardFragment(fragment: ImagePagerFragment) {
        fragments.add(fragment)
    }


}
