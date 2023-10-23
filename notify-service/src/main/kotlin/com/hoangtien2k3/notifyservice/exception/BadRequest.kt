package com.hoangtien2k3.notifyservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class BadRequest : RuntimeException {
    constructor(paramName: String?, value: String?) : super(String.format(INVALID_PARAM_MSG, value, paramName))
    constructor(paramName: String?, value: String?, format: String?) : super(
        String.format(
            INVALID_DATE_PARAM_MSG,
            value,
            paramName,
            format
        )
    )

    companion object {
        private const val serialVersionUID = 1L
        private const val INVALID_PARAM_MSG = "Invalid value [%s] found for paramater [%s]."
        private const val INVALID_DATE_PARAM_MSG = "Invalid value [%s] found for paramater [%s]. Expected date format is [%s]"
    }
}