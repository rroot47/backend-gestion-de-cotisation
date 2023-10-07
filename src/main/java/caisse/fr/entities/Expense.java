package caisse.fr.entities;

import caisse.fr.enums.TypeExpense;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeExpense typeExpense;
    private String description;
    private String firstName;
    private String lastName;
    private String date;
    private double somme;
}
