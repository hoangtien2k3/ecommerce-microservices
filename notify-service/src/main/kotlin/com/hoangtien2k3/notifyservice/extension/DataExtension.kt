package com.hoangtien2k3.notifyservice.extension

import com.hoangtien2k3.notifyservice.dto.NotifyDto

fun NotifyDto.checkNotNull(value: Int): Boolean {
    return id > value
}