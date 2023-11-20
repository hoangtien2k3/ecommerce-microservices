package com.hoangtien2k3.sendemailservice.entity

//class EmailDetails {
//    private val recipient: String? = null
//    private val msgBody: String? = null
//    private val subject: String? = null
//    private val attachment: String? = null
//}

class EmailDetails(
    val recipient: String,
    val msgBody: String,
    val subject: String,
    val attachment: String
)