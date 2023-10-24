package com.hoangtien2k3.notifyservice.service.channel

import com.hoangtien2k3.notifyservice.entity.ChannelType
import com.hoangtien2k3.notifyservice.entity.Message
import jakarta.validation.constraints.Email
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class EmailChannel : Channel{

    @Autowired
    private lateinit var emailValidator: EmailValidator

//    @Autowired
//    var emailService: EmailService? = null

    @Value("\${spring.mail.username}")
    var fromEmail: String? = null

    fun notify(msg: Message) {

        println(emailValidator.hashCode())

//        if (this::emailValidator.isInitialized && emailValidator.isValid("abc")) {
//
//        }
//        emailValidator.isValid("")
//        if (!emailValidator.isValid(msg.toString())) {
//            throw RuntimeException("Invalid email format in - from address")
//        }
//        if (!emailValidator.isValid(msg.getTo())) {
//            throw RuntimeException("Invalid email format in - to address")
//        }
//        try {
//            val email: Email = DefaultEmail.builder()
//                .from(InternetAddress(fromEmail, "NotificationService"))
//                .to(
//                    Lists.newArrayList(
//                        InternetAddress(
//                            msg.getTo(), ""
//                        )
//                    )
//                )
//                .subject(msg.getSubject())
//                .body(msg.getBody())
//                .encoding("UTF-8").build()
//            emailService.send(email)
//        } catch (e: Exception) {
//            throw RuntimeException("Failed to send message using email channel, exception : " + e.message, e)
//        }
    }

}

//private fun EmailValidator.isValid(email: String): Boolean {
//
//}
