package com.bank.notification.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationEvent {
    private String id;
    private String name;
    private String email;
}
