package gk.crud.controller.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberLoginForm {

    @NotEmpty(message = "아이디를 입력해주세요")
    private String userId;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    private String password;

}
