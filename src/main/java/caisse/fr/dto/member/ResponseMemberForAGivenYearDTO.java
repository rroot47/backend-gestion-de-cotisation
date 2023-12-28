package caisse.fr.dto.member;

import caisse.fr.entities.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMemberForAGivenYearDTO {
    private int currentPage;
    private int totalPages;
    private int pageSize;
    @JsonProperty("members")
    private List<Member>  members;
}
