# Koto

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/7afd1def08274d0eb292fb779d4d7125)](https://www.codacy.com/gh/HellHoleStudios/koto/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HellHoleStudios/koto&amp;utm_campaign=Badge_Grade)

> Koto is some random garbage th clone
>
> --Koto main developer, Zzzyt


Koto (KOtlin TOuhou Engine) is a Kotlin Touhou-style bullet hell game engine/template using Libgdx.
The goal of the project is to provide a simple and easy-to-use framework for creating Touhou-style games in Kotlin.

## Why Koto?
There are many Touhou-style game engines and other general game engines out there. 
It is easy to prototype a game using Unity, Godot or Danmakufu. 
However, we would like to prove that Kotlin is also a great choice for Touhou game development.
And here is Koto, built by Kotlin lovers, for Kotlin lovers.

Koto is:
- **Performant**: 10k+ bullets at 60fps by utilizing low-level rendering and efficient data structures.
- **Intuitive**: Instead of giving developers a `onFrame` method called every frame, Koto uses coroutines and Kotlin DSLs to create intuitive sequential patterns.
- **Kotlin-style**: Koto makes use of many Kotlin features such as extension functions, higher-order functions, and DSLs to provide quicker development experience.
- **Out-of-the-box**: Koto provides many commonly-used built-in features (see below) and tons of helper functions. Less boilerplate, more creativity.
- **Authentic**: Koto aims to replicate every detail of Touhou games, from effects to scoring systems.

Here is an example of how you write a spell card in Koto:
```kotlin
override fun spell(): Task = CoroutineTask {
    val boss = getBoss()

    //Create spellcard cutin effect
    game.stage.addDrawable(Cutin(getRegion("portrait/aya/attack.png")))
    repeat(20) {
        wander(boss, 120) //wander for 120 frames. Touhou bosses love to do this.
        cast(boss.x, boss.y) //cast effect
        wait(90)

        cast2(boss.x, boss.y) //another cast effect
        wait(30)

        boss.usingAction = true //use action animation instead of idle
        repeat(3) {
            ring( //creating patterns is simple!
                "DS_BALL_M_A_BLUE", //asset name
                boss.x,
                boss.y,
                50f, //radius
                difficultySelect(8, 12, 16, 20), //number of bullets, easily scaled by difficulty
                startAngle = random(0f, 360f), //angle
                speed = 5f, //speed
            )
            wait(20)
        }
        boss.usingAction = false
        wait(30)
    }
}
```
## Features
- UI
  - Grid System for faster UI positioning
  - Screen management for smooth transitions
  - Built-in containers
  - Smart HD font rendering
  - HSV support
- STG
  - Points, grazing, lives, bombs, power, etc.
  - Complete replay system
  - History data/scoreboard system
  - Laser and curvy laser support
  - Complete boss and spell card support (including card history)
  - Background and parallax support
  - Pause system
  - Dialog system
  - Default shotsheet for prototyping
  - All kinds of animation (spell declaration, boss cast, health circle, etc.)
- High-level System
  - Stage/Spell practise
  - Extra stage
  - Music room
  - Options
  - I18N