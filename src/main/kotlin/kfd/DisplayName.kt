package org.example.kfd

fun displayName(name: String?): String = (name ?: "Guest").trim()

fun main(){
    println(displayName("j "))
}



