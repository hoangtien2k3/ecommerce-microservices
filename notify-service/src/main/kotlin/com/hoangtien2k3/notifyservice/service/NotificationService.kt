package com.hoangtien2k3.notifyservice.service

import com.hoangtien2k3.notifyservice.entity.ChannelType
import com.hoangtien2k3.notifyservice.entity.Message
import com.hoangtien2k3.notifyservice.service.channel.ChannelFactory
import jdk.incubator.vector.VectorOperators
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger


@Service
class NotificationService {

    companion object {
        private val LOG: Logger = LogManager.getLogger(NotificationService::class.java)
    }

    @Autowired
    var factory: ChannelFactory? = null

    fun NotificationService(factory: ChannelFactory?) {
        this.factory = factory
    }

    private val notificationId = AtomicInteger(1)


    fun notifyAll(msg: Message): Long {
        for (c in factory!!.getChannels()!!) {
            msg.id = notificationId.getAndIncrement().toLong()
            c.notify(msg)
            LOG.debug("ID = $notificationId, Message sent = $msg")
        }
        return notificationId.toLong()
    }

    fun notify(channelType: ChannelType?, msg: Message): Long {
        msg.id = notificationId.getAndIncrement().toLong()
        factory!![channelType!!].notify(msg)
        LOG.debug("ID = $notificationId, Message sent = $msg")
        return notificationId.toLong()
    }


}