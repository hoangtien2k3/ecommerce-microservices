package com.hoangtien2k3.notifyservice.repository

import com.hoangtien2k3.notifyservice.dto.NotifyDto
import com.hoangtien2k3.notifyservice.extension.checkNotNull
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import org.springframework.beans.factory.annotation.Autowired

class NotifyRepository constructor(
    @Autowired
    private val emailValidator: EmailValidator
) {
    
    fun abd() {
//        emailValidator.isValid()
    }
    val notifyDto1 = lazy (LazyThreadSafetyMode.SYNCHRONIZED){ NotifyDto(id =100) }

    private var notifyDto2: NotifyDto? = null

    val notifyDto = NotifyDto(
        id = 1000
    ).checkNotNull(100)
}