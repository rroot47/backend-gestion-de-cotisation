package caisse.fr.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SigninDTO {

    private Long id;
    private String token;
    private String username;
    private String email;
    private List<String> roles;
}
