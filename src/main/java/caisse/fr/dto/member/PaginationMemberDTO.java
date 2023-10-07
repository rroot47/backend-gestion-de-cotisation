package caisse.fr.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationMemberDTO {
    private int currentPage;
    private int totalPages;
    private int pageSize;
    @JsonProperty("members")
    private List<AllMemberDTO> allMemberDTOList;
}
