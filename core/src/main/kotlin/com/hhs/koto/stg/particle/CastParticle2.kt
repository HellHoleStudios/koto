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

package com.hhs.koto.stg.particle

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.stg.graphics.SpriteDrawable
import com.hhs.koto.util.*
import ktx.math.vec2

/**
 * Particle displayed during boss cast2 (aka. explode?)
 */
class CastParticle2(x: Float, y: Float, val tx: Float, val ty: Float) : SpriteDrawable(
    getRegion("particle/cast_particle.png"),
    x,
    y,
    0f,
    atan2(x, y, tx, ty),
    1f,
    1f,
    64f,
    64f,
    0f,
    color = Color(1f,1f,1f,0.9f)
//    color = arrayOf(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.CYAN, Color.WHITE, Color.BLACK)[random(6)].cpy().apply { a=0.4f },
) {

    val iv = vec2(tx - x, ty - y)
    var omega = 0f
    val sx=x
    val sy=y
    val sz = random(0.5f, 1.5f)

    init {
        sprite.setScale(0f)
    }

    override fun tick() {
        super.tick()

        sprite.rotation += omega
        omega += 0.1f

        x=smoothstep(sx,tx,t/60f)
        y=smoothstep(sy,ty,t/60f)

        sprite.setScale(smoothstep(0f,sz,t/60f))

        if(t>60){
            sprite.alpha = max(sprite.alpha - 0.1f, 0f)
        }

        if(t>70){
            kill()
        }
    }
}