package com.hoangtien2k3.notifyservice.service.channel

import com.hoangtien2k3.notifyservice.entity.ChannelType
import com.hoangtien2k3.notifyservice.entity.Message


interface Channel {
    fun notify(msg: Message?) {
        throw RuntimeException("Notify method is not implemented yet.")
    }

    fun supports(type: ChannelType?): Boolean {
        throw RuntimeException("Notify method is not implemented yet.")
    }
}