package com.hhs.koto.util

import com.badlogic.gdx.math.Vector2

class Polyline(val maxLength: Float) {

    companion object{
        const val THRESHOLD=5f
    }

    data class NodeSnapshot(val x: Float, val y: Float, val rot: Float){
        fun dst(other: NodeSnapshot): Float {
            return Vector2.dst(x,y,other.x,other.y)
        }
    }

    val dq=ArrayDeque<NodeSnapshot>()
    var totalLength = 0f

    fun add(now: NodeSnapshot){
        if(dq.isEmpty()){
            dq.add(now)
            return
        }

        if(now==dq.first()) return

        totalLength+=dq.first().dst(now)
        dq.addFirst(now)

        while(totalLength>maxLength+THRESHOLD){
            val last=dq.removeLast()
            val newLast=dq.last()
            val dist=last.dst(newLast)
            totalLength-=dist
        }
    }

    fun get(distance: Float): NodeSnapshot{
        if(distance>=totalLength) return dq.last()
        var curDis=0f
        for(i in 0 until dq.size-1){
            val a=dq[i]
            val b=dq[i+1]
            val dist=a.dst(b)
            if(curDis+dist>=distance){
                val ratio=(distance-curDis)/dist
                return NodeSnapshot(
                    x=a.x+(b.x-a.x)*ratio,
                    y=a.y+(b.y-a.y)*ratio,
                    rot=a.rot+(b.rot-a.rot)*ratio
                )
            }
            curDis+=dist
        }

        error("Should not reach here")
    }
}