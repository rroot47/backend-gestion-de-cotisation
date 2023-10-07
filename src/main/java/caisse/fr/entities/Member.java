package caisse.fr.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private int phone;
    private String domicile;
    private  double amountMembership;
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Membership>  memberships =new ArrayList<>();
    private double totalMount=0;
}
