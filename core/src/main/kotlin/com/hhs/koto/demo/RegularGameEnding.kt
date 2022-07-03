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

package com.hhs.koto.demo

import com.hhs.koto.stg.task.RunnableTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.stg.task.TaskBuilder
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.app

object RegularGameEnding : TaskBuilder {
    override fun build(): Task = RunnableTask {
        SystemFlag.ending = if (SystemFlag.shottype!!.startsWith("marisa")) {
            "ending2"
        } else {
            "ending1"
        }
        SystemFlag.redirect = "ending"
        SystemFlag.redirectDuration = 3f
        app.setScreen("blank", 3f)
    }
}