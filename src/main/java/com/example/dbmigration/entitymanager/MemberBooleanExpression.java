package com.example.dbmigration.entitymanager;

import com.example.dbmigration.entity.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;

@Component
public class MemberBooleanExpression {
    private QMember member = QMember.member;

    public BooleanExpression usernameEq(String username) {
        return username != null? member.username.eq(username):null;
    }

    public BooleanExpression ageEq(Integer age) {
        return age != null? member.age.eq(age):null;
    }

    public BooleanExpression searchByKeyword(String username, Integer age){
        if (username != null && age != null) return member.username.eq(username).and(member.age.eq(age));
        else if (username != null) return member.username.eq(username);
        else if (age!=null) return member.age.eq(age);
        else return null;
    }
}
