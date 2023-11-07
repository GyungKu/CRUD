package gk.crud.service;

import gk.crud.entity.member.Member;

public interface MemberService {

    Member join(Member member);

    Member findById(Long memberId);

    Member findByUserId(String userId);

    Member login(String userId, String password);

    boolean duplicateMemberVerification(String userId);

}
