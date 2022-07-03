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

package com.hhs.koto.app.ui

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.hhs.koto.util.SE
import com.hhs.koto.util.VK
import ktx.collections.GdxArray
import kotlin.math.abs

open class Grid(
    override val gridX: Int = 0,
    override val gridY: Int = 0,
    var cycle: Boolean = true,
    final override var staticX: Float = 0f,
    final override var staticY: Float = 0f,
    width: Float = 0f,
    height: Float = 0f,
    var activeAction: (() -> Action)? = null,
    var inactiveAction: (() -> Action)? = null,
    val selectSound: String? = "select",
) : Group(), GridComponent, InputProcessor {
    val grid = GdxArray<GridComponent>()
    var selectedX: Int = 0
    var selectedY: Int = 0

    override var parent: Grid? = null
    override var active: Boolean = true
        set(value) {
            field = value
            update()
        }
    override var enabled: Boolean = true
    private var minX = Int.MAX_VALUE
    private var minY = Int.MAX_VALUE
    private var maxX = Int.MIN_VALUE
    private var maxY = Int.MIN_VALUE

    init {
        setBounds(staticX, staticY, width, height)
    }

    final override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
    }

    override fun update() {
        clearActions()
        if (active) {
            if (activeAction != null) {
                addAction(activeAction!!())
            }
        } else {
            if (inactiveAction != null) {
                addAction(inactiveAction!!())
            }
        }
        updateComponent()
    }

    open fun updateComponent(): Grid {
        grid.forEach {
            if (it is Grid) {
                it.updateComponent()
            } else if (it is GridButtonBase) {
                if (!it.ignoreParent) it.update()
            } else {
                it.update()
            }
        }
        return this
    }

    override fun trigger() = Unit

    override fun keyDown(keycode: Int): Boolean {
        if (!enabled || !active) return false
        when (keycode) {
            VK.UP.uiKeycode -> select(selectedX, selectedY - 1, 0, -1)
            VK.DOWN.uiKeycode -> select(selectedX, selectedY + 1, 0, 1)
            VK.LEFT.uiKeycode -> select(selectedX - 1, selectedY, -1, 0)
            VK.RIGHT.uiKeycode -> select(selectedX + 1, selectedY, 1, 0)
            VK.SELECT.uiKeycode -> triggerButton()
            VK.CANCEL.uiKeycode -> exit()
        }
        return false
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amountX: Float, amountY: Float) = false

    open fun exit() {
        if (parent != null) {
            SE.play("cancel")
            parent!!.enabled = true
            enabled = false
        }
    }

    fun triggerButton(): Boolean {
        var flag = false
        for (i in 0 until grid.size) {
            val button = grid[i]
            if (button.active) {
                button.trigger()
                if (button is Grid) button.triggerButton()
                flag = true
            }
        }
        return flag
    }

    open fun add(component: GridComponent): Grid {
        grid.add(component)
        component.parent = this
        minX = minX.coerceAtMost(component.gridX)
        minY = minY.coerceAtMost(component.gridY)
        maxX = maxX.coerceAtLeast(component.gridX)
        maxY = maxY.coerceAtLeast(component.gridY)
        if (component is Actor) {
            addActor(component as Actor)
        }
        return this
    }

    override fun clear() {
        super.clear()
        grid.clear()
    }

    override fun clearChildren() {
        super.clearChildren()
        grid.clear()
    }

    open fun selectFirst(): Grid {
        val component = grid.firstOrNull { it.enabled } ?: return this
        selectedX = component.gridX
        selectedY = component.gridY
        select(selectedX, selectedY, true)
        updateComponent()
        return this
    }

    open fun selectLast(): Grid {
        val component = grid.lastOrNull { it.enabled } ?: return this
        selectedX = component.gridX
        selectedY = component.gridY
        select(selectedX, selectedY, true)
        updateComponent()
        return this
    }

    fun select(component: GridComponent, silent: Boolean = false) = select(component.gridX, component.gridY, silent)

    open fun select(nx: Int, ny: Int, dx: Int, dy: Int, silent: Boolean = false): Grid {
        var closest: GridComponent? = null
        var dist = Int.MAX_VALUE
        for (i in 0 until grid.size) {
            val component = grid[i]
            if (component.enabled) {
                if (dx != 0) {
                    if (component.gridY == selectedY && distanceX(nx, component.gridX, dx) < dist) {
                        closest = component
                        dist = distanceX(nx, component.gridX, dx)
                    }
                } else {
                    if (component.gridX == selectedX && distanceY(ny, component.gridY, dy) < dist) {
                        closest = component
                        dist = distanceY(ny, component.gridY, dy)
                    }
                }
            }
        }
        if (closest == null) {
            return this
        }
        if (selectSound != null && !silent && (closest.gridX != selectedX || closest.gridY != selectedY)) {
            SE.play(selectSound)
        }
        selectedX = closest.gridX
        selectedY = closest.gridY
        grid.forEach {
            if (it.active && it.enabled && (it.gridX != selectedX || it.gridY != selectedY)) {
                it.active = false
            }
            if (!it.active && it.enabled && it.gridX == selectedX && it.gridY == selectedY) {
                it.active = true
            }
        }
        return this
    }

    open fun select(nx: Int, ny: Int, silent: Boolean = false): Grid {
        var closest: GridComponent? = null
        var dist = Int.MAX_VALUE
        for (i in 0 until grid.size) {
            val component = grid[i]
            if (component.enabled) {
                if (distance(nx, ny, component.gridX, component.gridY) < dist) {
                    closest = component
                    dist = distance(nx, ny, component.gridX, component.gridY)
                }
            }
        }
        if (closest == null) {
            return this
        }
        if (selectSound != null && !silent && (closest.gridX != selectedX || closest.gridY != selectedY)) {
            SE.play(selectSound)
        }
        selectedX = closest.gridX
        selectedY = closest.gridY
        grid.forEach {
            if (it.enabled && (it.gridX != selectedX || it.gridY != selectedY)) {
                it.active = false
            }
            if (it.enabled && it.gridX == selectedX && it.gridY == selectedY) {
                it.active = true
            }
        }
        return this
    }

    private fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Int {
        var x1tmp = x1
        var y1tmp = y1
        if (cycle) {
            if (x1tmp < minX) x1tmp = maxX
            if (x1tmp > maxX) x1tmp = minX
            if (y1tmp < minY) y1tmp = maxY
            if (y1tmp > maxY) y1tmp = minY
        }
        return abs(x1tmp - x2) + abs(y1tmp - y2)
    }

    private fun distanceX(x1: Int, x2: Int, dx: Int): Int {
        return if (cycle) {
            var x1tmp = x1
            if (x1tmp < minX) x1tmp = maxX
            if (x1tmp > maxX) x1tmp = minX
            if (dx > 0) {
                if (x1tmp <= x2) {
                    x2 - x1tmp
                } else {
                    x2 - x1tmp + maxX - minX + 1
                }
            } else {
                if (x1tmp >= x2) {
                    x1tmp - x2
                } else {
                    x1tmp - x2 + maxX - minX + 1
                }
            }
        } else {
            if (dx > 0) {
                if (x1 <= x2) {
                    x2 - x1
                } else {
                    Int.MAX_VALUE
                }
            } else {
                if (x1 >= x2) {
                    x1 - x2
                } else {
                    Int.MAX_VALUE
                }
            }
        }
    }

    private fun distanceY(y1: Int, y2: Int, dy: Int): Int {
        return if (cycle) {
            var y1tmp = y1
            if (y1tmp < minY) y1tmp = maxY
            if (y1tmp > maxY) y1tmp = minY
            if (dy > 0) {
                if (y1tmp <= y2) {
                    y2 - y1tmp
                } else {
                    y2 - y1tmp + maxY - minY + 1
                }
            } else {
                if (y1tmp >= y2) {
                    y1tmp - y2
                } else {
                    y1tmp - y2 + maxY - minY + 1
                }
            }
        } else {
            if (dy > 0) {
                if (y1 <= y2) {
                    y2 - y1
                } else {
                    Int.MAX_VALUE
                }
            } else {
                if (y1 >= y2) {
                    y1 - y2
                } else {
                    Int.MAX_VALUE
                }
            }
        }
    }

    open fun arrange(rootX: Float, rootY: Float, offsetX: Float, offsetY: Float): Grid {
        grid.forEach {
            if (it is Actor) {
                it.setPosition(rootX + offsetX * it.gridX, rootY + offsetY * it.gridY)
            }
            if (it is GridComponent) {
                it.staticX = rootX + offsetX * it.gridX
                it.staticY = rootY + offsetY * it.gridY
            }
        }
        return this
    }

    operator fun get(i: Int) = getChild(i) as GridComponent
}

fun <T : Grid> T.register(st: Stage, input: InputMultiplexer? = null): T {
    st.addActor(this)
    input?.addProcessor(this)
    return this
}