package caisse.fr.services;

import caisse.fr.dto.client.*;
import caisse.fr.entities.Client;
import caisse.fr.entities.Role;
import caisse.fr.ripository.ClientRepository;
import caisse.fr.ripository.RoleRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.bytebuddy.utility.RandomString;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private final ClientRepository  clientRepository;
    private final RoleRepository  roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;

    public ClientService(ClientRepository clientRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, AuthenticationManager authenticationManager, JavaMailSender mailSender) {
        this.clientRepository = clientRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
    }

    public ResponseRoleDTO saveRole(RequestRoleDTO requestRoleDTO) {
        ResponseRoleDTO responseRoleDTO = new ResponseRoleDTO();
        Role role = new Role();
        role.setRoleName(requestRoleDTO.getRoleName());
        roleRepository.save(role);
        responseRoleDTO.setId(role.getId());
        responseRoleDTO.setRoleName(role.getRoleName());
        return responseRoleDTO;
    }
    public Map<String, String> addRoleToClient(String email, String roleName) {
        Optional<Client> client = clientRepository.findClientByEmail(email);
        Role role = roleRepository.findRoleByRoleName(roleName);
        client.get().getRoles().add(role);
        clientRepository.save(client.get());
        Map<String, String> message = new HashMap<>();
        message.put("message", "the role is added with success");
        return message;
    }

    public ResponseCLientDTO saveClient(RequestClientDTO requestClientDTO, String siteURL) throws MessagingException, UnsupportedEncodingException {
        ResponseCLientDTO responseCLientDTO =new ResponseCLientDTO();
        List<RequestRoleDTO> requestRoleDTOList = new ArrayList<>();
        Client client = new Client();
        client.setFirstName(requestClientDTO.getFirstName());
        client.setEmail(requestClientDTO.getEmail());
        client.setPassword(requestClientDTO.getPassword());
        client.setConfirmPassword(requestClientDTO.getConfirmPassword());
        String randomCode = RandomString.make(6);
        client.setVerificationCode(randomCode);
        client.setEnabled(false);
        if(!client.getPassword().equals(requestClientDTO.getConfirmPassword())){
            throw new RuntimeException("Please confirm you password");
            //throw new IllegalArgumentException("Passwords don't match.");
        }
        client.setPassword(passwordEncoder.encode(requestClientDTO.getPassword()));
        clientRepository.save(client);
        addRoleToClient(requestClientDTO.getEmail(), "USER");
        //sendVerificationEmail(client, siteURL);
        return setIdClientToResponseCLientDTO(responseCLientDTO, client, requestRoleDTOList);
    }

    public SigninDTO signin(RequestLoginDTO requestLoginDTO){
        String subject;
        SigninDTO signinDTO = new SigninDTO();
        List<String> scope;
        Instant instant=Instant.now();
        Optional<Client> client = clientRepository.findClientByEmail(requestLoginDTO.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestLoginDTO.getEmail(), requestLoginDTO.getPassword())
        );
        subject=authentication.getName();
        scope=authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        JwtClaimsSet jwtClaimsSet=JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(instant)
                .expiresAt(instant.plus(3, ChronoUnit.HOURS))
                .issuer("auth-service")
                .claim("scope",scope)
                .build();
        String jwtAccessToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        signinDTO.setToken(jwtAccessToken);
        signinDTO.setId(client.get().getId());
        signinDTO.setUsername(client.get().getFirstName());
        signinDTO.setEmail(client.get().getEmail());
        signinDTO.setRoles(scope);
        return signinDTO;
    }

    public void sendVerificationEmail(Client client, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = client.getEmail();
        String fromAddress = "boubousylla2@gmail.com";
        String senderName = "hocolou";
        String subject = "Veuillez vérifier votre inscription";
        String content = "Bonjour [[name]],<br>"
                /* + "Please click the link below to verify your registration:<br>"*/
                + "Veuillez copier le code ci-dessous pour vérifier votre inscription:<br>"
                /*+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"*/
                + "<h3>Votre code de verification : \"[[CODE]]\"</h3>"
                + "Merci,<br>"
                + "CAISSE HOCOLOU.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", client.getFirstName());
        //String verifyURL = siteURL + "/verify?code=" + requestUserDTO.getVerificationCode();

        String verifyCode = client.getVerificationCode();
        content = content.replace("[[CODE]]", verifyCode);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        Client client = clientRepository.findByVerificationCode(verificationCode);
        if (client == null || client.isEnabled()) {
            return false;
        } else {
            client.setEnabled(true);
            clientRepository.save(client);
            return true;
        }

    }

    public ResponseCLientDTO getClientByCode(String code){
        ResponseCLientDTO responseCLientDTO = new ResponseCLientDTO();
        Client client =clientRepository.findByVerificationCode(code);
        List<RequestRoleDTO> requestRoleDTOList = new ArrayList<>();
        return setIdClientToResponseCLientDTO(responseCLientDTO, client, requestRoleDTOList);
    }

    private ResponseCLientDTO setIdClientToResponseCLientDTO(ResponseCLientDTO responseCLientDTO, Client client, List<RequestRoleDTO> requestRoleDTOList) {
        responseCLientDTO.setId(client.getId());
        responseCLientDTO.setFirstName(client.getFirstName());
        responseCLientDTO.setEmail(client.getEmail());
        responseCLientDTO.setEnabled(client.isEnabled());
        for(Role role:client.getRoles()){
            RequestRoleDTO requestRoleDTO = new RequestRoleDTO();
            requestRoleDTO.setRoleName(role.getRoleName());
            requestRoleDTOList.add(requestRoleDTO);
        }
        responseCLientDTO.setRoleName(requestRoleDTOList);
        return responseCLientDTO;
    }

    public Optional<ResponseCLientDTO>  getClientByEmail(String email){
        ResponseCLientDTO responseCLientDTO = new ResponseCLientDTO();
        List<RequestRoleDTO> requestRoleDTOList = new ArrayList<>();
        Optional<Client> client = clientRepository.findClientByEmail(email);
        responseCLientDTO.setId(client.get().getId());
        responseCLientDTO.setFirstName(client.get().getFirstName());
        responseCLientDTO.setEmail(client.get().getEmail());
        responseCLientDTO.setEnabled(client.get().isEnabled());
        for(Role role:client.get().getRoles()){
            RequestRoleDTO requestRoleDTO = new RequestRoleDTO();
            requestRoleDTO.setRoleName(role.getRoleName());
            requestRoleDTOList.add(requestRoleDTO);
        }
        responseCLientDTO.setRoleName(requestRoleDTOList);
        return Optional.of(responseCLientDTO);
    }

    public  Collection<Role> getRoleName(Long id){
        Client client = clientRepository.findById(id).orElse(null);
        assert client != null;
        return client.getRoles();
    }
}
