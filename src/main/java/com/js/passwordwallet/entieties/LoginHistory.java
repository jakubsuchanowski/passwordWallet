package com.js.passwordwallet.entieties;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @ManyToOne
    private User user;
    @NonNull
    private LocalDateTime lastLoginDate;
    @NonNull
    private Boolean loginResult;
    @NonNull
    private String ipAddress;
}

