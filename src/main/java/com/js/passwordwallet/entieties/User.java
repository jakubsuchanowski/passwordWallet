package com.js.passwordwallet.entieties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String login;

    @NonNull
    private String passwordHash;

    private String salt;

    private Boolean isPasswordKeptAsHash;

    private String auxiliaryQuestion;

    private String answer;

}
