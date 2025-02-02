package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;

    JPAQueryFactory queryFactory;


    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Optional<Member> findMember = memberRepository.findById(member.getId());
        assertThat(findMember.get()).isEqualTo(member);

        List<Member> result1 = memberRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberRepository.findByUsername("member1");
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

        List<MemberTeamDto> result = memberRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("member4");
    }

    @Test
    public void searchPageSimpleTest() {
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
        PageRequest pageRequest = PageRequest.of(0, 3);
//        condition.setAgeGoe(35);
//        condition.setAgeLoe(40);
//        condition.setTeamName("teamB");

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username").containsExactly("member1", "member2", "member3");
    }

    @Test
    public void querydslPredicateExecutorTest() {
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
        QMember member = QMember.member;
        Iterable<Member> result = memberRepository.findAll(member.age.between(10, 40).and(member.username.eq("member1")));

        for (Member member5 : result) {
            System.out.println("member5 = " + member5);
        }
    }
}