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

package com.hhs.koto.app

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.WindowedMean
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.hhs.koto.app.screen.*
import com.hhs.koto.app.ui.FPSDisplay
import com.hhs.koto.demo.RegularGame
import com.hhs.koto.demo.player.MarisaPlayer
import com.hhs.koto.demo.player.ReimuPlayer
import com.hhs.koto.demo.stage1.Spell1
import com.hhs.koto.demo.stage1.Stage1
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.stg.GameData
import com.hhs.koto.util.*
import ktx.actors.plusAssign
import ktx.app.clearScreen
import ktx.collections.GdxMap
import ktx.collections.set
import java.util.*

class KotoApp(val callbacks: KotoCallbacks) : ApplicationListener {
    lateinit var batch: SpriteBatch
    lateinit var normalBatch: SpriteBatch
    lateinit var viewport: Viewport
    private lateinit var st: Stage
    private lateinit var fps: FPSDisplay
    lateinit var fpsCounter: WindowedMean

    val screens = GdxMap<String, KotoScreen>()
    var input = InputMultiplexer()
    val logger = Logger("Main", Config.logLevel)
    var autoSaveCounter: Float = 0f

    override fun create() {
        app = this

        loadOptions()
        initA()

        loadAssetIndex(Gdx.files.internal(".assets.json"))
        A.finishLoading()

        Gdx.app.logLevel = Config.logLevel
        logger.info("Game start.")
        Locale.setDefault(Locale.ROOT)
        bundle = I18NBundle.createBundle(Gdx.files.internal("locale/locale"), options.locale, "UTF-8")
        Config.UIFont = bundle["font.ui"]
        Config.UIFontSmall = bundle["font.uiSmall"]

        batch = SpriteBatch(
            1000,
            ShaderProgram(
                Gdx.files.classpath("gdxvfx/shaders/default.vert").readString(),
                A["shader/koto_hsv.frag"],
            ),
        )
        normalBatch = SpriteBatch()

        fpsCounter = WindowedMean(10)
        viewport = ScalingViewport(Config.windowScaling, Config.screenWidth, Config.screenHeight)
        st = Stage(viewport, batch)
        st.isDebugAll = Config.debugActorLayout

        Gdx.input.inputProcessor = input

        fps = FPSDisplay()
        st += fps

        // TODO rename
        SE.register("cancel", "snd/se_cancel00.wav")
        SE.register("invalid", "snd/se_invalid.wav")
        SE.register("ok", "snd/se_ok00.wav")
        SE.register("select", "snd/se_select00.wav")
        SE.register("pldead", "snd/se_pldead00.wav")
        SE.register("item", "snd/se_item00.wav")
        SE.register("graze", "snd/se_graze.wav")
        SE.register("shoot", "snd/se_plst00.wav")
        SE.register("pause", "snd/se_pause.wav")
        SE.register("bomb", "snd/se_nep00.wav")
        SE.register("enemydead", "snd/se_enep00.wav")
        SE.register("bossdead", "snd/se_enep01.wav")
        SE.register("spellbreak", "snd/se_enep02.wav")
        SE.register("damage0", "snd/se_damage00.wav")
        SE.register("damage1", "snd/se_damage01.wav")
        SE.register("timeout0", "snd/se_timeout.wav")
        SE.register("timeout1", "snd/se_timeout2.wav")
        SE.register("cardget", "snd/se_cardget.wav")
        SE.register("spellcard", "snd/se_cat00.wav")

        BGM.register(LoopingMusic("mus/E0120.ogg", 2f, 58f))
        defaultShotSheet = A[Config.defaultShotSheet]

        // TODO variants
        GameBuilder.players["reimuA"] = { ReimuPlayer() }
        GameBuilder.players["reimuB"] = { ReimuPlayer() }
        GameBuilder.players["reimu"] = { ReimuPlayer() }
        GameBuilder.players["marisaA"] = { MarisaPlayer() }
        GameBuilder.players["marisaB"] = { MarisaPlayer() }
        GameBuilder.players["marisa"] = { MarisaPlayer() }

        GameBuilder.regularGame = RegularGame()
        GameBuilder.stages.add(Stage1())
        GameBuilder.spells.add(Spell1())

        loadGameData()

        screens["blank"] = BlankScreen()
        screens["title"] = TitleScreen()
        screens["game"] = GameScreen()
        screens["save"] = SaveScreen()
        screens["difficultySelect"] = DifficultySelectScreen()
        screens["playerSelect"] = PlayerSelectScreen()
        screens["stageSelect"] = StageSelectScreen()
        screens["spellSelect"] = SpellSelectScreen()
        screens["musicRoom"] = MusicRoomScreen()
        screens["options"] = OptionsScreen()
        setScreen("blank")
        setScreen("title", 1f)
    }

    override fun resize(width: Int, height: Int) {
        screens.safeValues().filter { it.state.isRendered() }.forEach { it.resize(width, height) }
        viewport.update(width, height)
    }

    override fun render() {
        BGM.update()
        SE.update()

        if (Config.allowFullScreen && VK.FULL_SCREEN.justPressed()) {
            if (Gdx.graphics.isFullscreen) {
                Gdx.graphics.setWindowedMode(options.windowWidth, options.windowHeight)
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
            }
        }

        fpsCounter.addValue(Gdx.graphics.deltaTime)

        autoSaveCounter += Gdx.graphics.deltaTime
        if (autoSaveCounter >= 60) {
            saveGameData()
            autoSaveCounter = 0f
        }

        clearScreen(0f, 0f, 0f, 1f)
        var flag = false
        screens.safeValues().filter { it.state == ScreenState.FADING_IN }.forEach {
            flag = true
            it.render(safeDeltaTime())
        }
        screens.safeValues().filter { it.state == ScreenState.SHOWN }.forEach {
            flag = true
            it.render(safeDeltaTime())
        }
        screens.safeValues().filter { it.state == ScreenState.FADING_OUT }.forEach {
            flag = true
            it.render(safeDeltaTime())
        }
        if (!flag) {
            exitApp()
        }
        st.act(safeDeltaTime())
        st.draw()
    }

    override fun pause() {
        screens.safeValues().filter { it.state.isRendered() }.forEach { it.pause() }
    }

    override fun resume() {
        screens.safeValues().filter { it.state.isRendered() }.forEach { it.resume() }
    }

    override fun dispose() {
        saveGameData()
        batch.dispose()
        BGM.dispose()
        disposeA()
        screens.safeValues().forEach { it.dispose() }
        batch.shader.dispose()
    }

    fun setScreen(name: String?) {
        val scr: KotoScreen? = screens[name]
        screens.safeValues().filter { it.state.isRendered() }.forEach {
            it.hide()
            it.state = ScreenState.HIDDEN
        }
        if (scr != null) {
            logger.info("Switching to screen $name")
            scr.resize(Gdx.graphics.width, Gdx.graphics.height)
            scr.show()
            scr.state = ScreenState.SHOWN
        } else {
            logger.info("Switching to no screen")
        }
    }

    fun setScreen(name: String?, duration: Float) {
        val scr: KotoScreen? = screens[name]
        var oldScreen: KotoScreen? = null
        screens.safeValues().filter { it.state.isRendered() }.forEach {
            it.fadeOut(scr, duration)
            oldScreen = it
        }
        if (scr != null) {
            logger.info("Switching to screen \"$name\" with fading time $duration")
            scr.resize(Gdx.graphics.width, Gdx.graphics.height)
            scr.fadeIn(oldScreen, duration)
            scr.state = ScreenState.SHOWN
        } else {
            logger.info("Switching to no screen")
        }
    }
}

interface KotoCallbacks {
    fun restartCallback(restart: Boolean)
    fun loadOptions(): Options?
    fun saveOptions(options: Options)
    fun loadGameData(): GameData?
    fun saveGameData(gameData: GameData)
}
