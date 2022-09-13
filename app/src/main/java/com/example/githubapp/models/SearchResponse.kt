package com.example.githubapp.models

import io.realm.RealmObject
import com.google.gson.annotations.SerializedName
import io.realm.RealmList

open class SearchResponse : RealmObject() {
    @SerializedName("total_count")
    var totalCount = 0

    var items: RealmList<Repository>? = null

}