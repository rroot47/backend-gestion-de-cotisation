package caisse.fr.dto.expense;

import caisse.fr.enums.TypeExpense;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestExpenseDTO {
    private TypeExpense typeExpense;
    private String description;
    private String firstName;
    private String lastName;
    private String date;
    private double somme;
}
