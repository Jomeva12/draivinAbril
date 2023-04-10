package com.jomeva.driving.activities.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jomeva.driving.activities.activities.*

class MypageAdapter(fm: FragmentManager): FragmentPagerAdapter(fm)   {
    var context: Context? = null
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
       return when(position){

           0 -> {

               FragmentTwo()
           }
           1 -> {
               FragmentOne()
           }
           2 -> {
               FragmentThree()
           }

           else->{
               FragmentFour()

           }
       }
    }


override fun getPageTitle(position: Int): CharSequence? {

        return   when(position){

           0 -> {
               "Mapa"

            }
            1 -> {
                "Eventos"
            }
            2 -> {
                "Chat"
            }
            else->{
                "Propinas"
            }
        }

    }

}