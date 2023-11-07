package gk.crud;

import gk.crud.entity.board.Comment;
import gk.crud.entity.board.Post;
import gk.crud.entity.member.Member;
import gk.crud.service.BoardService;
import gk.crud.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class TestDataInit {

    private final MemberService memberService;
    private final BoardService boardService;

    @PostConstruct
    public void init() {
        Member member = new Member("test", "1234", "hello");
        memberService.join(member);

        Post post = new Post("제목", "내용", member);
        boardService.createPost(post);

        for (int i = 0; i<100; i++) {
            boardService.createPost(new Post("제목" + i, "내용" + i, member));
        }

        boardService.createComment(new Comment("댓글내용", post, member));
        boardService.createComment(new Comment("댓글내용2", post, member));
        boardService.createComment(new Comment("댓글내용3", post, member));
        boardService.createComment(new Comment("댓글내용4", post, member));
        boardService.createComment(new Comment("댓글내용5", post, member));
        boardService.createComment(new Comment("댓글내용6", post, member));

    }

}
