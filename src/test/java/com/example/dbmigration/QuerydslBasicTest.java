package com.example.dbmigration;

import com.example.dbmigration.entity.Member;
import com.example.dbmigration.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

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

    @Test
    public void Search(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        // 검색 조건 예시
//        member.username.eq("member1") // username = 'member1'
//        member.username.ne("member1") //username != 'member1'
//        member.username.eq("member1").not() // username != 'member1'
//        member.username.isNotNull() //이름이 is not null
//        member.age.in(10, 20) // age in (10,20)
//        member.age.notIn(10, 20) // age not in (10, 20)
//        member.age.between(10,30) //between 10, 30
//        member.age.goe(30) // age >= 30
//        member.age.gt(30) // age > 30
//        member.age.loe(30) // age <= 30
//        member.age.lt(30) // age < 30
//        member.username.like("member%") //like 검색
//        member.username.contains("member") // like ‘%member%’ 검색
//        member.username.startsWith("member") //like ‘member%’ 검색

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
    
    @Test
    public void SearchAndParam(){
        // where 절의 파라미터의 and 는 생략할 수 있음
        // 조건이 null 일 경우 조건 무시 -> 추후 동적 쿼리 만들 때 사용되는 개념
        List<Member> result1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(10), null)
                .fetch();
        assertThat(result1.size()).isEqualTo(1);
    }

    @Test
    public void resultFetch(){
        // fetch: 리스트 조회
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();
        System.out.println("fetch = " + fetch);

        // fetchOne(): 단건 조회. 결과가 둘 이상일시 NonUniqueResultException
        Member fetchOne = queryFactory
                .selectFrom(member)
                .where(member.age.eq(10))
                .fetchOne();
        System.out.println("fetchOne = " + fetchOne);

        // fetchFirst(): limit(1).fetchOne()
        Member fetchFirsst = queryFactory
                .selectFrom(member)
                .fetchFirst();
        System.out.println("fetchFirsst = " + fetchFirsst);

        // fetchResults: 페이징에서 사용
        // limit, offset, total 등 제공
        // 단 어플리케이션의 규모가 커질수록 잘 사용하지 않는다 (페이징 정보 따로 구현)
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
        System.out.println("results = " + results);

        Long total = results.getTotal();
        List<Member> members = results.getResults();
        System.out.println("total = " + total);
        System.out.println(members);

        Long total2 = queryFactory
                .selectFrom(member)
                .fetchCount();
        System.out.println("total2 = " + total2);
    }

}
