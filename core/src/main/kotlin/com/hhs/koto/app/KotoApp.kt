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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.hhs.koto.app.screen.BasicScreen
import com.hhs.koto.util.*
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync

class KotoApp : KtxGame<KtxScreen>() {

    lateinit var batch: SpriteBatch
    lateinit var viewport: Viewport
    var input = InputMultiplexer()
    var blocker = InputBlocker()
    val logger = Logger("Main", Config.logLevel)

    override fun create() {
        loadOptions()

        koto = this

        Gdx.app.logLevel = Config.logLevel

        logger.info("Game start.")

        Gdx.graphics.setWindowedMode(options.startupWindowWidth, options.startupWindowHeight)
        Gdx.graphics.setVSync(options.vsyncEnabled)

        batch = SpriteBatch()
        viewport = ScalingViewport(Config.windowScaling, Config.screenWidth, Config.screenHeight)
        input.addProcessor(blocker)
        Gdx.input.inputProcessor = input

        KtxAsync.initiate()

        loadAssetIndex(Gdx.files.internal(".assets.json"))
        A.finishLoading()

        SE.register("cancel", "snd/se_cancel00.wav")
        SE.register("invalid", "snd/se_invalid.wav")
        SE.register("ok", "snd/se_ok00.wav")
        SE.register("select", "snd/se_select00.wav")
        SE.register("pldead", "snd/se_pldead00.wav")
        SE.register("item", "snd/se_item00.wav")
        SE.register("graze", "snd/se_graze.wav")
        SE.register("shoot", "snd/se_plst00.wav")

        BGM.register(LoopingMusic("mus/E.0120.ogg", 2f, 58f))

        B.setSheet(Config.defaultShotSheet);

        addScreen(BasicScreen("mus/E.0120.ogg", getRegion("bg/title.png")))
        setScreen<BasicScreen>()

        super.create()
    }

    override fun render() {
        A.update()
        BGM.update()
        clearScreen(0f, 0f, 0f, 1f)
        currentScreen.render(safeDeltaTime())
    }

    override fun dispose() {
        batch.dispose()
        BGM.dispose()
    }

    override fun <Type : KtxScreen> setScreen(type: Class<Type>) {
        logger.debug("Set screen to: $type")
        super.setScreen(type)
    }
}