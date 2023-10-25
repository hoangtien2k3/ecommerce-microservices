package com.hoangtien2k3.notifyservice

import com.hoangtien2k3.notifyservice.dto.NotifyDto
import com.hoangtien2k3.notifyservice.extension.checkNotNull
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NotifyServiceApplication

fun main(args: Array<String>) {
	runApplication<NotifyServiceApplication>(*args)
	val notifyDto = NotifyDto(
		id = 1000
	)

	val check = notifyDto.checkNotNull(100)

	println(check)
}
