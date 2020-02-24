package com.wessup.daily.user.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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
    @NotEmpty
    private String username;

    @Column(name="email", length=512)
    @NotEmpty
    private String email;

    @Column(name="access_token", length=64, nullable = false)
    @NotEmpty
    private String token;

    @Column(name="created_time")
    @CreationTimestamp
    private LocalDateTime createdTime;

    @Column(name="expiration_date", nullable = true)
    private LocalDateTime expired;

    @Builder
    public User(String username, String email, String token, Long userId, String nodeId) {
        this.username = username;
        this.email = email;
        this.token = token;
        this.userId = userId;
        this.nodeId = nodeId;
    }
}
