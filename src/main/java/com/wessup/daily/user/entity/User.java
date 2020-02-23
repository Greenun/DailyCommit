package com.wessup.daily.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name="user_tb")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="node_id", length=32)
    private String nodeId;

    @Column(name="username", length=256)
    private String username;

    @Column(name="email", length=512)
    private String email;

    @Column(name="access_token", length=64, nullable = false)
    private String token;

    @Column(name="created_time")
    @CreationTimestamp
    private LocalDateTime createdTime;

    @Column(name="expiration_date", nullable = true)
    private LocalDateTime expired;
}
