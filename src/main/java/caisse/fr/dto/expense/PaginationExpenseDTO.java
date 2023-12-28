package caisse.fr.dto.expense;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationExpenseDTO {
    private int currentPage;
    private int totalPages;
    private int pageSize;
    @JsonProperty("expenses")
    private List<ResponseExpenseDTO> responseExpenseDTOS;
}
