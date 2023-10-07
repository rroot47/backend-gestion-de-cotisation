package caisse.fr.dto.membership;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMembershipDTO {
    private Long id;
    private String year;
    private double amount;
}
