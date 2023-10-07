package caisse.fr.dto.member;

import caisse.fr.dto.membership.RequestMembershipDTO;
import caisse.fr.dto.membership.ResponseMembershipDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllMemberDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private int phone;
    private String domicile;
    private  double amountMembership;
    @JsonProperty("adhesion")
    private List<ResponseMembershipDTO> requestMembershipDTOS;
    private double totalMount=0;
}
