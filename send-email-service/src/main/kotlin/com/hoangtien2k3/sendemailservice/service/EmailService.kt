package com.hoangtien2k3.sendemailservice.service

import com.hoangtien2k3.sendemailservice.entity.EmailDetails
import org.springframework.web.multipart.MultipartFile

interface EmailService {
    fun sendSimpleMail(details: EmailDetails?): String?

    fun sendMailWithAttachment(details: EmailDetails?): String?

    fun sendMail(
        file: Array<MultipartFile?>?,
        to: String?,
        cc: Array<String?>?,
        subject: String?,
        body: String?
    ): String?

}