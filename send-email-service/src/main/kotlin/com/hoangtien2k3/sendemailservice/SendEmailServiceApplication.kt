package com.hoangtien2k3.sendemailservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SendEmailServiceApplication

fun main(args: Array<String>) {
	runApplication<SendEmailServiceApplication>(*args)
}
