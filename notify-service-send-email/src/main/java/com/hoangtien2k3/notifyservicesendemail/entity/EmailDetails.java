package com.hoangtien2k3.notifyservicesendemail.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
}
