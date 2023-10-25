package com.hoangtien2k3.notifyservice.service.channel

import com.hoangtien2k3.notifyservice.entity.ChannelType
import com.hoangtien2k3.notifyservice.entity.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChannelFactory {

//    @Autowired
    private val channelList = mutableListOf<Channel>()

    operator fun get(c: ChannelType): Channel {
        return channelList.firstOrNull { service -> service.supports(c) } ?: throw RuntimeException("No channel found with type: $c")
    }

    fun notifyAll(msg: Message?) {
        channelList.forEach { c -> c.notify(msg) }
    }

    fun getChannels(): List<Channel>? {
        return channelList
    }
}
