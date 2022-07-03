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

package com.hhs.koto.stg.item

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.util.A
import com.hhs.koto.util.game

class FullPowerItem(
    x: Float,
    y: Float,
    speed: Float = 2f,
    angle: Float = 90f,
    radius: Float = 10f,
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    color: Color = Color.WHITE,
) : BasicItem(
    x,
    y,
    A["item/item.atlas"],
    "power_full",
    32f,
    32f,
    speed,
    angle,
    radius,
    scaleX = scaleX,
    scaleY = scaleY,
    color = color,
    rotateOnCreation = true,
) {
    override fun onCollected(collectPositionX: Float, collectPositionY: Float, autoCollected: Boolean) {
        super.onCollected(collectPositionX, collectPositionY, autoCollected)
        game.power = game.maxPower
    }
}