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

package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.hhs.koto.stg.Bounded
import com.hhs.koto.stg.CollisionShape
import com.hhs.koto.stg.LaserCollision
import com.hhs.koto.stg.particle.BulletDestroyParticle
import com.hhs.koto.stg.particle.GrazeParticle
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.*
import kotlinx.coroutines.CoroutineScope
import ktx.collections.GdxArray
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

open class BasicBullet(
    override var x: Float,
    override var y: Float,
    speed: Float = 0f,
    angle: Float = 0f,
    val data: BulletData,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    override var destroyable: Boolean = true,
    override var rotation: Float = 0f,
    override var tint: Color = NO_TINT_HSV,
    open val delay: Int = 8,
) : Bullet, Bounded {
    companion object {
        val tmpColor = Color()
    }

    override val blending
        get() = if (t >= delay) {
            data.blending
        } else {
            data.delayBlending
        }
    var attachedTasks: GdxArray<Task>? = null
    override val collision: CollisionShape
        get() {
            return if (isPartOfLaser()) {
                LaserCollision(this)
            } else {
                data.collision
            }
        }

    override var speed: Float = speed
        set(value) {
            field = value
            calculateDelta()
        }
    override var angle: Float = angle
        set(value) {
            field = value
            calculateDelta()
        }

    val sprite = Sprite()
    var deltaX: Float = 0f
    var deltaY: Float = 0f
    override var alive: Boolean = true

    var grazeCounter: Int = 0
    var t: Int = 0
    override val boundingRadiusX
        get() = data.texture.maxWidth * scaleX + data.texture.maxHeight * scaleY
    override val boundingRadiusY
        get() = data.texture.maxWidth * scaleX + data.texture.maxHeight * scaleY

    //AIO laser related field

    /**
     * (Laser) is this node an AIO laser?
     */
    var laser: Boolean = false
    /**
     * (Laser) Previous node in laser
     *
     * Modifying and reading this field out of a laser may cause unexpected behaviour!
     *
     * This is not an API. the API is [getPreviousNode]
     */
    var prev: BasicBullet? = null

    /**
     * (Laser) Next node in laser
     *
     * Modifying and reading this field out of a laser may cause unexpected behaviour!
     *
     * This is not an API. the API is [getNextNode]
     */
    var next: BasicBullet? = null
    /**
     * (Laser) Maximum length of this laser
     *
     * Modifying and reading this field out of a laser may cause unexpected behaviour!
     *
     */
    var maxLength = -1f
    /**
     * (Laser) Render width of this laser
     *
     * Modifying and reading this field out of a laser may cause unexpected behaviour!
     *
     * Hitbox is determined by this and [hitRatio]
     *
     * This is not an API. the API is [getPreviousNode]
     */
    var width = -1f

    /**
     * (Laser) Only hitRatio part will be considered as hitbox
     *
     * Modifying and reading this field out of a laser may cause unexpected behaviour!
     */
    var hitRatio = 0.8f

    /**
     * (Laser) Ignore the first/last length in laser
     *
     * Modifying and reading this field out of a laser may cause unexpected behaviour!
     */
    var verticalMargin = 20f

    /**
     * (Laser) Whether this node is activated for collision
     *
     * Modifying and reading this field out of a laser may cause unexpected behaviour!
     */
    var laserActivated = false

    /**
     * (Laser) Frame before it is stabilized
     *
     * Modifying and reading this field out of a laser may cause unexpected behaviour!
     */
    var protectionFrame = 20

    fun getPreviousNode(): BasicBullet? {
        return if (prev == null || !prev!!.alive) {
            null
        } else {
            prev
        }
    }

    fun getNextNode(): BasicBullet? {
        return if (next == null || !next!!.alive) {
            null
        } else {
            next
        }
    }

    fun isPartOfLaser(): Boolean {
        return laser
    }

    fun isLaserHead(): Boolean {
        return getPreviousNode() == null && isPartOfLaser()
    }

    /**
     * This takes O(n) to calculate
     */
    fun getLaserLength(): Float {
        var now = this
        var ans = 0f
        while (true) {
            val nxt = now.getNextNode()
            if (nxt != null) {
                ans += dist(nxt.x, nxt.y, now.x, now.y)
                now = nxt
            } else {
                break
            }
        }
        return min(ans, maxLength)
    }

    /**
     * Returns true if no node in this laser has collision
     */
    fun isMoribund(): Boolean{
        var now = this
        var ans = 0f
        while (true) {
            val nxt = now.getNextNode()
            if(now.laserActivated){
                return false
            }
            if (nxt != null && ans<=maxLength) {
                ans += dist(nxt.x, nxt.y, now.x, now.y)
                now = nxt
            } else {
                break
            }
        }
        return true
    }

    init {
        calculateDelta()
    }

    fun calculateDelta() {
        deltaX = cos(angle) * speed
        deltaY = sin(angle) * speed
    }

    override fun attachTask(task: Task): BasicBullet {
        if (attachedTasks == null) {
            attachedTasks = GdxArray()
        }
        attachedTasks!!.add(task)
        return this
    }

    override fun task(index: Int, block: suspend CoroutineScope.() -> Unit): BasicBullet {
        attachTask(CoroutineTask(index, this, block))
        return this
    }

    override fun tick() {
        if (t >= delay) {
            x += deltaX
            y += deltaY
        }
        protectionFrame--
        protectionFrame=protectionFrame.coerceAtLeast(0)

        t++
        if (attachedTasks != null) {
            for (i in 0 until attachedTasks!!.size) {
                if (attachedTasks!![i].alive) {
                    attachedTasks!![i].tick()
                } else {
                    attachedTasks!![i] = null
                }
            }
            attachedTasks!!.removeNull()
        }

    }

    override fun destroy() {
        game.addParticle(
            BulletDestroyParticle(
                data.texture.getFrame(t),
                x,
                y,
                data.width,
                data.height,
                scaleX,
                scaleY,
                data.color.tintHSV(tint),
                rotation,
            )
        )
        kill()
    }

    override fun kill(): Boolean {
        alive = false
        attachedTasks?.forEach { it.kill() }
        return true
    }

    override fun onGraze() {
        if (isPartOfLaser()) {
            if(t%20==0){
                game.graze++
                game.pointValue = (game.pointValue + 1L).coerceAtMost(game.maxPointValue)
                SE.play("graze")
                game.addParticle(GrazeParticle(x, y))
            }
            return
        }

        if (grazeCounter <= 0) {
            grazeCounter++
            game.graze++
            game.pointValue = (game.pointValue + 1L).coerceAtMost(game.maxPointValue)
            SE.play("graze")
            game.addParticle(GrazeParticle(x, y))
        }
    }


    fun drawAsLaser(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (batch != game.drawer.batch) {
            game.drawer = ShapeDrawer(batch, getRegion("ui/blank.png"))
            game.drawer.pixelSize = 0.5f
        }

        var current = this
        val node = Array<Vector2>()
        var totalDistance = 0f
        while (true) {
            node.add(vec2(current.x, current.y))
            if (current.getNextNode() == null || totalDistance > maxLength) {
                break
            } else {
                val next = current.getNextNode()!!
                totalDistance += dist(current.x, current.y, next.x, next.y)
                current = next
            }
        }

//        println("$this $totalDistance")
        game.drawer.setColor(tint)
        game.drawer.path(node, width, true)
        batch.draw(A.get<Texture>("player/reimuBall.png"), x - 8f, y - 8f, 16f, 16f)
        game.addParticle(GrazeParticle(current.x, current.y))


        //TODO laser enhancement
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {

        //laser render (special judge)
        /*if(isPartOfLaser()){
            //laser hitbox debug
            run {
                if (laserActivated) {
                    val bullet = this
                    val last = bullet.prev
                    if (last != null) {
                        val vec = vec2(bullet.x - last.x, bullet.y - last.y)
                        vec.rotate90(1).setLength(bullet.width * bullet.hitRatio / 2)
                        val _x1 = vec.x + last.x
                        val _y1 = vec.y + last.y
                        val _x2 = vec.x + bullet.x
                        val _y2 = vec.y + bullet.y
                        vec.rotateDeg(180f)
                        val x3 = vec.x + last.x
                        val y3 = vec.y + last.y
                        val x4 = vec.x + bullet.x
                        val y4 = vec.y + bullet.y
                        val polygon = Polygon(floatArrayOf(_x1, _y1, _x2, _y2, x4, y4, x3, y3))
                        game.drawer.setColor(WHITE_HSV)
                        game.drawer.polygon(polygon)
                    }
                }
            }
        }*/

        if (isLaserHead()) {
            drawAsLaser(batch, parentAlpha, subFrameTime)
            return
        }
        if (isPartOfLaser()) {
            return
        }

        if (!outOfFrame(x, y, boundingRadiusX, boundingRadiusY)) {
            var tmpX = x
            var tmpY = y
            if (subFrameTime != 0f) {
                tmpX += deltaX * subFrameTime
                tmpY += deltaY * subFrameTime
            }

            val texture = data.texture.getFrame(t)
            tmpColor.set(batch.color)
            if (t >= delay) {
                val bulletColor = data.color.tintHSV(tint)
                bulletColor.a *= parentAlpha
                batch.color = bulletColor
                if ((rotation + data.rotation) != 0f || scaleX != 1f || scaleY != 1f) {
                    batch.draw(
                        texture,
                        tmpX - data.originX,
                        tmpY - data.originY,
                        data.originX,
                        data.originY,
                        data.width,
                        data.height,
                        scaleX,
                        scaleY,
                        rotation + data.rotation,
                    )
                } else {
                    batch.draw(
                        texture,
                        tmpX - data.originX,
                        tmpY - data.originY,
                        data.width,
                        data.height,
                    )
                }
            } else {
                val scaleFactor = lerp(2f, 0.8f, t.toFloat() / delay)
                val delayColor = data.delayColor.tintHSV(tint)
                delayColor.a *= parentAlpha
                delayColor.a *= lerp(0.2f, 1f, t.toFloat() / delay)
                batch.color = delayColor
                batch.draw(
                    data.delayTexture,
                    tmpX - data.originX,
                    tmpY - data.originY,
                    data.originX,
                    data.originY,
                    data.width,
                    data.height,
                    scaleX * scaleFactor,
                    scaleY * scaleFactor,
                    rotation,
                )
            }
            batch.color = tmpColor
        }
    }
}