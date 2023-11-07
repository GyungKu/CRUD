package gk.crud.controller.board;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class PostForm {

    @NotEmpty(message = "공백일 수 없습니다.")
    private String title;
    @NotEmpty(message = "공백일 수 없습니다.")
    private String content;

    public PostForm(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
