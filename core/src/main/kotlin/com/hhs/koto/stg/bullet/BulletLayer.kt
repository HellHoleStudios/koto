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

package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.hhs.koto.app.Config
import com.hhs.koto.stg.IndexedActor
import com.hhs.koto.util.game
import com.hhs.koto.util.outOfWorld
import com.hhs.koto.util.removeNull
import ktx.collections.GdxArray

class BulletLayer(override val z: Int = 0) : Actor(), IndexedActor {
    val bullets = GdxArray<Bullet>()
    var count: Int = 0
        private set
    var blankCount: Int = 0
        private set
    var subFrameTime: Float = 0f

    fun tick() {
        subFrameTime = 0f
        for (i in 0 until bullets.size) {
            if (bullets[i] != null) {
                bullets[i].tick()
                if (outOfWorld(bullets[i].x, bullets[i].y, bullets[i].boundingWidth, bullets[i].boundingHeight)) {
                    bullets[i].alive = false
                    bullets[i] = null
                }
            }
        }
        if (count <= Config.cleanupBulletCount && blankCount >= Config.cleanupBlankCount || bullets.size >= 1048576) {
            game.logger.info("Cleaning up blanks in bullet array: bulletCount=$count blankCount=$blankCount")
            bullets.removeNull()
            blankCount = 0
        }
    }

    override fun act(delta: Float) {
        subFrameTime += delta
        super.act(delta)
    }

    fun addBullet(bullet: Bullet): Bullet {
        bullets.add(bullet)
        count++
        return bullet
    }

    fun removeBullet(bullet: Bullet) {
        val index = bullets.indexOf(bullet)
        if (index != -1) {
            bullets[index] = null
            blankCount++
            count--
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        for (i in 0 until bullets.size) {
            if (bullets[i] != null) {
                bullets[i].draw(batch, parentAlpha, subFrameTime)
            }
        }
    }
}