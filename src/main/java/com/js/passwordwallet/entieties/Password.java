package com.js.passwordwallet.entieties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "password")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Password{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String password;
    @NonNull
    @ManyToOne
    private User user;
    @NonNull
    private String webAddress;

    private String description;
    @NonNull
    private String login;

}