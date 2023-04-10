package com.jomeva.driving.activities.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jomeva.driving.activities.activities.*

class MyPagerAdapterModerador (fm: FragmentManager): FragmentPagerAdapter(fm)   {
    var context: Context? = null
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when(position){

            0 -> {
                FragmentModerador2()
            }
            1-> {
                FragmentModerador3()
            }

            else->{
                FragmentModerador4()

            }
        }
    }



    override fun getPageTitle(position: Int): CharSequence? {

        return   when(position){


            0 -> {
                "Útil"
            }
            1-> {
                "petición"
            }

            else->{
                "equipo"
            }
        }
/*when(position){
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
        }*/
    }

}