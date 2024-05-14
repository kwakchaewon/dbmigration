package com.example.dbmigration.dto;

public class ReadMemberAvgAgeDto {
    private String teamName;
    private double age;

    public ReadMemberAvgAgeDto(String teamName, double age) {
        this.teamName = teamName;
        this.age = age;
    }

    @Override
    public String toString() {
        return "ReadMemberAvgAgeDto{" +
                "teamName='" + teamName + '\'' +
                ", age=" + age +
                '}';
    }
}
