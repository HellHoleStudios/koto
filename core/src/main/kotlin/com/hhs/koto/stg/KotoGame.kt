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

package com.hhs.koto.stg

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.crashinvaders.vfx.VfxManager
import com.hhs.koto.app.Config
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.stg.bullet.BasicBullet
import com.hhs.koto.stg.graphics.VfxOutputDrawable
import com.hhs.koto.stg.bullet.Bullet
import com.hhs.koto.stg.bullet.PlayerBullet
import com.hhs.koto.stg.graphics.*
import com.hhs.koto.stg.item.Item
import com.hhs.koto.stg.task.ParallelTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.*
import ktx.app.clearScreen
import space.earlygrey.shapedrawer.ShapeDrawer
import java.util.*

class KotoGame : Disposable {
    var state: GameState = GameState.RUNNING

    val backgroundVfx = VfxManager(Pixmap.Format.RGBA8888, options.frameBufferWidth, options.frameBufferHeight)
    val vfx = VfxManager(Pixmap.Format.RGBA8888, options.frameBufferWidth, options.frameBufferHeight)
    val postVfx = VfxManager(Pixmap.Format.RGBA8888, options.frameBufferWidth, options.frameBufferHeight)

    val tasks = ParallelTask()
    val backgroundViewport =
        StretchViewport(worldW, worldH, OrthographicCamera(worldW, worldH).apply {
            position.x = worldW / 2 - worldOriginX
            position.y = worldH / 2 - worldOriginY
        })
    val stageViewport =
        StretchViewport(worldW, worldH, OrthographicCamera(worldW, worldH).apply {
            position.x = worldW / 2 - worldOriginX
            position.y = worldH / 2 - worldOriginY
        })
    val hudViewport =
        StretchViewport(worldW, worldH, OrthographicCamera(worldW, worldH).apply {
            position.x = worldW / 2 - worldOriginX
            position.y = worldH / 2 - worldOriginY
        })

    val batch = SpriteBatch(
        1000,
        ShaderProgram(
            Gdx.files.classpath("gdxvfx/shaders/default.vert").readString(),
            A["shader/koto_hsv.frag"],
        ),
    ).apply {
        setBlending(BlendingMode.ALPHA)
    }

    /**
     * Drawer to draw lasers
     */
    var drawer = ShapeDrawer(batch, getRegion("ui/blank.png"))

    val normalBatch = SpriteBatch()
    val background = DrawableLayer<Drawable>()
    val stage = DrawableLayer<Drawable>().apply {
        addDrawable(VfxOutputDrawable(backgroundVfx, -worldOriginX, -worldOriginY, worldW, worldH))
    }
    val hud = DrawableLayer<Drawable>().apply {
        addDrawable(VfxOutputDrawable(vfx, -worldOriginX, -worldOriginY, worldW, worldH))
    }
    val overlay = Stage(app.viewport, app.batch)
    var globalAlpha: Float = 1f

    private var subFrameTime: Float = 0f
    var frame: Int = 0
    var speedUpMultiplier: Int = 1
    val frameScheduler = FrameScheduler(this)
    val logger = Logger("Game", Config.logLevel)
    val playerBullets = OptimizedLayer<PlayerBullet>(
        -100, Rectangle(
            -Config.bulletDeleteDistance - worldOriginX,
            -Config.bulletDeleteDistance - worldOriginY,
            Config.bulletDeleteDistance * 2 + worldW,
            Config.bulletDeleteDistance * 2 + worldH,
        )
    ).apply {
        stage.addDrawable(this)
    }
    val bullets = OptimizedLayer<Bullet>(
        0, Rectangle(
            -Config.bulletDeleteDistance - worldOriginX,
            -Config.bulletDeleteDistance - worldOriginY,
            Config.bulletDeleteDistance * 2 + worldW,
            Config.bulletDeleteDistance * 2 + worldH,
        )
    ).apply {
        stage.addDrawable(this)
    }
    val items = OptimizedLayer<Item>(
        -400, Rectangle(
            -32768f,
            -32f - worldOriginY,
            65536f,
            32768f,
        )
    ).apply {
        stage.addDrawable(this)
    }
    val particles = OptimizedLayer<Drawable>(200).apply {
        stage.addDrawable(this)
    }
    val enemies = DrawableLayer<Enemy>(50).apply {
        stage.addDrawable(this)
    }
    val bosses = DrawableLayer<Boss>(100).apply {
        stage.addDrawable(this)
    }
    val bossNameDisplay = BossNameDisplay().apply {
        hud.addDrawable(this)
    }
    val spellTimer = SpellTimer().apply {
        hud.addDrawable(this)
    }

    val event = EventManager()

    lateinit var player: Player
    var pointValue: Long = 10000
    var maxPointValue: Long = difficultySelect(100000L, 250000L, 500000L, 500000L, 500000L)
    var pointValueHeight: Float = worldH / 4f * 3f - 50f - worldOriginY
    var score: Long = 0
    var highScore: Long = 0
    var highScoreAchieved: Boolean = false
    var maxCredit: Int = when (SystemFlag.gamemode!!) {
        GameMode.SPELL_PRACTICE -> 0
        else -> difficultySelect(3, 3, 4, 5, 0)
    }
    var creditCount: Int = 0
    val initialLife: FragmentCounter = when (SystemFlag.gamemode!!) {
        GameMode.SPELL_PRACTICE -> FragmentCounter(3, 100, 0, 8)
        GameMode.STAGE_PRACTICE -> FragmentCounter(3, 8, 0, 8)
        else -> FragmentCounter(3, 2, 0, 8)
    }
    val initialBomb: FragmentCounter = when (SystemFlag.gamemode!!) {
        GameMode.SPELL_PRACTICE -> FragmentCounter(5, 100, 0, 8)
        else -> FragmentCounter(5, 3, 0, 8)
    }
    val life: FragmentCounter = initialLife.copy()
    val bomb: FragmentCounter = initialBomb.copy()
    var maxPower: Float = 4f
    var power: Float = 1f
    var graze: Int = 0
    var inDialog: Boolean = false

    val random = RandomXS128()
    val replay: Replay
    val inReplay: Boolean

    /**
     * Enable slow mode??
     * Force limiting FPS to around the number you set
     *
     * Set to 0 to disable it
     */
    var slowMode:Int = 0

    /**
     * Enable shaking??
     * Will shake game screen with different strength
     *
     * Set to 0 to disable it
     */
    var shaking:Int = 0
    init {
        logger.info("Game instance created.")
        if (SystemFlag.replay != null) {
            replay = SystemFlag.replay!!
            inReplay = true
            logger.info("Running in replay mode.")
        } else {
            replay = Replay()
            replay.saveSystemFlags()
            inReplay = false
            when (SystemFlag.gamemode!!) {
                GameMode.REGULAR, GameMode.EXTRA -> {
                    gameData.currentElement.score.forEach {
                        highScore = highScore.coerceAtLeast(it.score)
                    }
                }
                GameMode.STAGE_PRACTICE -> {
                    highScore = gameData.currentElement.practiceHighScore[SystemFlag.sessionName!!]
                }
                GameMode.SPELL_PRACTICE -> {
                    highScore = gameData.currentElement.spell[SystemFlag.sessionName!!].highScore
                }
            }
        }
    }

    fun resetPlayer() {
        if (this::player.isInitialized) player.kill()
        val playerName = SystemFlag.shottype ?: throw KotoRuntimeException("player flag is null!")
        game.player =
            (GameBuilder.shottypes.find { it.first == playerName }
                ?: throw KotoRuntimeException("player \"$playerName\" not found!")).second()
    }

    fun createScoreEntry(): GameData.ScoreEntry =
        GameData.ScoreEntry("", Date(), score, creditCount)

    fun update() {
        speedUpMultiplier = if (VK.SPEED_UP.pressed() && (inReplay || Config.allowSpeedUpOutOfReplay)) {
            options.speedUpMultiplier
        } else {
            1
        }
        frameScheduler.update()
    }

    fun act(delta: Float) {
        backgroundVfx.update(delta)
        vfx.update(delta)
        postVfx.update(delta)
        overlay.act(delta)
        subFrameTime += delta
    }

    fun tick() {
        if (!inReplay) {
            replay.logKeys()
        } else {
            if (frame >= replay.frameCount) {
                end()
                return
            }
        }
        subFrameTime = 0f
        event.trigger("tick")
        if (!highScoreAchieved && score > highScore) {
            highScoreAchieved = true
            hud.addDrawable(
                TextNotification(
                    bundle["game.highScore"],
                    80f,
                    font = bundle["font.notification"],
                    fontSize = 36,
                )
            )
        }

        //laser deactivate
        game.bullets.forEach {
            if(it is BasicBullet){
                it.laserActivated=false
            }
        }
        game.bullets.forEach {
            if (it is BasicBullet && it.isLaserHead()) { //tick laser activate
                var last: BasicBullet? = null
                val bullet = it as BasicBullet
                var now = bullet
                var accumulatedDistance = 0f
                val len = bullet.getLaserLength()
                while (true) {
                    if (last != null) {
                        accumulatedDistance += dist(last.x, last.y, now.x, now.y)
                        if (accumulatedDistance in bullet.verticalMargin..len - bullet.verticalMargin) {
                            now.laserActivated = true
                        }
                    }
                    last = now
                    if (now.getNextNode() == null) {
                        break
                    } else {
                        now = now.getNextNode()!!
                    }
                }
            }
        }
        game.bullets.forEach {
//            println("Trying to destroy $it with ${(it as BasicBullet).isLaserHead()} ${(it as BasicBullet).getLaserLength()} ${(it as BasicBullet).protectionFrame}")
            if(it is BasicBullet && it.isLaserHead() && it.isMoribund() && it.protectionFrame==0){
//                println("Destroying $it")
                it.destroy()
            }
        }

        background.tick()
        stage.tick()
        hud.tick()
        tasks.tick()

        if(slowMode!=0){
            Thread.sleep(1000L/slowMode)
        }

        frame++

        if (VK.PAUSE.pressed()) {
            state = GameState.PAUSED
            return
        }
    }

    fun end() {
        state = if (SystemFlag.gamemode == GameMode.SPELL_PRACTICE || SystemFlag.gamemode == GameMode.STAGE_PRACTICE) {
            GameState.FINISH_PRACTICE
        } else {
            GameState.FINISH
        }
    }

    fun draw() {
        event.trigger("draw")

        backgroundVfx.beginInputCapture()
        clearScreen(0f, 0f, 0f, 1f)
        batch.projectionMatrix = backgroundViewport.camera.combined
        batch.begin()
        background.draw(batch, 1f, subFrameTime)
        batch.end()
        backgroundVfx.endInputCapture()
        backgroundVfx.applyEffects()

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
        hud.draw(batch, globalAlpha, subFrameTime)
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

    fun bonus(text: String, bonus: Long, color: Color = WHITE_HSV) {
        score += bonus
        hud.addDrawable(TextNotification(text, color = color))
        hud.addDrawable(
            TextNotification(
                String.format("%,d", bonus),
                120f,
                font = bundle["font.numerical"],
                fontSize = 36,
            )
        )
    }

    fun addLife(completedCount: Int = 0, fragmentCount: Int = 0) {
        val oldCompleted = life.completedCount
        life.add(completedCount, fragmentCount)
        if (life.completedCount != oldCompleted) {
            SE.play("extend")
            hud.addDrawable(
                TextNotification(
                    bundle["game.extend"],
                    100f,
                    font = bundle["font.notification"],
                    fontSize = 36,
                )
            )
        }
    }

    fun addBomb(completedCount: Int = 0, fragmentCount: Int = 0) {
        bomb.add(completedCount, fragmentCount)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Boss> findBoss(clazz: Class<T>): T? {
        bosses.forEach {
            if (it.javaClass == clazz) {
                return it as T
            }
        }
        return null
    }

    fun <T : Task> addTask(task: T): T {
        tasks.addTask(task)
        return task
    }

    fun <T : Bullet> addBullet(bullet: T): T {
        bullets.add(bullet)
        return bullet
    }

    fun <T : Item> addItem(item: T): T {
        items.add(item)
        return item
    }

    fun <T : Drawable> addParticle(particle: T): T {
        particles.add(particle)
        return particle
    }

    fun <T : Enemy> addEnemy(enemy: T): T {
        enemies.addDrawable(enemy)
        return enemy
    }

    fun <T : Boss> addBoss(boss: T): T {
        bosses.addDrawable(boss)
        return boss
    }

    fun addListener(event: String, listener: (Array<out Any?>) -> Unit): (Array<out Any?>) -> Unit {
        this.event.addListener(event, listener)
        return listener
    }

    fun removeListener(event: String, listener: (Array<out Any?>) -> Unit): (Array<out Any?>) -> Unit {
        this.event.removeListener(event, listener)
        return listener
    }

    fun pressed(vk: VK): Boolean {
        return replay.pressed(vk, frame)
    }

    fun justPressed(vk: VK): Boolean {
        return replay.justPressed(vk, frame)
    }
}