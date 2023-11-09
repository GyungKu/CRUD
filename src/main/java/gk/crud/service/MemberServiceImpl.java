package gk.crud.service;

import gk.crud.entity.member.Member;
import gk.crud.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member join(Member member) {
        member.passwordEncode(passwordEncoder.encode(member.getPassword()));
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
        validPassword(password, member.getPassword());
        return member;
    }

    @Override
    public boolean duplicateMemberVerification(String userId) {
        Member member = memberRepository.findByUserId(userId);
        if(member == null) {
            return true;
        }
        return false;
    }

    public void validPassword(String input, String realPassword) {
        if (!passwordEncoder.matches(input, realPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
