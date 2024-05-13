package com.example.dbmigration.dto;

public class ReadMemberReturnDto {
    private Long id;
    private String teamName;
    private String username;
    private int age;

    public ReadMemberReturnDto(Long id, String teamName, String username, int age) {
        this.id = id;
        this.teamName = teamName;
        this.username = username;
        this.age = age;
    }

    @Override
    public String toString() {
        return "ReadMemberReturnDto{" +
                "id=" + id +
                ", teamName='" + teamName + '\'' +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
