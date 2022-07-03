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

import com.hhs.koto.util.safeMod

data class FragmentCounter(
    var fragmentFactor: Int = 3,
    var completedCount: Int = 0,
    var fragmentCount: Int = 0,
    var limit: Int = 8,
) {
    fun set(other: FragmentCounter) {
        fragmentFactor = other.fragmentFactor
        completedCount = other.completedCount
        fragmentCount = other.fragmentCount
    }

    fun update() {
        val remainder = safeMod(fragmentCount, fragmentFactor)
        val quotient = (fragmentCount - remainder) / fragmentFactor
        completedCount += quotient
        fragmentCount = remainder
        if (completedCount >= limit) {
            completedCount = limit
            fragmentCount = 0
        }
    }

    fun clear() {
        completedCount = 0
        fragmentCount = 0
    }

    fun add(completedCount: Int, fragmentCount: Int) {
        this.completedCount += completedCount
        this.fragmentCount += fragmentCount
        update()
    }

    fun addFragment(count: Int) {
        fragmentCount += count
        update()
    }

    fun addCompleted(count: Int) {
        completedCount += count
        update()
    }

    fun removeFragment(count: Int) {
        fragmentCount -= count
        update()
    }

    fun removeCompleted(count: Int) {
        completedCount -= count
        update()
    }
}