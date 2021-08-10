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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.crashinvaders.vfx.VfxManager
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.VfxOutputDrawable
import com.hhs.koto.stg.bullet.Bullet
import com.hhs.koto.stg.bullet.PlayerBullet
import com.hhs.koto.stg.drawable.DrawableLayer
import com.hhs.koto.stg.drawable.Enemy
import com.hhs.koto.stg.drawable.OptimizedLayer
import com.hhs.koto.stg.item.Item
import com.hhs.koto.stg.task.ParallelTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.*
import ktx.app.clearScreen

class KotoGame : Disposable {
    val backgroundVfx = VfxManager(Pixmap.Format.RGBA8888, Config.frameWidth, Config.frameHeight)
    val vfx = VfxManager(Pixmap.Format.RGBA8888, Config.frameWidth, Config.frameHeight)
    val postVfx = VfxManager(Pixmap.Format.RGBA8888, Config.frameWidth, Config.frameHeight)

    val tasks = ParallelTask()
    val backgroundViewport =
        StretchViewport(Config.worldW, Config.worldH, OrthographicCamera(Config.worldW, Config.worldH).apply {
            position.x = Config.worldW / 2f - Config.originX
            position.y = Config.worldH / 2f - Config.originY
        })
    val stageViewport =
        StretchViewport(Config.worldW, Config.worldH, OrthographicCamera(Config.worldW, Config.worldH).apply {
            position.x = Config.worldW / 2f - Config.originX
            position.y = Config.worldH / 2f - Config.originY
        })
    val hudViewport =
        StretchViewport(Config.worldW, Config.worldH, OrthographicCamera(Config.worldW, Config.worldH).apply {
            position.x = Config.worldW / 2f - Config.originX
            position.y = Config.worldH / 2f - Config.originY
        })

    val batch = SpriteBatch(
        1000,
        ShaderProgram(
            Gdx.files.classpath("gdxvfx/shaders/default.vert").readString(),
            A["shader/koto_hsv.frag"],
        ),
    )
    val normalBatch = SpriteBatch()
    val background = DrawableLayer<Drawable>()
    val stage = DrawableLayer<Drawable>().apply {
        addDrawable(VfxOutputDrawable(backgroundVfx, -Config.originX, -Config.originY, Config.worldW, Config.worldH))
    }
    val hud = DrawableLayer<Drawable>().apply {
        addDrawable(VfxOutputDrawable(vfx, -Config.originX, -Config.originY, Config.worldW, Config.worldH))
    }

    private var subFrameTime: Float = 0f
    var frame: Int = 0
    var speedUpMultiplier: Int = 1
    val frameScheduler = FrameScheduler(this)
    val logger = Logger("Game", Config.logLevel)
    val playerBullets = OptimizedLayer<PlayerBullet>(
        -10, Rectangle(
            -Config.bulletDeleteDistance - Config.originX,
            -Config.bulletDeleteDistance - Config.originY,
            Config.bulletDeleteDistance * 2 + Config.worldW,
            Config.bulletDeleteDistance * 2 + Config.worldH,
        )
    ).apply {
        stage.addDrawable(this)
    }
    val bullets = OptimizedLayer<Bullet>(
        0, Rectangle(
            -Config.bulletDeleteDistance - Config.originX,
            -Config.bulletDeleteDistance - Config.originY,
            Config.bulletDeleteDistance * 2 + Config.worldW,
            Config.bulletDeleteDistance * 2 + Config.worldH,
        )
    ).apply {
        stage.addDrawable(this)
    }
    val items = OptimizedLayer<Item>(
        -400, Rectangle(
            -32768f,
            -32f - Config.originY,
            65536f,
            32768f,
        )
    ).apply {
        stage.addDrawable(this)
    }
    val particles = OptimizedLayer<Drawable>(200).apply {
        stage.addDrawable(this)
    }
    val enemies = DrawableLayer<Enemy>(-350).apply {
        stage.addDrawable(this)
    }
    lateinit var player: Player
    var maxScore: Long = 10000
    var maxScoreHeight: Float = Config.worldH / 4f * 3f - 50f - Config.originY
    var score: Long = 0
    val life = FragmentCounter()
    val bomb = FragmentCounter()
    var power: Float = 1f
    var graze: Int = 0

    init {
        logger.info("Game instance created.")
    }

    fun update() {
        speedUpMultiplier = if (keyPressed(options.keySpeedUp)) {
            options.speedUpMultiplier
        } else {
            1
        }
        frameScheduler.update()
    }

    fun act(delta: Float) {
        game.backgroundVfx.update(delta)
        game.vfx.update(delta)
        game.postVfx.update(delta)
        subFrameTime += delta
    }

    fun tick() {
        subFrameTime = 0f
        game.background.tick()
        game.stage.tick()
        game.hud.tick()
        game.tasks.tick()
        game.frame++
    }

    fun draw() {
        backgroundVfx.beginInputCapture()
        clearScreen(0f, 0f, 0f, 1f)
        batch.projectionMatrix = backgroundViewport.camera.combined
        batch.begin()
        background.draw(batch, 1f, subFrameTime)
        batch.end()
        backgroundVfx.endInputCapture()
        backgroundVfx.applyEffects()

        stageViewport.update(Config.frameWidth, Config.frameHeight)
        vfx.beginInputCapture()
        clearScreen(0f, 0f, 0f, 1f)
        batch.projectionMatrix = stageViewport.camera.combined
        batch.begin()
        stage.draw(batch, 1f, subFrameTime)
        batch.end()
        vfx.endInputCapture()
        vfx.applyEffects()

        hudViewport.apply()
        postVfx.beginInputCapture()
        clearScreen(0f, 0f, 0f, 1f)
        batch.projectionMatrix = hudViewport.camera.combined
        batch.begin()
        hud.draw(batch, 1f, subFrameTime)
        batch.end()
        postVfx.endInputCapture()
        postVfx.applyEffects()

        app.viewport.apply()
    }

    override fun dispose() {
        backgroundVfx.dispose()
        vfx.dispose()
        postVfx.dispose()
        disposeRegisteredEffects()
        batch.shader.dispose()
        logger.info("Game instance disposed.")
    }
}

fun <T : Task> addTask(task: T): T {
    game.tasks.addTask(task)
    return task
}

fun <T : Bullet> addBullet(bullet: T): T {
    game.bullets.add(bullet)
    return bullet
}

fun <T : Item> addItem(item: T): T {
    game.items.add(item)
    return item
}

fun <T : Drawable> addParticle(particle: T): T {
    game.particles.add(particle)
    return particle
}

fun <T : Enemy> addEnemy(enemy: T): T {
    game.enemies.addDrawable(enemy)
    return enemy
}
