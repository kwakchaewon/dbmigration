package com.example.dbmigration.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString(of = {"id", "name"})
@NoArgsConstructor
public class Team {
    @Id @GeneratedValue
    private int id;
    private String name;

    public Team(String name) {
        this.name = name;
    }
}
