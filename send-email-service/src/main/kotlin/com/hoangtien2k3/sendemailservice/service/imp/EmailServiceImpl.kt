package com.hoangtien2k3.sendemailservice.service.imp

import com.hoangtien2k3.sendemailservice.entity.EmailDetails
import com.hoangtien2k3.sendemailservice.service.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.mail.MessagingException

@Service
class EmailServiceImpl(
    @Autowired private val javaMailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val fromEmail: String
) : EmailService {

    override fun sendSimpleMail(details: EmailDetails?): String? {
        return try {
            val mailMessage = SimpleMailMessage()
            mailMessage.from = fromEmail
            mailMessage.setTo(details?.recipient)
            mailMessage.text = details?.msgBody
            mailMessage.subject = details?.subject

            javaMailSender.send(mailMessage)
            "Mail Sent Successfully"
        } catch (e: Exception) {
            "Error while Sending Mail"
        }
    }

    override fun sendMailWithAttachment(details: EmailDetails?): String? {
        return try {
            val mimeMessage = javaMailSender.createMimeMessage()
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, true)

            mimeMessageHelper.setFrom(fromEmail)
            if (details != null) {
                mimeMessageHelper.setTo(details.recipient)
                mimeMessageHelper.setText(details.msgBody)
                mimeMessageHelper.setSubject(details.subject)
                val file = FileSystemResource(File(details.attachment))
                mimeMessageHelper.addAttachment(file.filename, file)
            }

            javaMailSender.send(mimeMessage)
            "Mail sent Successfully"
        } catch (e: MessagingException) {
            "Error while sending mail!!!"
        }
    }

    override fun sendMail(
        file: Array<MultipartFile?>?,
        to: String?,
        cc: Array<String?>?,
        subject: String?,
        body: String?
    ): String? {
        return try {
            val mimeMessage = javaMailSender.createMimeMessage()
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, true)

            mimeMessageHelper.setFrom(fromEmail)
            if (to != null) {
                mimeMessageHelper.setTo(to)
            }
            if (cc != null) {
                mimeMessageHelper.setCc(cc)
            }
            if (subject != null) {
                mimeMessageHelper.setSubject(subject)
            }
            if (body != null) {
                mimeMessageHelper.setText(body)
            }

            for (multipartFile in file!!) {
                if (multipartFile != null) {
                    mimeMessageHelper.addAttachment(
                        multipartFile.getOriginalFilename().toString(),
                        ByteArrayResource(multipartFile.getBytes())
                    )
                }
            }

            javaMailSender.send(mimeMessage)
            "Email send success to $cc"
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}
