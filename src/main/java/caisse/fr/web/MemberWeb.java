package caisse.fr.web;

import caisse.fr.dto.member.PaginationMemberDTO;
import caisse.fr.dto.member.RequestMemberDTO;
import caisse.fr.dto.member.ResponseMemberDTO;
import caisse.fr.dto.membership.ResponseMembershipDTO;
import caisse.fr.services.MemberService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hc")
@CrossOrigin("*")
@SecurityRequirement(name = "Bearer Authorization")
@Tag(name = "API MEMEBER")
public class MemberWeb {

    private final MemberService memberService;

    public MemberWeb(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/member/addMember")
    public ResponseMemberDTO addMember(@RequestBody RequestMemberDTO requestMemberDTO){
        return memberService.saveMember(requestMemberDTO);
    }

    @GetMapping("/member/getMembershipByMember_Id/{member_id}")
    public List<ResponseMembershipDTO> getMembershipByMemberId(@PathVariable Long member_id){
        return memberService.getMembershipByMemberId(member_id);
    }

    @GetMapping("/member/getMemberById/{member_id}")
    public ResponseMemberDTO getMemberById(@PathVariable Long member_id){
        return memberService.getMemberById(member_id);
    }

    @PatchMapping("/member/editMember/{member_id}")
    public ResponseMemberDTO updateMember(@PathVariable Long member_id, @RequestBody RequestMemberDTO requestMemberDTO){
        return memberService.updateMember(member_id, requestMemberDTO);
    }

    @GetMapping("/members/paging")
    public PaginationMemberDTO findAllMembers(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size){
        return memberService.getAllMembers(page,size);
    }

    @GetMapping("/member/totalMounts")
    public  double totalMount(){
        return memberService.totalMount();
    }

    @DeleteMapping("/member/deleteMemberById/{member_id}")
    public Map<String, String> removeMember(@PathVariable Long member_id){
        return memberService.deleteMember(member_id);
    }
}
