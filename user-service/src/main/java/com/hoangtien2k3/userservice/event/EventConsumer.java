package com.hoangtien2k3.userservice.event;

import com.google.gson.Gson;
//import com.hoangtien2k3.model.ProfileDTO;
//import com.hoangtien2k3.service.ProfileService;
//import com.hoangtien2k3.utils.Constants;
import com.hoangtien2k3.userservice.constant.KafkaConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;

@Service
@Slf4j
public class EventConsumer {
//    Gson gson = new Gson();
//    @Autowired
//    ProfileService profileService;
//
//    public EventConsumer(ReceiverOptions<String, String> receiverOptions) {
//        KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(KafkaConstant.PROFILE_ONBOARDED_TOPIC)))
//                .receive()
//                .subscribe(this::profileOnboarded);
//    }
//
//    public void profileOnboarded(ReceiverRecord<String, String> receiverRecord) {
//        log.info("Profile Onboarded event");
//        ProfileDTO dto = gson.fromJson(receiverRecord.value(), ProfileDTO.class);
//        profileService.updateStatusProfile(dto).subscribe();
//    }
}