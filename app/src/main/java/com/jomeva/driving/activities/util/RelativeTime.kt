package com.jomeva.driving.activities.util

import android.app.Application
import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


class RelativeTime {

    object RelativeTime : Application() {
        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS

        public fun getTimeAgo(time: Long, ctx: Context?): String? {
            var time = time
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            val now = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return "Hace un momento"
            }

            // TODO: localize
            val diff = now - time
            return if (diff < MINUTE_MILLIS) {
                "Hace un momento"
            } else if (diff < 2 * MINUTE_MILLIS) {
                "Hace un minuto"
            } else if (diff < 50 * MINUTE_MILLIS) {
                "Hace " + diff / MINUTE_MILLIS + " minutos"
            } else if (diff < 90 * MINUTE_MILLIS) {
                "Hace una hora"
            } else if (diff < 24 * HOUR_MILLIS) {
                "Hace " + diff / HOUR_MILLIS + " horas"
            } else if (diff < 48 * HOUR_MILLIS) {
                "Ayer"
            } else if (diff >= 48 * HOUR_MILLIS && diff < 1488 * HOUR_MILLIS) {
                "Hace " + diff / DAY_MILLIS + " dias"
            } else {
                getDateTime(time)

            }
        }

        //86400000 un dia en milli
        //1560507488
        //1622898627385

        fun timeFormatAMPM(timestamp: Long): String {
            val formatter = SimpleDateFormat("hh:mm a")
            return formatter.format(Date(timestamp))
        }


        private fun getDateTime(s: Long): String? {
            if (s == null) return ""
            //Get instance of calendar
            val calendar = Calendar.getInstance(Locale.getDefault())
            //get current date from ts
            calendar.timeInMillis = s
            //return formatted date
            return android.text.format.DateFormat.format("E, dd MMM yyyy", calendar).toString()
        }

        fun timeAudio(x: Long): String {
            var time = ""
            if (x < 10) {
                time = "00:0$x"
            } else if (x > 10 && x < 60) {
                time = "00:$x"
            } else if (x > 59) {
                time = getEntero(x)

            }
            return time
        }
        fun timeAUsoApp(x: Long): String {
            var time = ""
            if (x>=3600){
                var hor = (x / 3600).toString()
                var min = ((x % 3600)/60).toString()

                hor = if (hor.toLong() < 10){
                    "0$hor"+"h"
                }else{
                    "$hor"+"h"
                }

                min = if (min.toLong() < 10){
                    "0$min"+"m"
                }else{
                    "$min"+"m"
                }
                time= "$hor:$min"
            }else{
                var hor ="0"
                var min = (x /60).toString()

                hor = if (hor.toLong() < 10){
                    "0$hor"+"h"
                }else{
                    "$hor"+"h"
                }

                min = if (min.toLong() < 10){
                    "0$min"+"m"
                }else{
                    "$min"+"m"
                }
                time= "$hor:$min"
            }
/*
        */




            //3600
           /* if (x < 10) {
                time = "00:0$x"
            } else if (x > 10 && x < 60) {
                time = "00:$x"
            } else if (x > 59) {
                time = getEntero(x)
                var hor = (x / 3600).toString()
                var min = (x / 60).toString()
                var seg = (x % 60).toString()
                if (hor.toLong() < 10) hor = "0$hor"
                if (min.toLong() < 10) min = "0$min"
                if (seg.toLong() < 10) seg = "0$seg"



            }*/
            return time
        }
        fun getEntero(x: Long): String {
            var min = (x / 60).toString()
            var seg = (x % 60).toString()
           if (min.toLong() < 10) min = "0$min"
            if (seg.toLong() < 10) seg = "0$seg"
            return "$min:$seg"
        }
fun getSecondsCurrent(time: Long, ctx: Context?): String {
    val formatter =  SimpleDateFormat("s");
    var time = time
    if (time < 1000000000000L) {
        // if timestamp given in seconds, convert to millis
        time *= 1000
    }
    val now = System.currentTimeMillis()
    val diff = now - time
    if (diff < 24 * HOUR_MILLIS) return formatter.format(Date(time))
    return ""
}
        fun getFirst(time: Long, ctx: Context?): String {

            var time = time
            val formatter = SimpleDateFormat("hh:mm a")
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            val now = System.currentTimeMillis()
            val diff = now - time
            if (diff < 24 * HOUR_MILLIS) return formatter.format(Date(time))
            return ""
        }

        fun timeFormatAMPM(time: Long, ctx: Context?): String? {
            var time = time
            val formatter = SimpleDateFormat("hh:mm a")
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            val now = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return formatter.format(Date(time))
            }

            // TODO: localize
            val diff = now - time
            return if (diff < 24 * HOUR_MILLIS) {
                formatter.format(Date(time))
            } else if (diff < 48 * HOUR_MILLIS) {
                "Ayer"
            } else {
                "Hace " + diff / DAY_MILLIS + " dias"
            }
        }

        fun timeFormat24H(time: Long, ctx: Context?): String? {
            var time = time
            val formatter = SimpleDateFormat("hh:mm a")
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            var horaSistema = formatter.format(Date(time))
            var formato = horaSistema.substring(6, 11)
            var hora = horaSistema.substring(0, 2).toInt()
            var min = horaSistema.substring(3, 5)


            if (formato == "a. m." && hora > 0 && hora < 12) {

            } else if (formato == "a. m." && hora == 12) {
                hora = "00".toInt()
            } else if (formato == "p. m." && hora > 12) {

            } else if (formato == "p. m." && hora < 12) {
                hora += 12

            }
            var nuevahora = ""
            if (hora < 12) {
                if (hora<10){
                    nuevahora = "0$hora"
                }else{
                    nuevahora = "$hora"
                }

            }

            nuevahora = "$nuevahora:$min $formato"

            Log.d("ahoramismo", "$horaSistema")
            Log.d("ahoramismo", "$nuevahora")

            return nuevahora

        }
    }
}
