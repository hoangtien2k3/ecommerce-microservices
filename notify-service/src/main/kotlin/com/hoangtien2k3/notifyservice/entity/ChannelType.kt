package com.hoangtien2k3.notifyservice.entity

import java.util.*

enum class ChannelType {
    LACK, EMAIL;

    override fun toString(): String {
        return name.lowercase(Locale.getDefault())
    }
}
