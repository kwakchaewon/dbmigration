package com.example.dbmigration;

import com.example.dbmigration.entity.Hello;
import com.example.dbmigration.entity.Member;
import com.example.dbmigration.entity.QHello;
import com.example.dbmigration.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class DbmigrationApplicationTests {

    @Autowired
    EntityManager em;

    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = QHello.hello;

        Hello result = query
                .selectFrom(qHello)
                .fetchOne();

        assertThat(result).isEqualTo(hello);
        assertThat(result.getId()).isEqualTo(hello.getId());
    }

    @Test
    public void testEntity(){
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
        
        // 초기화
        em.flush(); // 영속성 컨텍스트 즉시 반영
        em.clear(); // 영속성 컨텍스트 비우기

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
        }
        
    }


}
