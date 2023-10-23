package com.hoangtien2k3.notifyservice.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Document(collection = "notify")
class Notify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null
    private var message: String? = null
    private var date: Date? = null

}