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

package com.hhs.koto.stg.shot

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.app.Config
import com.hhs.koto.stg.Collision

class BulletData(parent: ShotSheet, raw: ShotSheetLoader.RawShotSheet.RawBulletData) {
    var id: Int = raw.id
    var name: String = raw.name
    var render: String = raw.render ?: Config.defaultBlending
    var originX = 0f
    var originY = 0f
    var texture: BulletTexture
    var delayTexture: TextureRegion
    var delayColor: Color
    var spinVelocity = 0f
    var collision: Collision.CollisionData? = null
    var rotation = 0f

    init {
        spinVelocity = raw.spinVelocity
        rotation = raw.rotation
        texture = BulletTexture(parent.atlas, name, raw.frames)
        collision = when (raw.collisionMethod) {
            "Circle" -> Collision.Circle(raw.collisionData!!.get(0))
            "Rectangle" -> Collision.Rectangle(raw.collisionData!!.get(0), raw.collisionData!!.get(1))
            else -> Collision.Circle(raw.collisionData!!.get(0))
        }
        originX = raw.originX ?: texture.maxWidth / 2f
        originY = raw.originY ?: texture.maxWidth / 2f
        delayColor = Color.valueOf(raw.delayColor)
        delayTexture = parent.atlas.findRegion(raw.delaySrc)
    }
}