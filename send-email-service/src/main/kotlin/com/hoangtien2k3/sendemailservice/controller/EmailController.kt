package com.hoangtien2k3.sendemailservice.controller

import com.hoangtien2k3.sendemailservice.entity.EmailDetails
import com.hoangtien2k3.sendemailservice.service.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/email")
class EmailController {
    @Autowired
    private val emailService: EmailService? = null

    @PostMapping("/sendMail")
    fun sendMail(@RequestBody details: EmailDetails?): String? {
        return emailService?.sendSimpleMail(details)
    }

    @PostMapping("/sendMailWithAttachment")
    fun sendMailWithAttachment(@RequestBody details: EmailDetails?): String? {
        return emailService?.sendMailWithAttachment(details)
    }

    @PostMapping("/send")
    fun sendMail(
        @RequestParam(value = "file", required = false) file: Array<MultipartFile?>?,
        to: String?,
        cc: Array<String?>?,
        subject: String?,
        body: String?
    ): String? {
        return emailService?.sendMail(file, to, cc, subject, body)
    }
}