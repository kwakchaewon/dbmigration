package com.example.dbmigration;

import com.example.dbmigration.dto.ReadMemberAvgAgeDto;
import com.example.dbmigration.dto.ReadMemberReturnDto;
import com.example.dbmigration.entity.Member;
import com.example.dbmigration.entity.QMember;
import com.example.dbmigration.entity.QTeam;
import com.example.dbmigration.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
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
    private QMember member = QMember.member;
    private QTeam team = QTeam.team;

    @BeforeEach
    public void before(){
        em.clear();

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

    @Test
    public void sort(){
        em.persist(new Member(null, 100, 1));
        em.persist(new Member("member5", 100, 2));
        em.persist(new Member("member6", 100, 2));

        // nullLast(), nullFirst() 로 null 데이터의 순서를 부여할 수 있음
        List<Member> members = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.username.asc().nullsFirst())
                .fetch();

        System.out.println("members = " + members);
    }

    @Test
    public void paging1(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 0부터 시작 (zero index)
                .limit(2) // 최대 2건 조회
                .fetch();

        System.out.println("result = " + result);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2(){
        QueryResults<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 시작 데이터 위치 조정. 0부터 시작 (zero index)
                .limit(2) // 가져올 데이터 개수 설정. 최대 2건 조회
                .fetchResults();

        System.out.println("result.total = " + result.getTotal());
        System.out.println("result.limit = " + result.getLimit());
        System.out.println("result.offset = " + result.getOffset());
        System.out.println("result.size = " + result.getResults().size());
    }

    @Test
    public void aggregation(){
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        System.out.println("result = " + result);

        Tuple tuple = result.get(0);
        System.out.println("tuple = " + tuple);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(52);
        assertThat(tuple.get(member.age.avg())).isEqualTo(13);
        assertThat(tuple.get(member.age.max())).isEqualTo(16);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 팀 이름과 각 팀의 평균 연령을 구하라
     */
    @Test
    public void group(){
        List<Member> members = queryFactory
                .selectFrom(member)
                .fetch();
        System.out.println("members = " + members);

        // 팀별 멤버 정보
        List<ReadMemberReturnDto> readMemberReturnDtos1 = queryFactory
                .select(Projections.constructor(ReadMemberReturnDto.class,
                        member.id, team.name, member.username, member.age))
                .from(member)
                .leftJoin(team).on(member.teamId.eq(team.id))
                .fetch();

        System.out.println("Team Info = " + readMemberReturnDtos1);

        // 팀별 평균 연령 정보: age 와 avg(age) 는 사용하는 자바 타입이 다르기 때문에 다른 필드 또는 다른 dto 로 선언
        List<ReadMemberAvgAgeDto> readMemberReturnDtos2 = queryFactory
                .select(Projections.constructor(ReadMemberAvgAgeDto.class,
                        team.name, member.age.avg()))
                .from(member)
                .leftJoin(team).on(member.teamId.eq(team.id))
                .groupBy(team.name)
                .fetch();

        System.out.println("Average age by team = " + readMemberReturnDtos2);
    }

    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    public void join(){
        List<Member> members = queryFactory
                .selectFrom(member)
                .leftJoin(team)
                .on(team.id.eq(member.teamId))
                .where(team.name.eq("teamA"))
                .fetch();

        System.out.println("members = " + members);
    }

    /**
     * 세타조인: 연관 관계가 없는 필드로 조인
     * 회원이름과 팀이름이 같은 회원 조회
     */
    @Test
    public void theta_join() {
        em.persist(new Member("teamA", 20, 1));
        em.persist(new Member("teamB", 20, 1));

        List<Member> members = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        System.out.println("members = " + members);
    }

    /**
     * 회원과 팀을 조회하면서 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     */
    @Test
    public void join_on_filtering(){
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(team.id.eq(member.teamId))
                .fetch();

        System.out.println("result = " + result);
    }

    /**
     * 나이가 가장 많은 회원 조회1
     */
    @Test
    public void subQuery1(){
        Member member1 = queryFactory
                .selectFrom(member)
                .orderBy(member.age.desc())
                .fetchFirst();

        System.out.println("member1 = " + member1);
    }

    /**
     *  나이가 가장 많은 회원들 조회2
     */
    @Test
    public void subQuery2(){
        QMember member2 = new QMember("member2");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(member2.age.max())
                                .from(member2)
                ))
                .fetch();

        System.out.println("result = " + result);
    }

    /**
     * 평균 나이 이상인 회원 리스트
     */
    @Test
    public void subQuery3(){
        QMember member2 = new QMember("member2");
        
        List<Member> members = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(member2.age.avg())
                                .from(member2)
                ))
                .fetch();

        System.out.println("members = " + members);
    }

    // in 서브쿼리 예제
    @Test
    public void subQueryIn(){
        QMember member2 = new QMember("member2");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(member2.age)
                                .from(member2)
                                .where(member2.age.gt(10))
                ))
                .fetch();
    }

    
}
