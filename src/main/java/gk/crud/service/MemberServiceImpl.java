package gk.crud.service;

import gk.crud.entity.member.Member;
import gk.crud.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public Member join(Member member) {
        memberRepository.save(member);
        return member;
    }

    @Override
    public Member findById(Long memberId) {
        Member member = memberRepository.findById(memberId).get();
        return member;
    }

    @Override
    public Member findByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId);
        return member;
    }

    @Override
    public Member login(String userId, String password) {
        Member member = findByUserId(userId);
        if(member != null && member.getPassword().equals(password)) {
            return member;
        }
        return null;
    }

    @Override
    public boolean duplicateMemberVerification(String userId) {
        Member member = memberRepository.findByUserId(userId);
        if(member == null) {
            return true;
        }
        return false;
    }
}
