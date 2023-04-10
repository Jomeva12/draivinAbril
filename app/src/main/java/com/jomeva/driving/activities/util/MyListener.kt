package com.jomeva.driving.activities.util

import android.view.View
import com.jomeva.driving.activities.modelos.Post
import com.jomeva.driving.activities.modelos.Propinas

interface MyListener {
    /*para usar listener
       * 1 creo la interface con los metodos a los que quiero acceder
       * 2 en la clase que va a mandar instancio la clase que recibe
       * 3 en la clase que recibe implemento la interface con metodos
       * 4 en la clase que manda, el objeto de la otra clase ahora pouede acceder al metodo implementado de la interface
       * en pocas palabras quien recibe implementa los metodos y la que manda instancia a la clase que se lo va a enviar*/



    fun cambiandoCiudadListener(view: View, post: Post,isImagen:Boolean)
fun consultarPropinas(view: View,propinas: Propinas)
fun CrearPrimerReporte(view: View)


}