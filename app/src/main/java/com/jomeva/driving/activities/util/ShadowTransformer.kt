package com.jomeva.driving.activities.util

import android.view.View
import com.jomeva.driving.activities.adapters.CardAdapter

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener


class ShadowTransformer(private val viewPager: ViewPager, adapter: CardAdapter) :
    OnPageChangeListener, ViewPager.PageTransformer {
    private val cardAdapter: CardAdapter
    private var lastOffset = 0f
    private var scalingEnabled = false
    fun enableScaling(enable: Boolean) {
        if (scalingEnabled && !enable) {
            // shrink main card
            val currentCard = cardAdapter.getCardViewAt(viewPager.currentItem)
            if (currentCard != null) {
                currentCard.animate().scaleY(1f)
                currentCard.animate().scaleX(1f)
            }
        } else if (!scalingEnabled && enable) {
            // grow main card
            val currentCard = cardAdapter.getCardViewAt(viewPager.currentItem)
            if (currentCard != null) {
                //enlarge the current item
                currentCard.animate().scaleY(1.1f)
                currentCard.animate().scaleX(1.1f)
            }
        }
        scalingEnabled = enable
    }

    override fun transformPage(page: View, position: Float) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val realCurrentPosition: Int
        val nextPosition: Int
        val baseElevation = cardAdapter.getBaseElevation()
        val realOffset: Float
        val goingLeft = lastOffset > positionOffset

        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        if (goingLeft) {
            realCurrentPosition = position + 1
            nextPosition = position
            realOffset = 1 - positionOffset
        } else {
            nextPosition = position + 1
            realCurrentPosition = position
            realOffset = positionOffset
        }

        // Avoid crash on overscroll
        if (nextPosition > cardAdapter.getCount() - 1
            || realCurrentPosition > cardAdapter.getCount() - 1
        ) {
            return
        }
        val currentCard = cardAdapter.getCardViewAt(realCurrentPosition)

        // This might be null if a fragment is being used
        // and the views weren't created yet
        if (currentCard != null) {
            if (scalingEnabled) {
                currentCard.scaleX = (1 + 0.1 * (1 - realOffset)).toFloat()
                currentCard.scaleY = (1 + 0.1 * (1 - realOffset)).toFloat()
            }
            currentCard.cardElevation = baseElevation + (baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - realOffset))
        }
        val nextCard = cardAdapter.getCardViewAt(nextPosition)

        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextCard != null) {
            if (scalingEnabled) {
                nextCard.scaleX = (1 + 0.1 * realOffset).toFloat()
                nextCard.scaleY = (1 + 0.1 * realOffset).toFloat()
            }
            nextCard.cardElevation = baseElevation + (baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * realOffset)
        }
        lastOffset = positionOffset
    }

    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}

    init {
        viewPager.addOnPageChangeListener(this)
        cardAdapter = adapter
    }
}