package gk.crud.controller.board;

import gk.crud.controller.member.LoginMember;
import gk.crud.dto.CommentForm;
import gk.crud.dto.CommentListDto;
import gk.crud.dto.PostCondition;
import gk.crud.dto.PostListDto;
import gk.crud.entity.board.Comment;
import gk.crud.entity.board.Post;
import gk.crud.entity.member.Member;
import gk.crud.repository.CommentRepository;
import gk.crud.service.BoardService;
import gk.crud.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BoardController {

    private static List<SearchSelect> selects;
    private final BoardService boardService;
    private final MemberService memberService;
    private final CommentRepository commentRepository;

    public BoardController(BoardService boardService, MemberService memberService, CommentRepository commentRepository) {
        this.boardService = boardService;
        this.memberService = memberService;
        this.commentRepository = commentRepository;
        selects = selects();
    }

    @ModelAttribute(name = "member")
    public void login(@SessionAttribute(name = "loginMember", required = false) LoginMember loginMember, Model model) {
        if(loginMember != null) {
            model.addAttribute("member", loginMember);
        }
    }

    @GetMapping("/board")
    public String board(@RequestParam(defaultValue = "1") int p, Model model, PostCondition postCond) {
        List<SearchSelect> selects = selects();
        PageRequest pageRequest = PageRequest.of(p-1, 10);
        model.addAttribute("selects", selects);
        model.addAttribute("postCond", postCond);
        Page<PostListDto> pages = boardService.postList(pageRequest, postCond);
        List<PostListDto> posts = pages.getContent();
        model.addAttribute("posts", posts);
        model.addAttribute("pages", pages);
        return "/board/board.html";
    }

    @GetMapping("/board/{postId}")
    public String postView(@PathVariable Long postId, Model model, @RequestParam(defaultValue = "1") int p, CommentForm form, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {
        Post post = boardService.postView(postId);
        PostListDto postDto = new PostListDto();
        postDto.convert(post);
        model.addAttribute("post", postDto);
        model.addAttribute("form", form);
        System.out.println("postDto = " + postDto.getLikeCount());

        Member member = memberService.findById(loginMember.getId());
        Boolean hasLike = boardService.hasLike(member, post);
        model.addAttribute("hasLike", hasLike);

        PageRequest pageRequest = PageRequest.of(p - 1, 10);
        Page<CommentListDto> pages = boardService.commentsByPost(postId, pageRequest);
        List<CommentListDto> comments = pages.getContent();

        model.addAttribute("comments", comments);
        model.addAttribute("pages", pages);
        System.out.println(pages.getNumber());

        return "/board/post.html";
    }

    @PostMapping("/board/{postId}")
    public String createComment(@PathVariable Long postId, @Valid @ModelAttribute(name = "form") CommentForm form, BindingResult bindingResult, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {

        if(bindingResult.hasErrors()) {
            return "redirect:/board/" + postId;
        }
        Post post = boardService.postOne(postId);
        Member member = memberService.findById(loginMember.getId());
        Comment comment = new Comment(form.getContent(), post, member);
        boardService.createComment(comment);

        return "redirect:/board/" + postId;
    }

    @GetMapping("/board/createPost")
    public String createPost(PostForm form, Model model) {
        model.addAttribute("form", form);
        return "/board/createPost.html";
    }

    @PostMapping("/board/createPost")
    public String createPost(@Valid @ModelAttribute(name = "form") PostForm form, BindingResult bindingResult, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember, Model model) {
        Member member = memberService.findById(loginMember.getId());

        if (bindingResult.hasErrors()) {
            return "board/createPost.html";
        }

        Post post = new Post(form.getTitle(), form.getContent(), member);
        Long postId = boardService.createPost(post);
        return "redirect:/board/" + postId;
    }

    @GetMapping("/board/edit/{postId}")
    public String editPost(@PathVariable Long postId, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember, PostForm form, BindingResult bindingResult, Model model) {

        Post post = boardService.postOne(postId);
        model.addAttribute("postId", postId);

        //내 게시글이 아닐 시
        if(post.getMember().getId() != loginMember.getId()) {
            bindingResult.reject("notMyPost", "권한이 없습니다.");
            return "redirect:/board/" + postId;
        }

        //내 게시글 일 시
        PostForm postForm = new PostForm(post.getTitle(), post.getContent());
        model.addAttribute("form", postForm);
        return "/board/edit.html";
    }

    @PostMapping("/board/edit/{postId}")
    public String editPost(@Valid @ModelAttribute(name = "form") PostForm form, BindingResult bindingResult, Model model, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember, @PathVariable Long postId) {

        if (bindingResult.hasErrors()) {
            return "board/edit.html";
        }

        Member member = memberService.findById(loginMember.getId());

        Post post = new Post(form.getTitle(), form.getContent(), member);
        boardService.updatePost(post, postId);

        return "redirect:/board/" + postId;
    }

    @GetMapping("/board/delete/{postId}")
    public String deletePost(@PathVariable Long postId, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {
        boardService.deletePost(postId, loginMember.getId());
        return "redirect:/board";
    }

    @GetMapping("/board/comment/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {
        Long postId = boardService.deleteComment(commentId, loginMember.getId());
        return "redirect:/board/" + postId;
    }

    public List<SearchSelect> selects() {
        List<SearchSelect> selects = new ArrayList<>();
        selects.add(new SearchSelect("title", "제목"));
        selects.add(new SearchSelect("writerId", "작성자"));
        selects.add(new SearchSelect("content", "내용"));
        return selects;
    }

    @GetMapping("/board/like/{postId}")
    public String like(@PathVariable Long postId, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {
        Member member = memberService.findById(loginMember.getId());
        Post post = boardService.postOne(postId);
        boardService.like(member, post);
        return "redirect:/board/" + postId;
    }

    @GetMapping("/board/unLike/{postId}")
    public String unLike(@PathVariable Long postId, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {
        Member member = memberService.findById(loginMember.getId());
        Post post = boardService.postOne(postId);
        boardService.unLike(member, post);
        return "redirect:/board/" + postId;
    }

}
