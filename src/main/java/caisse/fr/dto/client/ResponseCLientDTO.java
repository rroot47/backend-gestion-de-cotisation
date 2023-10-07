package caisse.fr.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCLientDTO {
    private Long id;
    private String firstName;
    private String email;
    @JsonProperty("roles")
    private List<RequestRoleDTO> roleName;
    //private String verificationCode;
    private boolean enabled;
}
