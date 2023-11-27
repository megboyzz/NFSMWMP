package com.megboyzz.devmenu.data.repositories

class MyClass {

    fun main(){

        val input  = readln().split(",").associate {
            val s = it.trim().split(" ")
            s[0] to s[1].toInt()
        }

        var sum = 0

        readln().split(" ").forEach {

            val s = input[it] ?: 0
            sum += s

        }

        println(sum)


    }

}