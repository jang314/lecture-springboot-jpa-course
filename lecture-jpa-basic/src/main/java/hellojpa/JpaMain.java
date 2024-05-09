package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();


        try {
            Member member = em.find(Member.class, 2L);
            System.out.println("findMember : " + member.getName());
            member.setName("helloA2");
          //  em.persist(member);

            tx.commit();
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.clear();
        }

        emf.close();
    }
}
