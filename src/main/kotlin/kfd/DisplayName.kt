package org.example.kfd

fun displayName(name: String?): String {
    if (name.isNullOrBlank()) {
        return "Гость"
    }
    return name.trim()
}

fun main(){
    println(displayName("j "))
}



