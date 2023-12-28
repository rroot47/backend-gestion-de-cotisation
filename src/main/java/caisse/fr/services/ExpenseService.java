package caisse.fr.services;

import caisse.fr.dto.expense.PaginationExpenseDTO;
import caisse.fr.dto.expense.RequestExpenseDTO;
import caisse.fr.dto.expense.ResponseExpenseDTO;
import caisse.fr.dto.member.PaginationMemberDTO;
import caisse.fr.entities.Expense;
import caisse.fr.ripository.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ResponseExpenseDTO saveExpense(RequestExpenseDTO requestExpenseDTO){
        Expense expense = new Expense();
        ResponseExpenseDTO responseExpenseDTO = new ResponseExpenseDTO();

        expense.setTypeExpense(requestExpenseDTO.getTypeExpense());
        expense.setFirstName(requestExpenseDTO.getFirstName());
        expense.setLastName(requestExpenseDTO.getLastName());
        expense.setDescription(requestExpenseDTO.getDescription());
        expense.setDate(requestExpenseDTO.getDate());
        expense.setSomme(requestExpenseDTO.getSomme());
        saveEntitieExpense(responseExpenseDTO, expense);
        return responseExpenseDTO;
    }

    public ResponseExpenseDTO updateExpense(Long expense_id, RequestExpenseDTO requestExpenseDTO){
        ResponseExpenseDTO responseExpenseDTO = new ResponseExpenseDTO();
        Expense expense = expenseRepository.findById(expense_id).orElse(null);
        if(expense==null){
            throw new RuntimeException("Cette depense n'existe pas!!");
        }
        expense.setTypeExpense(requestExpenseDTO.getTypeExpense()==null?expense.getTypeExpense():requestExpenseDTO.getTypeExpense());
        expense.setDescription(requestExpenseDTO.getDescription()==null?expense.getDescription():requestExpenseDTO.getDescription());
        expense.setFirstName(requestExpenseDTO.getFirstName()==null?expense.getFirstName(): requestExpenseDTO.getFirstName());
        expense.setLastName(requestExpenseDTO.getLastName()==null?expense.getLastName(): requestExpenseDTO.getLastName());
        expense.setDate(requestExpenseDTO.getDate()==null?expense.getDate(): requestExpenseDTO.getDate());
        expense.setSomme(requestExpenseDTO.getSomme()==0?expense.getSomme(): requestExpenseDTO.getSomme());

        saveEntitieExpense(responseExpenseDTO, expense);
        return responseExpenseDTO;
    }

    public List<ResponseExpenseDTO> getAllExpense(){
        ResponseExpenseDTO responseExpenseDTO = new ResponseExpenseDTO();
        List<ResponseExpenseDTO> responseExpenseDTOList = new ArrayList<>();
        List<Expense> expenseList = expenseRepository.findAll();
        for(Expense expense:expenseList){
            setExpenseToResponseExpenseDTO(responseExpenseDTO, expense);
            responseExpenseDTOList.add(responseExpenseDTO);
        }
        return responseExpenseDTOList;
    }
    public PaginationExpenseDTO getAllExpenses(int page, int size){
        PaginationExpenseDTO paginationExpenseDTO = new PaginationExpenseDTO();
        Pageable pageable = PageRequest.of(page, size);
        Page<Expense> expensePage = expenseRepository.findAll(pageable);
        List<Expense> expenseList = expensePage.getContent();
        List<ResponseExpenseDTO>  responseExpenseDTOList = new ArrayList<>();

        for(Expense expense:expenseList){
            ResponseExpenseDTO responseExpenseDTO= new ResponseExpenseDTO();
            responseExpenseDTO.setId(expense.getId());
            responseExpenseDTO.setTypeExpense(expense.getTypeExpense());
            responseExpenseDTO.setDescription(expense.getDescription());
            responseExpenseDTO.setFirstName(expense.getFirstName());
            responseExpenseDTO.setLastName(expense.getLastName());
            responseExpenseDTO.setDate(expense.getDate());
            responseExpenseDTO.setSomme(expense.getSomme());
            responseExpenseDTOList.add(responseExpenseDTO);
        }
        paginationExpenseDTO.setCurrentPage(page);
        paginationExpenseDTO.setPageSize(size);
        paginationExpenseDTO.setTotalPages(expensePage.getTotalPages());
        paginationExpenseDTO.setResponseExpenseDTOS(responseExpenseDTOList);
        return paginationExpenseDTO;
    }

    public ResponseExpenseDTO getExpense(Long expense_id){
        ResponseExpenseDTO responseExpenseDTO = new ResponseExpenseDTO();
        Expense expense = expenseRepository.findById(expense_id).orElse(null);
        if(expense==null){
            throw new RuntimeException("Expense not found!!");
        }
        setExpenseToResponseExpenseDTO(responseExpenseDTO, expense);
        return responseExpenseDTO;
    }

    public Map<String, String> deteleExpense(Long expense_id){
        Expense expense = expenseRepository.findById(expense_id).orElse(null);
        if(expense==null){
            throw new RuntimeException("Expense not found!!");
        }
        expenseRepository.delete(expense);
        Map<String, String> messageDelete = new HashMap<>();
        messageDelete.put("message", "the expense is deleted with success");
        return messageDelete;
    }

    public double amountTotalExpenditure(){
        return expenseRepository.amountTotalExpenditure();
    }
    private void saveEntitieExpense(ResponseExpenseDTO responseExpenseDTO, Expense expense) {
        expenseRepository.save(expense);
        setExpenseToResponseExpenseDTO(responseExpenseDTO, expense);
    }
    private void setExpenseToResponseExpenseDTO(ResponseExpenseDTO responseExpenseDTO, Expense expense) {
        responseExpenseDTO.setId(expense.getId());
        responseExpenseDTO.setTypeExpense(expense.getTypeExpense());
        responseExpenseDTO.setFirstName(expense.getFirstName());
        responseExpenseDTO.setLastName(expense.getLastName());
        responseExpenseDTO.setDescription(expense.getDescription());
        responseExpenseDTO.setDate(expense.getDate());
        responseExpenseDTO.setSomme(expense.getSomme());
    }
}
