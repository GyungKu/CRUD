package gk.crud.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class CommentForm {

    @NotEmpty(message = "공백일 수 없습니다.")
    private String content;

}
