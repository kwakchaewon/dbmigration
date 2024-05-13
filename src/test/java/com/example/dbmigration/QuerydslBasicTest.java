package com.example.dbmigration;

import com.example.dbmigration.entity.Member;
import com.example.dbmigration.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.example.dbmigration.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, 1);
        Member member2 = new Member("member2", 12, 1);
        Member member3 = new Member("member3", 14, 2);
        Member member4 = new Member("member4", 16, 2);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL(){
        // member1 을 찾아라
        String qlString =
                "select m from Member m " +
                        "where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        
        // findMember 가 member1 이다
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        // 1. JPAQueryFactory 만들때 Entitymanager 를 생성자로 넘겨줌
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // 2. 변수명 기입 (크게 중요하진 않음)
//        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();

        // 3. 문자열로 작성하는 JPQL 과 다르게 Querydsl 은 자바 객체를 생성하기 때문에
        // 컴파일 시점에서 오류를 발견할 수 있다.
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
}
