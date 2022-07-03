/*
 * MIT License
 *
 * Copyright (c) 2021-2022 Hell Hole Studios
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

package com.hhs.koto.stg.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
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
    override var zIndex: Int = 200,
) : Drawable {
    override var alive: Boolean = true
    override var x: Float = targetX
    override var y: Float = targetY - worldH + 60
    var t: Int = 0
    var finishCounter: Int = 0
    var failed: Boolean = false
    var finished: Boolean = false

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
        24, bundle["font.spellNameDisplay"], borderWidth = 2f, borderColor = Color.BLACK
    )
    val infoFont = getFont(
        20, bundle["font.spellInfoDisplay"], borderWidth = 1f, borderColor = Color.BLACK
    )
    val spellName = bundle["game.spell.$name.name"]
    val spellHistory = "%d / %d".format(
        gameData.currentElement.spell[name].successfulAttempt,
        gameData.currentElement.spell[name].totalAttempt,
    )

    var backgroundOffset = -384f
    var nameOffset = 200f
    var deltaScale = 1.5f
    var fontScale = 24f
    var alpha = 0f
    var nameAlpha = 0f

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (alpha < 0.001f) return

        val distanceAlpha = lerp(
            1f,
            lerp(
                0.1f, 1f,
                Rectangle(x - 200f, y - 10f, 300f, 60f).distanceTo(playerX, playerY) / 30f,
            ),
            (t - 90f) / 60f,
        )

        background.setPosition(x + backgroundOffset, y)
        background.setScale(deltaScale)
        background.color = backgroundColor
        background.draw(batch, parentAlpha * alpha * distanceAlpha)
        historyText.setPosition(x - 85f, y - 6f)
        historyText.draw(batch, parentAlpha * alpha * distanceAlpha)
        bonusText.setPosition(x - 180f, y - 6f)
        bonusText.draw(batch, parentAlpha * alpha * distanceAlpha)

        nameFont.draw(
            batch,
            parentAlpha * nameAlpha * distanceAlpha,
            spellName,
            fontScale / 24,
            x + nameOffset,
            y + 20f,
            halign = Align.right,
        )
        infoFont.draw(
            batch,
            parentAlpha * alpha * distanceAlpha,
            spellHistory,
            10f / 20,
            x - 45f,
            y + 4f,
        )
        infoFont.draw(
            batch,
            parentAlpha * alpha * distanceAlpha,
            if (failed) "Failed" else String.format("%d", bonus),
            10f / 20,
            x - 140f,
            y + 4f,
        )
    }

    override fun tick() {
        if (t <= 30) {
            nameOffset = smoothstep(-120f, -10f, t / 30f)
            fontScale = smoothstep(36f, 12f, t / 30f)
            nameAlpha = lerp(0f, 1f, t / 30f)
        }
        if (t in 45..75) {
            backgroundOffset = smoothstep(-384f, -256f, (t - 45f) / 30f)
            deltaScale = smoothstep(1.5f, 1f, (t - 45f) / 30f)
            alpha = lerp(0f, 1f, (t - 45f) / 30f)
        }
        if (t in 90..150) {
            y = smoothstep(targetY - worldH + 60, targetY, (t - 90f) / 60f)
        }
        t++
        if (finished) {
            x += 10f
            finishCounter++
            if (finishCounter >= 30) kill()
        }
    }
}