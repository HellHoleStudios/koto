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

package com.hhs.koto.stg

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport

class IndexedStage : Stage {
    constructor() : super()
    constructor(viewport: Viewport) : super(viewport)
    constructor(viewport: Viewport, batch: Batch) : super(viewport, batch)

    override fun addActor(actor: Actor) {
        if (actor is IndexedActor) {
            for (i in 0 until root.children.size) {
                val currentActor = root.children[i]
                if (currentActor is IndexedActor && currentActor.z > actor.z) {
                    root.addActorAt(i, actor)
                    return
                }
            }
            root.addActor(actor)
        } else {
            super.addActor(actor)
        }
    }

    operator fun plusAssign(actor: Actor) = addActor(actor)
}

interface IndexedActor {
    val z: Int
}

class IndexedGroup(override val z: Int) : IndexedActor, Group()