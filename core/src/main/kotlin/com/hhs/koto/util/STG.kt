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
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.utils.GdxRuntimeException
import com.hhs.koto.app.Config
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.KotoGame
import com.hhs.koto.stg.addBullet
import com.hhs.koto.stg.bullet.*
import com.hhs.koto.stg.drawable.Boss

lateinit var game: KotoGame

lateinit var B: ShotSheet

fun outOfFrame(x: Float, y: Float, rx: Float, ry: Float): Boolean {
    if (x + rx < -Config.worldOriginX) return true
    if (x - rx > Config.worldW - Config.worldOriginX) return true
    if (y + ry < -Config.worldOriginY) return true
    if (y - ry > Config.worldH - Config.worldOriginY) return true
    return false
}

fun <T> difficultySelect(easy: T, normal: T, hard: T, lunatic: T): T {
    return when (SystemFlag.difficulty) {
        GameDifficulty.EASY -> easy
        GameDifficulty.NORMAL -> normal
        GameDifficulty.HARD -> hard
        GameDifficulty.LUNATIC -> lunatic
        else -> throw GdxRuntimeException("Difficulty select: current difficulty is not regular")
    }
}

val playerX: Float
    get() = game.player.x
val playerY: Float
    get() = game.player.y

@Suppress("UNCHECKED_CAST")
fun <T : Boss> findBoss(clazz: Class<T>): T? {
    game.enemies.forEach {
        if (it.javaClass == clazz) {
            return it as T
        }
    }
    return null
}

fun create(
    data: BulletData,
    x: Float,
    y: Float,
    speed: Float = 0f,
    angle: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
    setRotation: Boolean = false,
): BasicBullet {
    val result = addBullet(BasicBullet(x, y, speed, angle, data, tint = color, delay = delay))
    if (setRotation) result.rotation = angle
    return result
}

fun create(
    name: String,
    x: Float,
    y: Float,
    speed: Float = 0f,
    angle: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
    setRotation: Boolean = false,
): BasicBullet = create(B[name], x, y, speed, angle, color, delay, setRotation)

fun <T : Bullet> T.setSpeed(speed: Float): T {
    this.speed = speed
    return this
}

fun <T : Bullet> T.setAngle(angle: Float, setRotation: Boolean = false): T {
    this.angle = angle
    if (setRotation) rotation = angle
    return this
}

fun towards(
    data: BulletData,
    x: Float,
    y: Float,
    targetX: Float,
    targetY: Float,
    speed: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
    setRotation: Boolean = false,
): BasicBullet {
    val angle = atan2(x, y, targetX, targetY)
    val result = addBullet(BasicBullet(x, y, speed, angle, data, tint = color, delay = delay))
    if (setRotation) result.rotation = angle
    return result
}

fun towards(
    name: String,
    x: Float,
    y: Float,
    targetX: Float,
    targetY: Float,
    speed: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
    setRotation: Boolean = false,
): BasicBullet = towards(B[name], x, y, targetX, targetY, speed, color, delay, setRotation)

fun <T : Bullet> T.towards(targetX: Float, targetY: Float): T {
    angle = atan2(x, y, targetX, targetY)
    return this
}

fun ring(
    data: BulletData,
    x: Float,
    y: Float,
    radius: Float,
    count: Int,
    offsetAngle: Float = 360f / count,
    startAngle: Float = 0f,
    speed: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
): BulletGroup {
    val ret = BulletGroup()
    for (i in 0 until count) {
        val angle = i * offsetAngle + startAngle
        val bullet = BasicBullet(
            x + cos(angle) * radius, y + sin(angle) * radius,
            speed, angle, data, tint = color, delay = delay,
        )
        addBullet(bullet)
        ret.addBullet(bullet)
    }
    return ret
}

fun ring(
    name: String,
    x: Float,
    y: Float,
    radius: Float,
    count: Int,
    offsetAngle: Float = 360f / count,
    startAngle: Float = 0f,
    speed: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
): BulletGroup = ring(B[name], x, y, radius, count, offsetAngle, startAngle, speed, color, delay)

fun ring(
    data: BulletData,
    x: Float,
    y: Float,
    radius: Float,
    progression: IntProgression,
    speed: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
): BulletGroup {
    val ret = BulletGroup()
    progression.forEach {
        val angle = it.toFloat()
        val bullet = BasicBullet(
            x + cos(angle) * radius, y + sin(angle) * radius,
            speed, angle, data, tint = color, delay = delay,
        )
        addBullet(bullet)
        ret.addBullet(bullet)
    }
    return ret
}

fun ring(
    name: String,
    x: Float,
    y: Float,
    radius: Float,
    progression: IntProgression,
    speed: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
): BulletGroup = ring(B[name], x, y, radius, progression, speed, color, delay)

inline fun ring(
    x: Float,
    y: Float,
    radius: Float,
    progression: IntProgression,
    action: (Float, Float) -> Unit,
) {
    progression.forEach {
        val angle = it.toFloat()
        action(x + cos(angle) * radius, y + sin(angle) * radius)
    }
}

inline fun ring(
    x: Float,
    y: Float,
    radius: Float,
    offsetAngle: Float,
    count: Int,
    startAngle: Float = 0f,
    action: (Float, Float) -> Unit,
) {
    for (i in 0 until count) {
        val angle = i * offsetAngle + startAngle
        action(x + cos(angle) * radius, y + sin(angle) * radius)
    }
}

inline fun ringCloud(
    x: Float,
    y: Float,
    count: Int,
    radius: Float = 30f,
    thickness: Float = 15f,
    action: (Float, Float) -> Unit,
) {
    repeat(count) {
        val r = random(radius - thickness / 2, radius + thickness / 2)
        val a = random(0f, 360f)
        val tmpX = cos(a) * r + x
        val tmpY = sin(a) * r + y
        action(tmpX, tmpY)
    }
}