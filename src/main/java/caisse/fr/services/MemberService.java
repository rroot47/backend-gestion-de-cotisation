package caisse.fr.services;

import caisse.fr.dto.member.AllMemberDTO;
import caisse.fr.dto.member.PaginationMemberDTO;
import caisse.fr.dto.member.RequestMemberDTO;
import caisse.fr.dto.member.ResponseMemberDTO;
import caisse.fr.dto.membership.RequestMembershipDTO;
import caisse.fr.dto.membership.ResponseMembershipDTO;
import caisse.fr.entities.Member;
import caisse.fr.entities.Membership;
import caisse.fr.ripository.MemberRepository;
import caisse.fr.ripository.MembershipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;

    public MemberService(MemberRepository memberRepository, MembershipRepository membershipRepository) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
    }

    public ResponseMemberDTO saveMember(RequestMemberDTO requestMemberDTO){
        int somme=0;
        int sommeMembership=0;
        Member member = new Member();
        ResponseMemberDTO responseMemberDTO = new ResponseMemberDTO();
        List<Membership> memberships = new ArrayList<>();
        List<RequestMembershipDTO> requestMembershipDTOS = new ArrayList<>();
        member.setFirstName(requestMemberDTO.getFirstName());
        member.setLastName(requestMemberDTO.getLastName());
        member.setDomicile(requestMemberDTO.getDomicile());
        member.setPhone(requestMemberDTO.getPhone());
        member.setAmountMembership(requestMemberDTO.getAmountMembership());
        for(RequestMembershipDTO requestMembershipDTO: requestMemberDTO.getRequestMembershipDTOS()){
            sommeMembership = setSommeMembership(sommeMembership, requestMembershipDTOS, member, memberships, requestMembershipDTO);

        }
        somme+=member.getAmountMembership()+sommeMembership;
        member.setTotalMount(somme);
        return setResponseMemberDTO(member, responseMemberDTO, memberships, requestMembershipDTOS);
    }

    private ResponseMemberDTO setResponseMemberDTO(Member member, ResponseMemberDTO responseMemberDTO, List<Membership> memberships, List<RequestMembershipDTO> requestMembershipDTOS) {
        member.setMemberships(memberships);
        memberRepository.save(member);
        responseMemberDTO.setId(member.getId());
        return getMemberByIdSetResponseMemberDTO(responseMemberDTO, member, requestMembershipDTOS);
    }

    public List<ResponseMembershipDTO> getMembershipByMemberId(Long member_id){
        List<ResponseMembershipDTO> responseMembershipDTOList = new ArrayList<>();
        List<Membership> memberships = membershipRepository.findByMemberId(member_id);
        for(Membership membership:memberships){
            ResponseMembershipDTO responseMembershipDTO = new ResponseMembershipDTO();
            responseMembershipDTO.setId(membership.getId());
            responseMembershipDTO.setYear(membership.getYear());
            responseMembershipDTO.setAmount(membership.getAmount());
            responseMembershipDTOList.add(responseMembershipDTO);
        }
        return responseMembershipDTOList;
    }

    public ResponseMemberDTO updateMember(Long member_id, RequestMemberDTO requestMemberDTO){
        int somme =0;
        int sommeMembership=0;
        ResponseMemberDTO responseMemberDTO = new ResponseMemberDTO();
        List<RequestMembershipDTO> requestMembershipDTOS = new ArrayList<>();
        Member member = memberRepository.findById(member_id).orElse(null);
        List<Membership> membershipList = membershipRepository.findByMemberId(member_id);
        List<Membership> memberships = new ArrayList<>();
        int totalMountMember = memberRepository.totalMountByMember(member_id);
        int saveSommeTotalMember = 0;
        if(member!=null){
            member.setFirstName(requestMemberDTO.getFirstName()==null?member.getFirstName():requestMemberDTO.getFirstName());
            member.setLastName(requestMemberDTO.getLastName()==null?member.getLastName():requestMemberDTO.getLastName());
            member.setPhone(requestMemberDTO.getPhone()==0? member.getPhone() : requestMemberDTO.getPhone());
            member.setDomicile(requestMemberDTO.getDomicile()==null? member.getDomicile() : requestMemberDTO.getDomicile());
            member.setAmountMembership(requestMemberDTO.getAmountMembership()==0?member.getAmountMembership(): requestMemberDTO.getAmountMembership());
            member.setPhone(requestMemberDTO.getPhone()==0?member.getPhone(): requestMemberDTO.getPhone());
            for(RequestMembershipDTO requestMembershipDTO: requestMemberDTO.getRequestMembershipDTOS()){
                boolean bool=false;
                Membership membershipSave = new Membership();
                for(Membership membership:membershipList){
                    if(membership.getYear().equals(requestMembershipDTO.getYear()) &&
                    member.getId().equals(membership.getMember().getId())){
                        bool = true;
                        membershipSave = membership;
                    }
                }
                if(bool){
                    saveSommeTotalMember+=totalMountMember-membershipSave.getAmount();
                    sommeMembership+=requestMembershipDTO.getAmount();
                    membershipSave.setAmount(requestMembershipDTO.getAmount());
                    membershipRepository.save(membershipSave);
                    memberships.add(membershipSave);
                    requestMembershipDTOS.add(requestMembershipDTO);
                }
                else {
                    sommeMembership = setSommeMembership(sommeMembership, requestMembershipDTOS, member, memberships, requestMembershipDTO);
                }
            }
            if(saveSommeTotalMember==0){
                somme+=saveSommeTotalMember+sommeMembership+totalMountMember;
            }else somme+=saveSommeTotalMember+sommeMembership;

            member.setTotalMount(somme);
            return setResponseMemberDTO(member, responseMemberDTO, memberships, requestMembershipDTOS);
        }
        return null;
    }

    private int setSommeMembership(int sommeMembership, List<RequestMembershipDTO> requestMembershipDTOS, Member member, List<Membership> memberships, RequestMembershipDTO requestMembershipDTO) {
        Membership membership1 = new Membership();
        sommeMembership+=requestMembershipDTO.getAmount();
        membership1.setYear(requestMembershipDTO.getYear());
        membership1.setAmount(requestMembershipDTO.getAmount());
        membership1.setMember(member);
        membershipRepository.save(membership1);
        memberships.add(membership1);
        requestMembershipDTOS.add(requestMembershipDTO);
        return sommeMembership;
    }

    public PaginationMemberDTO getAllMembers(int page, int size){
        PaginationMemberDTO paginationMemberDTO = new PaginationMemberDTO();
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> members = memberRepository.findAll(pageable);
        List<Member>  memberList = members.getContent();
        List<AllMemberDTO> allMemberDTOList = new ArrayList<>();
        List<ResponseMembershipDTO> responseMembershipDTOList;
        for(Member member: memberList){
            AllMemberDTO allMemberDTO = new AllMemberDTO();
            allMemberDTO.setId(member.getId());
            allMemberDTO.setFirstName(member.getFirstName());
            allMemberDTO.setLastName(member.getLastName());
            allMemberDTO.setPhone(member.getPhone());
            allMemberDTO.setDomicile(member.getDomicile());
            allMemberDTO.setAmountMembership(member.getAmountMembership());
            allMemberDTO.setTotalMount(member.getTotalMount());
            for (Membership membership:member.getMemberships()){
                responseMembershipDTOList  = getMembershipByMemberId(membership.getMember().getId());
                allMemberDTO.setRequestMembershipDTOS(responseMembershipDTOList);
            }
            allMemberDTOList.add(allMemberDTO);
        }
        paginationMemberDTO.setCurrentPage(page);
        paginationMemberDTO.setPageSize(size);
        paginationMemberDTO.setTotalPages(members.getTotalPages());
        paginationMemberDTO.setAllMemberDTOList(allMemberDTOList);
        return paginationMemberDTO;
    }

    public ResponseMemberDTO getMemberById(Long member_id){
        ResponseMemberDTO responseMemberDTO = new ResponseMemberDTO();
        Member member = memberRepository.findById(member_id).orElse(null);
        List<Membership> membershipList = membershipRepository.findByMemberId(member_id);
        List<RequestMembershipDTO> requestMembershipDTOList = new ArrayList<>();
        if(membershipList.isEmpty()){
            return null;
        }
        for(Membership memberships:membershipList){
            RequestMembershipDTO  requestMembershipDTO = new RequestMembershipDTO();
            requestMembershipDTO.setYear(memberships.getYear());
            requestMembershipDTO.setAmount(memberships.getAmount());
            requestMembershipDTOList.add(requestMembershipDTO);
        }
        assert false;
        return getMemberByIdSetResponseMemberDTO(responseMemberDTO, member, requestMembershipDTOList);
    }

    private ResponseMemberDTO getMemberByIdSetResponseMemberDTO(ResponseMemberDTO responseMemberDTO, Member member, List<RequestMembershipDTO> requestMembershipDTOList) {
        responseMemberDTO.setId(member.getId());
        responseMemberDTO.setFirstName(member.getFirstName());
        responseMemberDTO.setLastName(member.getLastName());
        responseMemberDTO.setPhone(member.getPhone());
        responseMemberDTO.setDomicile(member.getDomicile());
        responseMemberDTO.setAmountMembership(member.getAmountMembership());
        responseMemberDTO.setRequestMembershipDTOS(requestMembershipDTOList);

        return responseMemberDTO;
    }

    public  double totalMount(){
        return memberRepository.totalMount();
    }

    public Map<String, String> deleteMember(Long member_id){
        Member member = memberRepository.findById(member_id).orElse(null);
        if(member==null){
            throw new RuntimeException("Member not found!!");
        }
        memberRepository.delete(member);
        Map<String, String> messageDelete = new HashMap<>();
        messageDelete.put("message", "the memeber is deleted with success");
        return messageDelete;
    }
}