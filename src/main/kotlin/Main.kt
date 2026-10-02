package org.example

data class User(val name: String, val id: Long, val profile: Profile?)
data class Profile(val city: String?)



fun main(){
    println(1.toString() + "Hello");
}