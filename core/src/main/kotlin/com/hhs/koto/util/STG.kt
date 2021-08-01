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
import com.hhs.koto.stg.bullet.Bullet
import com.hhs.koto.stg.bullet.BulletData
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

fun create(bulletData: BulletData, x: Float, y: Float, angle: Float, speed: Float, color: Color = Color.WHITE): Bullet =
    game.bullets.addBullet(Bullet(x, y, speed, angle, bulletData, color = color))

//    TODO B
//    fun setAngleSpeed(bullet: Bullet, x: Float, y: Float, angle: Float, speed: Float): Bullet {
//        var angle = angle
//        angle = M.normalizeAngle(angle)
//        bullet.sprite.setRotation(angle - bullet.data.rotation)
//        bullet.setXY(x, y)
//        bullet.setSpeed(speed)
//        bullet.setAngle(angle)
//        J.add(bullet)
//        return bullet
//    }
//
//    fun create(x: Float, y: Float, angle: Float, speed: Float, id: Int, tag: Int): Bullet {
//        return setAngleSpeed(EnemyBullet(B[id], tag), x, y, angle, speed)
//    }
//
//    fun towards(x: Float, y: Float, angle: Float, speed: Float, name: String?, tag: Int): Bullet {
//        return create(x, y, angle, speed, defaultSheet!!.getId(name!!), tag)
//    }
//
//    fun towards(x: Float, y: Float, targetX: Float, targetY: Float, speed: Float, id: Int, tag: Int): Bullet {
//        return create(x, y, atan2(x, y, targetX, targetY), speed, id, tag)
//    }
//
//    fun towards(x: Float, y: Float, targetX: Float, targetY: Float, speed: Float, name: String?, tag: Int): Bullet {
//        return towards(x, y, targetX, targetY, speed, defaultSheet!!.getId(name!!), tag)
//    }
//
//    fun towards(x: Float, y: Float, speed: Float, id: Int, tag: Int): Bullet {
//        return towards(x, y, J.playerX(), J.playerY(), speed, id, tag)
//    }
//
//    fun towards(x: Float, y: Float, speed: Float, name: String?, tag: Int): Bullet {
//        return towards(x, y, speed, defaultSheet!!.getId(name!!), tag)
//    }
