package study.querydsl.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    JPAQueryFactory queryFactory;


    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Optional<Member> findMember = memberJpaRepository.findById(member.getId());
        assertThat(findMember.get()).isEqualTo(member);

        List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    public void basicTest_querydsl() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Optional<Member> findMember = memberJpaRepository.findById(member.getId());
        assertThat(findMember.get()).isEqualTo(member);

        List<Member> result1 = memberJpaRepository.findAll_QueryDls();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findByUsername_querydsl("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    public void searchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        // teamA로 했더니 (Commit시) transaction marked 익셉션이 터졌다.
        // Transaction silently rolled back because it has been marked as rollback-only
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);

        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("member4");
    }



}