package gk.crud.controller.member;

import lombok.Getter;

@Getter
public class LoginMember {

    public LoginMember(Long id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    private Long id;
    private String userId;

}
