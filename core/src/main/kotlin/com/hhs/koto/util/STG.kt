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

package com.hhs.koto.util

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.KotoGame
import com.hhs.koto.stg.bullet.*
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.wait

lateinit var game: KotoGame

lateinit var defaultShotSheet: ShotSheet

fun outOfFrame(x: Float, y: Float, rx: Float, ry: Float): Boolean {
    if (x + rx < -worldOriginX) return true
    if (x - rx > worldW - worldOriginX) return true
    if (y + ry < -worldOriginY) return true
    if (y - ry > worldH - worldOriginY) return true
    return false
}

fun <T> difficultySelect(easy: T, normal: T, hard: T, lunatic: T): T {
    return when (SystemFlag.difficulty) {
        GameDifficulty.EASY -> easy
        GameDifficulty.NORMAL -> normal
        GameDifficulty.HARD -> hard
        GameDifficulty.LUNATIC -> lunatic
        else -> throw KotoRuntimeException("Difficulty select: current difficulty is not regular")
    }
}

fun <T> difficultySelect(easy: T, normal: T, hard: T, lunatic: T, extra: T): T {
    return when (SystemFlag.difficulty) {
        GameDifficulty.EASY -> easy
        GameDifficulty.NORMAL -> normal
        GameDifficulty.HARD -> hard
        GameDifficulty.LUNATIC -> lunatic
        GameDifficulty.EXTRA -> extra
        else -> throw KotoRuntimeException("Difficulty select: current difficulty is not regular or extra")
    }
}

val playerX: Float
    get() = game.player.x
val playerY: Float
    get() = game.player.y

fun staticLaser(
    data: BulletData,
    x: Float,
    y: Float,
    length: Float,
    width: Float,
    angle: Float,
    speed: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 60,
    headHit: Float = 0.6f,
    widthHit: Float = 0.7f,
    style: Int = 0,
): StaticLaser {
    val b = StaticLaser(x, y, length, width, delay, data, headHit, widthHit, style)
    b.tint = color
    b.angle = angle
    b.speed = speed
    game.addBullet(b)
    return b
}

fun create(
    data: BulletData,
    x: Float,
    y: Float,
    speed: Float = 0f,
    angle: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
    setRotation: Boolean = true,
): BasicBullet {
    val result = game.addBullet(BasicBullet(x, y, speed, angle, data, tint = color, delay = delay))
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
    setRotation: Boolean = true,
): BasicBullet = create(defaultShotSheet[name], x, y, speed, angle, color, delay, setRotation)

/**
 * Create an All-in-one(AIO) laser
 */
suspend fun laser(
    env: CoroutineTask,
    width: Float,
    length: Float,
    hitPercent: Float = 0.8f,
    verticalMargin: Float = 10f,
    maxSample: Int = 512,
    sampleDelay: Int = 1,
    color: Color = RED_HSV,
    /**
     * After how many frames will this node start to be considered stable
     *
     * A stable node can be removed if it is moribund
     */
    protectionFrame: Int = 20,
    /**
     * The task to create the laser. The given parameter is laser index. (Should not be used anyway)
     */
    creationTask: (Int) -> BasicBullet
) {
    env.attachTask(CoroutineTask {
        var last: BasicBullet? = null
        var first: BasicBullet? = null
        var totalDistance = 0f
        for (it in 0 until maxSample) {
            val bul = creationTask(it).apply {
                this.laser = true
                this.maxLength = length
                this.width = width
                this.hitRatio = hitPercent
                this.prev = last
                this.tint = color
                this.verticalMargin = verticalMargin
                this.protectionFrame = protectionFrame
                last?.next = this
            }
            if (last == null) {
                first = bul
            }

            game.addBullet(bul)
            wait(sampleDelay)

//            println(first!!.getLaserLength())
            if (first!!.getLaserLength() >= length) {
                break
            }
            last = bul
        }
    })
}

fun <T : Bullet> T.setSpeed(speed: Float): T {
    this.speed = speed
    return this
}

fun <T : Bullet> T.setAngle(angle: Float, setRotation: Boolean = true): T {
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
    setRotation: Boolean = true,
): BasicBullet {
    val angle = atan2(x, y, targetX, targetY)
    val result = game.addBullet(BasicBullet(x, y, speed, angle, data, tint = color, delay = delay))
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
    setRotation: Boolean = true,
): BasicBullet = towards(defaultShotSheet[name], x, y, targetX, targetY, speed, color, delay, setRotation)

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
    setRotation: Boolean = true,
): BulletGroup {
    val ret = BulletGroup()
    for (i in 0 until count) {
        val angle = i * offsetAngle + startAngle
        val bullet = BasicBullet(
            x + cos(angle) * radius, y + sin(angle) * radius,
            speed, angle, data, tint = color, delay = delay,
        )
        if (setRotation) {
            bullet.rotation = angle
        }
        game.addBullet(bullet)
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
    setRotation: Boolean = true,
): BulletGroup =
    ring(defaultShotSheet[name], x, y, radius, count, offsetAngle, startAngle, speed, color, delay, setRotation)

fun ring(
    data: BulletData,
    x: Float,
    y: Float,
    radius: Float,
    progression: IntProgression,
    speed: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 8,
    setRotation: Boolean = true,
): BulletGroup {
    val ret = BulletGroup()
    progression.forEach {
        val angle = it.toFloat()
        val bullet = BasicBullet(
            x + cos(angle) * radius, y + sin(angle) * radius,
            speed, angle, data, tint = color, delay = delay,
        )
        if (setRotation) {
            bullet.rotation = angle
        }
        game.addBullet(bullet)
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
    setRotation: Boolean = true,
): BulletGroup = ring(defaultShotSheet[name], x, y, radius, progression, speed, color, delay, setRotation)

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