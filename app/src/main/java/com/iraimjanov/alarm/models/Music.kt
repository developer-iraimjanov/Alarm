package com.iraimjanov.alarm.models

import java.io.Serializable

data class Music(
    var id: Long = 0L,
    var title: String = "",
    var imagePath: String = "",
    var musicPath: String = "",
    var author: String = "",
) : Serializable