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

package com.hhs.koto.app

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
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
import com.hhs.koto.demo.RegularGameEnding
import com.hhs.koto.demo.player.MarisaPlayer
import com.hhs.koto.demo.player.ReimuPlayer
import com.hhs.koto.demo.stage1.Stage1
import com.hhs.koto.demo.stage1.Stage1Spell1
import com.hhs.koto.demo.stage2.Stage2
import com.hhs.koto.demo.stage2.Stage2Spell1
import com.hhs.koto.demo.stage2.Stage2Spell2
import com.hhs.koto.demo.stage2.Stage2Spell3
import com.hhs.koto.demo.stage_extra.StageExtra
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.stg.GameData
import com.hhs.koto.stg.Replay
import com.hhs.koto.util.*
import ktx.actors.plusAssign
import ktx.app.clearScreen
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.set
import java.util.*

class KotoApp(
    val fileSystem: NativeFileSystem,
    val graphicsSystem: NativeGraphicsSystem,
) : ApplicationListener {
    lateinit var batch: SpriteBatch
    lateinit var normalBatch: SpriteBatch
    lateinit var viewport: Viewport
    private lateinit var st: Stage
    private lateinit var fps: FPSDisplay
    lateinit var fpsCounter: WindowedMean

    val screens = GdxMap<String, KotoScreen>()
    var input = EmulatedInput()
    val logger = Logger("Main", Config.logLevel)
    var autoSaveCounter: Float = 0f
    var currentScreen: KotoScreen? = null

    override fun create() {
        app = this

        loadOptions()

        Locale.setDefault(Locale.ROOT)
        bundle = I18NBundle.createBundle(Gdx.files.internal("locale/locale"), options.locale, "UTF-8")
        Config.UIFont = bundle["font.ui"]
        Config.UIFontSmall = bundle["font.uiSmall"]

        when (options.displayMode) {
            "fullscreen" -> {
                val displayModes = GdxArray<Graphics.DisplayMode>()
                Gdx.graphics.displayModes.forEach {
                    for ((i, mode) in displayModes.withIndex()) {
                        if (it.width == mode.width && it.height == mode.height) {
                            if (it.refreshRate > mode.refreshRate) {
                                displayModes[i] = it
                            }
                            return@forEach
                        }
                    }
                    displayModes.add(it)
                }

                var selectedDisplayMode = 0
                for ((i, displayMode) in displayModes.withIndex()) {
                    if (displayMode.height <= options.windowHeight) {
                        selectedDisplayMode = i
                    }
                }
                Gdx.graphics.setFullscreenMode(displayModes[selectedDisplayMode])
            }
            "borderless" -> {
                if (graphicsSystem.borderlessAvailable) {
                    graphicsSystem.setBorderless()
                } else {
                    throw KotoRuntimeException("Borderless mode not available")
                }
            }
            "windowed" -> {
                Gdx.graphics.setUndecorated(false)
                Gdx.graphics.setWindowedMode(options.windowWidth, options.windowHeight)
            }
        }

        initAsset()

        loadAssetIndex(Gdx.files.internal(".assets.json"))
        A.finishLoading()

        Gdx.app.logLevel = Config.logLevel
        logger.info("Game start.")


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
        SE.register("bonus", "snd/se_bonus.wav")
        SE.register("extend", "snd/se_extend.wav")
        SE.register("charge","snd/se_ch02.wav")
        SE.register("charge2","snd/se_kira00.wav")

        BGM.register(LoopingMusic(bundle["music.0.file"], 2f, 58f))
        BGM.register(LoopingMusic(bundle["music.1.file"], 0f, 12f))
        BGM.register(LoopingMusic(bundle["music.2.file"]))
        BGM.register(LoopingMusic(bundle["music.3.file"]))
        defaultShotSheet = A[Config.defaultShotSheet]

        // TODO variants
        GameBuilder.shottypes.add("reimuA" to { ReimuPlayer() })
        GameBuilder.shottypes.add("reimuB" to { ReimuPlayer() })
        GameBuilder.shottypes.add("reimu" to { ReimuPlayer() })
        GameBuilder.shottypes.add("marisaA" to { MarisaPlayer() })
        GameBuilder.shottypes.add("marisaB" to { MarisaPlayer() })
        GameBuilder.shottypes.add("marisa" to { MarisaPlayer() })
        GameBuilder.shottypes.add("sakuyaA" to { ReimuPlayer() })
        GameBuilder.shottypes.add("sakuyaB" to { ReimuPlayer() })
        GameBuilder.shottypes.add("sakuya" to { ReimuPlayer() })
        GameBuilder.shottypes.add("youmuA" to { ReimuPlayer() })
        GameBuilder.shottypes.add("youmuB" to { ReimuPlayer() })
        GameBuilder.shottypes.add("youmu" to { ReimuPlayer() })
        GameBuilder.shottypes.add("sanaeA" to { ReimuPlayer() })
        GameBuilder.shottypes.add("sanaeB" to { ReimuPlayer() })
        GameBuilder.shottypes.add("sanae" to { ReimuPlayer() })

        GameBuilder.regularStages.add(Stage1)
        GameBuilder.regularStages.add(Stage2)
        GameBuilder.regularEnding = RegularGameEnding
        GameBuilder.extraStages.add(StageExtra)

        GameBuilder.spells.add(Stage1Spell1)
        GameBuilder.spells.add(Stage2Spell1)
        GameBuilder.spells.add(Stage2Spell2)
        GameBuilder.spells.add(Stage2Spell3)

        loadGameData()

        screens["blank"] = BlankScreen()
        screens["title"] = TitleScreen()
        screens["game"] = GameScreen()
        screens["difficultySelect"] = DifficultySelectScreen()
        screens["playerSelect"] = PlayerSelectScreen()
        screens["stageSelect"] = StageSelectScreen()
        screens["spellSelect"] = SpellSelectScreen()
        screens["replay"] = ReplayScreen()
        screens["playerData"] = PlayerDataScreen()
        screens["musicRoom"] = MusicRoomScreen()
        screens["options"] = OptionsScreen()
        screens["ending"] = EndingScreen()
        screens["credit"] = CreditScreen()
        setScreen("blank")
        setScreen("title", 1f)
    }

    override fun resize(width: Int, height: Int) {
        screens.safeValues().forEach { if (it.state.isRendered()) it.resize(width, height) }
        viewport.update(width, height)
    }

    override fun render() {
        BGM.update()
        SE.update()

        fpsCounter.addValue(Gdx.graphics.deltaTime)

        autoSaveCounter += Gdx.graphics.deltaTime
        if (autoSaveCounter >= 60) {
            saveGameData()
            autoSaveCounter = 0f
        }

        input.update(safeDeltaTime())

        clearScreen(0f, 0f, 0f, 1f)
        var flag = false
        screens.safeValues().forEach {
            if (it.state == ScreenState.FADING_IN) {
                flag = true
                it.render(safeDeltaTime())
            }
        }
        screens.safeValues().forEach {
            if (it.state == ScreenState.SHOWN) {
                flag = true
                it.render(safeDeltaTime())
            }
        }
        screens.safeValues().forEach {
            if (it.state == ScreenState.FADING_OUT) {
                flag = true
                it.render(safeDeltaTime())
            }
        }
        if (!flag) {
            exitApp()
        }
        st.act(safeDeltaTime())
        st.draw()
    }

    override fun pause() {
        screens.safeValues().forEach { if (it.state.isRendered()) it.pause() }
    }

    override fun resume() {
        screens.safeValues().forEach { if (it.state.isRendered()) it.resume() }
    }

    override fun dispose() {
        saveGameData()
        batch.dispose()
        BGM.dispose()
        disposeAsset()
        screens.safeValues().forEach { it.dispose() }
        batch.shader.dispose()
    }

    fun setScreen(name: String?) {
        val newScreen: KotoScreen? = screens[name]
        screens.safeValues().forEach {
            if (it.state.isRendered()) {
                it.hide()
                it.state = ScreenState.HIDDEN
            }
        }
        if (newScreen != null) {
            logger.info("Switching to screen $name")
            newScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
            newScreen.show()
            newScreen.state = ScreenState.SHOWN
        } else {
            logger.info("Switching to no screen")
        }
        currentScreen = newScreen
    }

    fun setScreen(name: String?, duration: Float) {
        val newScreen: KotoScreen? = screens[name]
        screens.safeValues().filter { it.state.isRendered() }.forEach {
            it.fadeOut(newScreen, duration)
        }
        if (newScreen != null) {
            logger.info("Switching to screen \"$name\" with fading time $duration")
            newScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
            newScreen.fadeIn(currentScreen, duration)
            newScreen.state = ScreenState.SHOWN
        } else {
            logger.info("Switching to no screen")
        }
        currentScreen = newScreen
    }
}

interface NativeFileSystem {
    fun loadOptions(): Options
    fun saveOptions(options: Options)
    fun loadGameData(): GameData?
    fun saveGameData(gameData: GameData)
    fun loadReplays(): GdxArray<Replay>
    fun saveReplay(replay: Replay)


}

interface NativeGraphicsSystem {
    val borderlessAvailable: Boolean
        get() = false

    fun setBorderless(): Pair<Int, Int>
}