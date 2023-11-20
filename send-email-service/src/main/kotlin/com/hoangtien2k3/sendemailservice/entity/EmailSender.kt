package com.hoangtien2k3.sendemailservice.entity

import org.springframework.web.multipart.MultipartFile

//class EmailSender {
//    lateinit var file: Array<MultipartFile>
//    var to: String? = null
//    lateinit var cc: Array<String>
//    var subject: String? = null
//    var body: String? = null
//}

class EmailSender(
    val file: Array<MultipartFile>,
    val to: String?,
    val cc: Array<String>,
    val subject: String?,
    val body: String?
)