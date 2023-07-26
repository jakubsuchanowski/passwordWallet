package com.js.passwordwallet.entieties;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_login")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class FailedLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @NonNull
    private User user;
    @NonNull
    private int failedLoginNum;
    @NonNull
    private LocalDateTime lastFailedLogin;
    @NonNull
    private boolean pernamentBlock;
    private String ipAddress;
    private LocalDateTime blockTime;
}

