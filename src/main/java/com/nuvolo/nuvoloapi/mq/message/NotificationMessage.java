package com.nuvolo.nuvoloapi.mq.message;

import lombok.Data;


@Data
public class NotificationMessage {

    private String email;

    private String firstName;

    private String lastName;

    private Boolean success;

    private String message;

}
