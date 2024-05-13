package com.example.dbmigration.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString(of = {"id", "username", "age", "teamId"})
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private long id;
    private String username;
    private int age;
    private int teamId;

    public Member(String username, int age, int teamId) {
        this.username = username;
        this.age = age;
        this.teamId = teamId;
    }
}
