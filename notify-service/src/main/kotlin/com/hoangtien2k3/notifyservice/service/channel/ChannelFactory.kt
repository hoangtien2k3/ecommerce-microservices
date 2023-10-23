package com.hoangtien2k3.notifyservice.service.channel

import com.hoangtien2k3.notifyservice.entity.ChannelType
import com.hoangtien2k3.notifyservice.entity.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChannelFactory {

    @Autowired
    private var channelList: List<Channel>? = null

    operator fun get(c: ChannelType): Channel {
        return channelList
            ?.filter { service -> service.supports(c) }
            ?.firstOrNull() ?: throw RuntimeException("No channel found with type: $c")
    }

    fun notifyAll(msg: Message?) {
        channelList?.forEach { c -> c.notify(msg) }
    }

    fun getChannels(): List<Channel>? {
        return channelList
    }
}
