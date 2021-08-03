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

package com.hhs.koto.util

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.app.Config
import com.hhs.koto.stg.KotoGame
import com.hhs.koto.stg.addBullet
import com.hhs.koto.stg.bullet.Bullet
import com.hhs.koto.stg.bullet.BulletData
import com.hhs.koto.stg.bullet.BulletGroup
import com.hhs.koto.stg.bullet.ShotSheet

lateinit var game: KotoGame

object B {
    lateinit var defaultSheet: ShotSheet

    operator fun get(id: Int): BulletData {
        return defaultSheet.findBullet(id)
    }

    operator fun get(name: String): BulletData {
        return defaultSheet.findBullet(name)
    }
}

fun outOfWorld(x: Float, y: Float, rx: Float, ry: Float): Boolean {
    if (x + rx < -Config.originX - Config.deleteDistance) return true
    if (x - rx > Config.w + Config.deleteDistance - Config.originX) return true
    if (y + ry < -Config.originY - Config.deleteDistance) return true
    if (y - ry > Config.h + Config.deleteDistance - Config.originY) return true
    return false
}

fun outOfFrame(x: Float, y: Float, rx: Float, ry: Float): Boolean {
    if (x + rx < -Config.originX) return true
    if (x - rx > Config.w - Config.originX) return true
    if (y + ry < -Config.originY) return true
    if (y - ry > Config.h - Config.originY) return true
    return false
}

val playerX: Float
    get() = game.player.getX()
val playerY: Float
    get() = game.player.getY()

fun create(
    data: BulletData,
    x: Float,
    y: Float,
    angle: Float = 0f,
    speed: Float = 0f,
    color: Color = Color.WHITE,
): Bullet =
    addBullet(Bullet(x, y, speed, angle, data, color = color))

fun towards(
    data: BulletData,
    x: Float,
    y: Float,
    targetX: Float,
    targetY: Float,
    speed: Float = 0f,
    color: Color = Color.WHITE,
): Bullet =
    addBullet(Bullet(x, y, speed, atan2(x, y, targetX, targetY), data, color = color))

fun ring(
    data: BulletData,
    x: Float,
    y: Float,
    radius: Float,
    offsetAngle: Float,
    count: Int,
    startAngle: Float = 0f,
    speed: Float = 0f,
    color: Color = Color.WHITE,
): BulletGroup {
    val ret = BulletGroup()
    for (i in 0 until count) {
        val angle = i * offsetAngle + startAngle
        val bullet = Bullet(x + cos(angle) * radius, y + sin(angle) * radius, speed, angle, data, color = color)
        addBullet(bullet)
        ret.addBullet(bullet)
    }
    return ret
}

fun ring(
    data: BulletData,
    x: Float,
    y: Float,
    radius: Float,
    progression: IntProgression,
    speed: Float = 0f,
    color: Color = Color.WHITE,
): BulletGroup {
    val ret = BulletGroup()
    progression.forEach {
        val angle = it.toFloat()
        val bullet = Bullet(x + cos(angle) * radius, y + sin(angle) * radius, speed, angle, data, color = color)
        addBullet(bullet)
        ret.addBullet(bullet)
    }
    return ret
}