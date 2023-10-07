package caisse.fr.ripository;

import caisse.fr.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select  SUM(m.totalMount) from Member m where m.totalMount NOT IN(0)")
    double totalMount();

    @Query("select  m.totalMount from Member m where m.id=:member_id")
    int totalMountByMember(Long member_id);
}
