package com.example.foodiefling.ui

class ChatUser  {
    var name: String? = null
    var email: String? = null
    var uid: String? = null

    // Firebase needs an empty constructor to work properly
    constructor(){}
    constructor(name: String, email: String, uid: String){
        this.name = name
        this.email = email
        this.uid = uid
    }
}