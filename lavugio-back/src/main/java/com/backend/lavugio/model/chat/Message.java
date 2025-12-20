package com.backend.lavugio.model.chat;

import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.model.user.AccountType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account chatOwner;

    @Column(nullable = false)
    private LocalDate messageDate;
    @Column(nullable = false)
    private LocalTime messageTime;

    @Column(nullable = false)
    private String text;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountType senderType;
}
