package com.hoangtien2k3.notifyservice.service.channel

import com.hoangtien2k3.notifyservice.entity.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException


@Component
class SlackChannel : Channel {

    @Value("\${slack.access.token}")
    private val slackAccessToken: String? = null

    @Value("\${slack.channel.name}")
    private val slackChannel: String? = null

    override fun notify(msg: Message?) {
        var session: SlackSession? = null
        try {
            session = SlackSessionFactory.createWebSocketSlackSession(slackAccessToken)
            session.connect()
            val channel: SlackChannel = session.findChannelByName(slackChannel)
                ?: throw RuntimeException("Invalid Slack channel [$slackChannel] is specified.")
            session.sendMessage(channel, prepareMsg(msg))
        } catch (e: Exception) {
            throw RuntimeException("Failed to send message using slack channel, exception : " + e.message, e)
        } finally {
            try {
                if (session != null) session.disconnect()
            } catch (ignore: IOException) {
            }
        }
    }


}