package caisse.fr.ripository;

import caisse.fr.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("select  SUM(e.somme) from Expense e where e.somme NOT IN(0)")
    double amountTotalExpenditure();
}
