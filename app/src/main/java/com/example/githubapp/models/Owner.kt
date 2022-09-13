package com.example.githubapp.models

import io.realm.RealmObject

open class Owner : RealmObject() {
    var id = 0
    var login: String? = null
}