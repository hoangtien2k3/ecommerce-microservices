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
@Document(collation = "message")
data class Message constructor(
    val id: Long = 0,
    val subject: String,
    val from: String,
    val to: String,
    val body: String,
    val sentTime: String
) {
    val checkId : Boolean get() = id > 10
    
}
