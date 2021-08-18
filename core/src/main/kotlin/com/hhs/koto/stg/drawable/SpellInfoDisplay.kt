/*
 * MIT License
 *
 * Copyright (c) 2021 Hell Hole Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.hhs.koto.stg.drawable

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.*

class SpellInfoDisplay(
    name: String,
    var bonus: Long,
    val targetX: Float = worldW - worldOriginX,
    val targetY: Float = worldH - worldOriginY - 25,
    var backgroundColor: Color = RED_HSV,
) : Drawable {
    override var alive: Boolean = true
    override var x: Float = targetX
    override var y: Float = targetY - worldH + 60
    var t: Int = 0
    var finishCounter: Int = 0
    var failed: Boolean = false
    var finished: Boolean = false
    var alpha: Float = 0f

    val background = Sprite(getRegion("ui/spell_info_bg.png")).apply {
        setSize(256f, 36f)
    }
    val bonusText = Sprite(getRegion("ui/bonus_text.png")).apply {
        setSize(36f, 12f)
    }
    val historyText = Sprite(getRegion("ui/history_text.png")).apply {
        setSize(36f, 12f)
    }
    val nameFont = getFont(
        bundle["font.spellNameDisplay"], 24, Color.RED, borderWidth = 2f, borderColor = Color.BLACK
    )
    val infoFont = getFont(
        bundle["font.spellInfoDisplay"], 20, Color.RED, borderWidth = 1f, borderColor = Color.BLACK
    )
    val spellName = bundle["game.spell.$name.name"]
    val spellHistory = "%d / %d".format(
        gameData.currentElement.spell[name].successfulAttempt,
        gameData.currentElement.spell[name].totalAttempt,
    )

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        background.setPosition(x - 256f, y)
        background.color = backgroundColor
        background.draw(batch, parentAlpha * alpha)
        historyText.setPosition(x - 80f, y - 6f)
        historyText.draw(batch, parentAlpha * alpha)
        bonusText.setPosition(x - 180f, y - 6f)
        bonusText.draw(batch, parentAlpha * alpha)

        nameFont.draw(
            batch,
            parentAlpha * alpha,
            spellName,
            12f / 24,
            x - 10f,
            y + 20f,
            halign = Align.right,
        )
        infoFont.draw(
            batch,
            parentAlpha * alpha,
            spellHistory,
            10f / 20,
            x - 40f,
            y + 4f,
        )
        infoFont.draw(
            batch,
            parentAlpha * alpha,
            if (failed) "Failed" else bonus.toString(),
            10f / 20,
            x - 140f,
            y + 4f,
        )
    }

    override fun tick() {
        if (t <= 30) {
            alpha = lerp(0f, 1f, t / 30f)
        } else if (t <= 90) {
            y = smoothstep(targetY - worldH + 60, targetY, (t - 30f) / 60f)
        }
        t++
        if (finished) {
            x += 10f
            finishCounter++
            if (finishCounter >= 30) kill()
        }
    }
}