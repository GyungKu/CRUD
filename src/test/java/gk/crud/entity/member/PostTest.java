package gk.crud.entity.member;

import gk.crud.dto.CommentListDto;
import gk.crud.dto.PostCondition;
import gk.crud.dto.PostListDto;
import gk.crud.entity.board.Comment;
import gk.crud.entity.board.Post;
import gk.crud.repository.CommentRepository;
import gk.crud.repository.PostRepository;
import gk.crud.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostTest {

    @Autowired MemberService memberService;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired EntityManager em;

    @Test
    @Commit
    void 게시글_댓글_작성테스트() {
        Member member = new Member("userA", "1234", "test");
        memberService.join(member);

        Post post = new Post("제목", "내용", member);
        postRepository.save(post);

        Comment comment = new Comment("댓글", post, member);
        commentRepository.save(comment);

    }

    @Test
    void 검색_테스트() {
        Member member = new Member("userA", "1234", "test");
        memberService.join(member);

        for (int i = 0; i<100; i++) {
            postRepository.save(new Post("제목" + i, "내용" + i, member));
        }

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<PostListDto> postList = postRepository.findSearchByAll(new PostCondition("title", "제목"), pageRequest, null);

        List<PostListDto> content = postList.getContent();
        for (PostListDto post : content) {
            System.out.println("post = " + post);
        }

        assertThat(content.size()).isEqualTo(10);
    }

    @Test
    void 댓글_테스트() {
        Member member1 = new Member("userA", "1234", "사용자1");
        Member member2 = new Member("userB", "1234", "사용자2");
        memberService.join(member1);
        memberService.join(member2);

        Post post = new Post("게시글", "내용", member1);
        Post post2 = new Post("게시글", "내용", member1);
        postRepository.save(post);
        postRepository.save(post2);

        Comment comment1 = new Comment("댓글1", post, member1);
        Comment comment2 = new Comment("댓글2", post, member2);
        Comment comment3 = new Comment("댓글3", post2, member2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        PageRequest pageRequest = PageRequest.of(0, 2);


        Page<CommentListDto> comments = commentRepository.findMyCommentsByMemberId(member2.getId(), pageRequest);

        List<CommentListDto> content = comments.getContent();
        for (CommentListDto commentListDto : content) {
            System.out.println("commentListDto = " + commentListDto);
        }
    }

    @Test
    void 마이페이지() {
        Member member1 = new Member("userA", "1234", "사용자1");
        Member member2 = new Member("userB", "1234", "사용자2");
        memberService.join(member1);
        memberService.join(member2);

        Post post = new Post("게시글1", "내용1", member1);
        Post post2 = new Post("게시글2", "내용2", member1);
        Post post3 = new Post("게시글3", "내용3", member1);
        Post post4 = new Post("게시글4", "내용4", member2);
        Post post5 = new Post("게시글5", "내용5", member2);
        Post post6 = new Post("게시글6", "내용6", member1);
        postRepository.save(post);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        postRepository.save(post5);
        postRepository.save(post6);

        Comment comment1 = new Comment("댓글1", post, member1);
        Comment comment2 = new Comment("댓글2", post, member2);
        Comment comment3 = new Comment("댓글3", post2, member2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        PageRequest pageRequest = PageRequest.of(0, 10);
        // 게시글 댓글 목록
        Page<CommentListDto> postComments = commentRepository.findCommentsByPostId(post.getId(), pageRequest);
        List<CommentListDto> content = postComments.getContent();

        for (CommentListDto commentListDto : content) {
            System.out.println("commentListDto = " + commentListDto);
        }

        //내 댓글 목록
        Page<CommentListDto> myComments = commentRepository.findMyCommentsByMemberId(member2.getId(), pageRequest);
        List<CommentListDto> content1 = myComments.getContent();

        for (CommentListDto commentListDto : content1) {
            System.out.println("commentListDto = " + commentListDto);
            System.out.println(myComments.getTotalPages());
        }

        //내 게시글 목록
        PostCondition postCondition = new PostCondition(null, null);
        Page<PostListDto> myPost = postRepository.findSearchMyPostByAll(postCondition, pageRequest, member1, "createDate");

        List<PostListDto> postContent = myPost.getContent();
        for (PostListDto postListDto : postContent) {
            System.out.println("postListDto = " + postListDto);
        }

    }

}