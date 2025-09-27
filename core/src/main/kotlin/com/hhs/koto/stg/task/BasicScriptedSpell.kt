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

package com.hhs.koto.stg.task

import com.badlogic.gdx.Files
import com.hhs.koto.scripting.SpellScriptConfig
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.graphics.*
import com.hhs.koto.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.assets.file
import ktx.collections.GdxArray
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.valueOrNull
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

/**
 * A basic spellcard that uses [Kotlin Scripting](https://kotlinlang.org/docs/custom-script-deps-tutorial.html) to define its behavior.
 *
 * The script will be compiled at runtime, therefore hot-reloading is supported.
 * In other words, you can edit the spell while the game is running.
 *
 * You should name your script file with extension `.spell.kts` so that Idea can perform syntax highlighting and autocompleting.
 *
 * Do NOT place the script in `assets/` folder.
 * Otherwise, it will not be reloaded when changed as assets are pre-packaged into the destination binary.
 *
 * Your script should return a [Task] and will be executed under the context of a [BasicSpell].
 * For more information, please check the example code at `script/test.spell.kts`
 *
 * Compilation at runtime causes a lag about 0.5-2 seconds.
 * Also, the script will not be secure when finally packaged.
 * For these reasons, this class is NOT recommended for production use.
 *
 * This feature is always **EXPERIMENTAL** due to the experimental nature of Kotlin Scripting.
 * @author XiaoGeNintendo
 */
abstract class BasicScriptedSpell<T : Boss>(bossClass: Class<T>,
                                            override val name:String,
                                            val scriptName: String,
                                            override val maxTime: Int,
                                            override val availableDifficulties: GdxArray<GameDifficulty> = GameDifficulty.REGULAR_AVAILABLE,
                                            ) : BasicSpell<T>(bossClass) {


    companion object{
        val scriptingHost = BasicJvmScriptingHost()
    }

    private var cachedTask:Task? = null
    private var cachedHash:Int = 0

    init{
        app.logger.info("Started background compiling of $scriptName")
        GlobalScope.launch{
            while(true){
                val newHash=file(scriptName, Files.FileType.Local).readString().hashCode()
                if(newHash!=cachedHash) {
                    app.logger.debug("Recompiling $scriptName")
                    cachedTask = compileScript()
                    cachedHash = newHash
                }
                delay(2000)
            }
        }
    }

    fun compileScript():Task{
        val scriptSource = file(scriptName, Files.FileType.Local).file().toScriptSource()
        val result = scriptingHost.eval(
            scriptSource,
            SpellScriptConfig,
            ScriptEvaluationConfiguration {
                implicitReceivers(this@BasicScriptedSpell)
            }
        )

        when (result) {
            is ResultWithDiagnostics.Success -> {
                val returnVal = result.valueOrNull()?.returnValue
                app.logger.info("Script returned: $returnVal")
                return (result.value.returnValue as ResultValue.Value).value as Task
            }
            else -> {
                app.logger.error("Script evaluation failed:")
                result.reports.forEach {
                    app.logger.error("${it.severity}: ${it.message} at ${it.location}")
                }

                return EmptyTask()
            }
        }
    }

    override fun spell(): Task = cachedTask ?: compileScript()

}