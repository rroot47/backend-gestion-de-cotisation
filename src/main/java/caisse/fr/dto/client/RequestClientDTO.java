package caisse.fr.dto.client;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestClientDTO {
    private String firstName;
    private String email;
    private String password;
    private String confirmPassword;
}
