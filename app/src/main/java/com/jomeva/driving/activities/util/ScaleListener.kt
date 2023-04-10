package com.jomeva.driving.activities.util

import android.view.ScaleGestureDetector
import android.widget.ImageView

class ScaleListener(): ScaleGestureDetector.SimpleOnScaleGestureListener() {
  private lateinit var imageView: ImageView
  private  var scale:Float=1.0f

    constructor(imageView: ImageView) : this() {
        this.imageView = imageView

    }


    override fun onScale(detector: ScaleGestureDetector): Boolean {
      scale*=detector!!.scaleFactor
      scale=Math.max(0.1f,Math.min(scale,5f))
        imageView.scaleX=scale
        imageView.scaleY=scale
        return true
    }




}