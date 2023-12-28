package caisse.fr.web;

import caisse.fr.dto.expense.PaginationExpenseDTO;
import caisse.fr.dto.expense.RequestExpenseDTO;
import caisse.fr.dto.expense.ResponseExpenseDTO;
import caisse.fr.services.ExpenseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hc")
@CrossOrigin("*")
@SecurityRequirement(name = "Bearer Authorization")
@Tag(name = "API EXPENSE")
public class ExpenseWeb {

    private final ExpenseService expenseService;


    public ExpenseWeb(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expense/addExpense")
    public ResponseExpenseDTO addExpense(@RequestBody RequestExpenseDTO requestExpenseDTO){
        return expenseService.saveExpense(requestExpenseDTO);
    }

    @PatchMapping("/expense/editExpense/{expense_id}")
    public ResponseExpenseDTO editExpense(@PathVariable Long expense_id, @RequestBody RequestExpenseDTO requestExpenseDTO){
        return expenseService.updateExpense(expense_id, requestExpenseDTO);
    }

    @GetMapping("/expense/allExpenses")
    public PaginationExpenseDTO findAllExpense(@RequestParam(value = "page", defaultValue = "0") int page,
                                               @RequestParam(value = "size", defaultValue = "10") int size){
        return expenseService.getAllExpenses(page, size);
    }

    @GetMapping("/expense/getExpenseById/{expense_id}")
    public ResponseExpenseDTO findExpense(@PathVariable Long expense_id){
        return expenseService.getExpense(expense_id);
    }

    @DeleteMapping("/expense/removeExpenseById/{expense_id}")
    public Map<String, String> removeExpense(@PathVariable Long expense_id){
        return expenseService.deteleExpense(expense_id);
    }

    @GetMapping("/expense/totalExpense")
    public double totalExpenditure(){
        return expenseService.amountTotalExpenditure();
    }
}
