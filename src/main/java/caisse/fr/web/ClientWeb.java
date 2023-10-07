package caisse.fr.web;

import caisse.fr.dto.client.*;
import caisse.fr.entities.Role;
import caisse.fr.services.ClientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hc")
@CrossOrigin("*")
@SecurityRequirement(name = "Bearer Authorization")
@Tag(name = "API CLIENT")
public class ClientWeb {

    private final ClientService clientService;

    public ClientWeb(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/client/addRole")
    public ResponseRoleDTO addRole(@RequestBody RequestRoleDTO requestRoleDTO) {
        return clientService.saveRole(requestRoleDTO);
    }

    @PostMapping("/client/addClient")
    public ResponseCLientDTO addClient(@RequestBody RequestClientDTO requestClientDTO, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        return clientService.saveClient(requestClientDTO ,getSiteURL(request));
    }

    @PostMapping("/client/signin")
    public SigninDTO login(@RequestBody RequestLoginDTO loginUserDTO){
        return clientService.signin(loginUserDTO);
    }

    @GetMapping("/client/verify")
    public boolean verifyUser(@Param("codeVerify") String codeVerify) {
        return clientService.verify(codeVerify);
    }

    @PostMapping("/client/addRoleToUser")
    //@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Map<String, String> addRoleToUser(@RequestBody ClientRoleDTO clientRoleDTO){
       return clientService.addRoleToClient(clientRoleDTO.getEmail(), clientRoleDTO.getRoleName());
    }
    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/client/getClientByCode")
    public ResponseCLientDTO findClientByCode(@RequestParam("code") String code){
        return clientService.getClientByCode(code);
    }

    @GetMapping("/client/getClientByEmail")
    public Optional<ResponseCLientDTO> findClientByEmail(@RequestParam("email") String email){
        return clientService.getClientByEmail(email);
    }

    @GetMapping("/client/getRoleCleint/{id}")
    public Collection<Role> getRoleName(@PathVariable  Long id){
        return clientService.getRoleName(id);
    }
}
