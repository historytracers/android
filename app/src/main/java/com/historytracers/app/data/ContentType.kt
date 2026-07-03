package com.historytracers.app.data

enum class ContentType(val value: String) {
    INDEX("index"),
    CLASS("class"),
    ATLAS("atlas"),
    FAMILY_TREE("family_tree"),
    SM_GAME("sm_game"),
    UNKNOWN("");

    companion object {
        fun from(value: String?): ContentType =
            entries.firstOrNull { it.value == value } ?: UNKNOWN
    }
}
