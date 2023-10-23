package com.hoangtien2k3.notifyservice.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Entity
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.data.mongodb.core.mapping.Document

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collation = "message")
class Message {
    private val id: Long = 0
    private val subject: String? = null
    private val from: String? = null
    private val to: String? = null
    private val body: String? = null
    private val sentTime: String? = null
}