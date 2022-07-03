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
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.*

class BossNameDisplay(
    override val zIndex: Int = 300,
) : Drawable {
    override var alive: Boolean = true
    override var x: Float = -worldOriginX + 6
    override var y: Float = worldH - worldOriginY - 30
    var textColor: Color = CYAN_HSV
    var bossName: String = ""
    var spellCount: Int = 0
    var textAlpha = 0f
    var visible: Boolean = false
    val font = getFont(
        20, bundle["font.bossNameDisplay"], borderWidth = 1f, borderColor = Color.BLACK
    )
    val star = Sprite(getRegion("ui/star.png")).apply {
        color = GREEN_HSV
        setSize(12f, 12f)
    }

    fun show(boss: BasicBoss, spellCount: Int) {
        show(bundle["game.boss.${boss.name}.name"], spellCount, boss.nameColor)
    }

    fun show(bossName: String, spellCount: Int, textColor: Color = BLUE_HSV) {
        this.bossName = bossName
        this.spellCount = spellCount
        this.textColor = textColor
        visible = true
    }

    fun hide() {
        this.bossName = ""
        this.spellCount = 0
        visible = false
    }

    fun nextSpell() {
        spellCount = (spellCount - 1).coerceAtLeast(0)
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (textAlpha >= 0.001f) {
            font.draw(batch, textAlpha * parentAlpha, bossName, 10f / 20, x, y + 24, textColor)
        }
        if (star.alpha >= 0.0001f) {
            repeat(spellCount) {
                star.setPosition(x + it * 12, y)
                star.draw(batch, parentAlpha)
            }
        }
    }

    override fun tick() {
        if (visible) {
            if (textAlpha < 1f) textAlpha = (textAlpha + 0.05f).coerceAtMost(1f)
            if (star.alpha < 1f) star.alpha = (star.alpha + 0.03f).coerceAtMost(1f)
        } else {
            if (textAlpha > 0f) textAlpha = (textAlpha - 0.05f).coerceAtLeast(0f)
            if (star.alpha > 0f) star.alpha = (star.alpha - 0.03f).coerceAtLeast(0f)
        }
    }
}