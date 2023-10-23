package com.hoangtien2k3.notifyservice.entity

enum class ChannelType {
    LACK, EMAIL;

    override fun toString(): String {
        return name.toLowerCase()
    }
}
