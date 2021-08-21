package com.hhs.koto.app.ui

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.badlogic.gdx.scenes.scene2d.ui.Label

class FontScaleAction : TemporalAction() {

    var startX = 0f
    var startY = 0f
    var endX = 0f
    var endY = 0f

    override fun begin() {
        val t = target as Label
        startX = t.fontScaleX
        startY = t.fontScaleY
    }

    override fun update(percent: Float) {
        val t = target as Label
        when (percent) {
            0f -> {
                t.setFontScale(startX, startY)
            }
            1f -> {
                t.setFontScale(endX, endY)
            }
            else -> {
                t.setFontScale(startX + (endX - startX) * percent, startY + (endY - startY) * percent)
            }
        }
    }
}

fun fontScaleTo(x: Float, y: Float, duration: Float): FontScaleAction {
    val act = Actions.action(FontScaleAction::class.java)
    act.duration = duration
    act.endX = x
    act.endY = y
    return act
}
