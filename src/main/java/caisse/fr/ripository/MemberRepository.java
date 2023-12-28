package caisse.fr.ripository;

import caisse.fr.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select  SUM(m.totalMount) from Member m where m.totalMount NOT IN(0)")
    double totalMount();

    @Query("select  m.totalMount from Member m where m.id=:member_id")
    int totalMountByMember(Long member_id);

    /**
     * Cette requête utilise une sous-requête pour obtenir les identifiants
     * des membres ayant une adhésion pour l'année spécifiée. Ensuite,
     * elle filtre les membres principaux en fonction de ces identifiants.
     * Ainsi, seuls les membres ayant une adhésion pour l'année 2023 seront renvoyés.
     * **/
    @Query("SELECT DISTINCT m FROM Member m JOIN FETCH m.memberships ms WHERE ms.year = :year AND m.id" +
            " IN (SELECT DISTINCT m2.id FROM Member m2 JOIN m2.memberships ms2 WHERE ms2.year = :year)")
    List<Member> getMembersByMembershipYear(@Param("year") String year);

    @Query("SELECT m FROM Member m JOIN FETCH m.memberships ms WHERE ms.year = :year")
    Page<Member> findMembersByYear(@Param("year") String year, Pageable pageable);
}
