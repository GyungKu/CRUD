package gk.crud.controller.member;

import gk.crud.controller.board.SearchSelect;
import gk.crud.dto.CommentListDto;
import gk.crud.dto.PostCondition;
import gk.crud.dto.PostListDto;
import gk.crud.entity.member.Member;
import gk.crud.service.BoardService;
import gk.crud.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class MemberController {

    private final MemberService memberService;
    private final BoardService boardService;
    private static List<SearchSelect> selects;

    public MemberController(MemberService memberService, BoardService boardService) {
        this.memberService = memberService;
        this.boardService = boardService;
        selects = selects();
    }

    @ModelAttribute(name = "member")
    public void login(@SessionAttribute(name = "loginMember", required = false) LoginMember loginMember, Model model) {
        if(loginMember != null) {
            model.addAttribute("member", loginMember);
        }
    }

    //회원가입 창 열기
    @GetMapping("/members/join")
    public String join(Model model) {
        model.addAttribute("member", new MemberJoinForm());
        return "/members/joinForm";
    }

    //회원가입 요청 처리
    @PostMapping("/members/join")
    public String join(@Valid @ModelAttribute("member") MemberJoinForm form, BindingResult bindingResult) {

        // 회원 아이디 중복 검증
        if(!memberService.duplicateMemberVerification(form.getUserId())) {
            bindingResult.reject("duplicateFail", "이미 존재하는 아이디 입니다.");
            return "/members/joinForm";
        }

        if(bindingResult.hasErrors()) {
            return "/members/joinForm";
        }

        Member member = new Member(form.getUserId(), form.getPassword(), form.getName());

        memberService.join(member);
        log.info("회원가입 성공 ={}", member);
        return "redirect:/members/login";
    }

    //로그인 창 열기
    @GetMapping("/members/login")
    public String login(Model model) {
        model.addAttribute("member", new MemberLoginForm());
        return "/members/loginForm";
    }

    //로그인 요청 처리
    @PostMapping("/members/login")
    public String login(@Valid @ModelAttribute("member") MemberLoginForm form, BindingResult bindingResult, HttpServletRequest request, String redirectURL) {

        //아이디, 패스워드 확인 실패
        if(memberService.login(form.getUserId(), form.getPassword()) == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "/members/loginForm";

//            throw new IllegalStateException("아이디 또는 비밀번호가 맞지 않습니다.");
        }

        if(bindingResult.hasErrors()) {
            return "/members/loginForm";
        }

        //로그인 성공
        Member member = memberService.login(form.getUserId(), form.getPassword());
        LoginMember loginMember = new LoginMember(member.getId(), member.getUserId());
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        if (StringUtils.hasText(redirectURL)) {
            return "redirect:" + redirectURL;
        }
        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/test")
    public String test(@SessionAttribute(name = "loginMember", required = false) LoginMember loginMember, Model model) {
        model.addAttribute("member", loginMember);
        return "test";
    }

    //내 정보 보기
    @GetMapping("/members/{memberId}")
    public String myPage(@PathVariable Long memberId, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {
        if (loginMember == null || loginMember.getId() != memberId) {
//            return "redirect:/members/" + loginMember.getId();
            return "redirect:/";
        }
        return "/members/myPage.html";
    }

    @GetMapping("/members/myPosts/{memberId}")
    public String myPosts(@PathVariable Long memberId, @RequestParam(defaultValue = "1") int p, Model model, PostCondition postCond, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {

        if(loginMember.getId() != memberId) {
            return "/members/myPosts" + loginMember.getId();
        }

        PageRequest pageRequest = PageRequest.of(p - 1, 10);
        Member member = memberService.findById(loginMember.getId());
        Page<PostListDto> postListDto = boardService.myPostList(pageRequest, postCond, member);
        List<PostListDto> posts = postListDto.getContent();
        model.addAttribute("pages", postListDto);
        model.addAttribute("posts", posts);
        model.addAttribute("postCond", postCond);
        model.addAttribute("selects", selects);


        return "/members/myPosts.html";
    }

    @GetMapping("/members/myLikes/{memberId}")
    public String myLikes(@PathVariable Long memberId, @RequestParam(defaultValue = "1") int p, Model model, PostCondition postCond, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {

        if(loginMember.getId() != memberId) {
            return "/members/myPosts" + loginMember.getId();
        }

        PageRequest pageRequest = PageRequest.of(p - 1, 10);
        Member member = memberService.findById(loginMember.getId());
        Page<PostListDto> postListDto = boardService.myLikeList(pageRequest, postCond, member);
        List<PostListDto> posts = postListDto.getContent();
        model.addAttribute("pages", postListDto);
        model.addAttribute("posts", posts);
        model.addAttribute("postCond", postCond);
        model.addAttribute("selects", selects);


        return "/members/myLikes.html";
    }

    @GetMapping("/members/myComments/{memberId}")
    public String myComments(@PathVariable Long memberId, @RequestParam(defaultValue = "1") int p, Model model) {

        PageRequest pageRequest = PageRequest.of(p - 1, 10);
        Page<CommentListDto> pages = boardService.myComments(memberId, pageRequest);
        List<CommentListDto> comments = pages.getContent();

        model.addAttribute("pages", pages);
        model.addAttribute("comments", comments);

        return "/members/myComments.html";
    }

    @GetMapping("/members/myComments/delete/{commentId}")
    public String deleteMyComment(@PathVariable Long commentId, @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember) {
        Long postId = boardService.deleteComment(commentId, loginMember.getId());
        return "redirect:/members/myComments/" + loginMember.getId();
    }

    public List<SearchSelect> selects() {
        List<SearchSelect> selects = new ArrayList<>();
        selects.add(new SearchSelect("title", "제목"));
        selects.add(new SearchSelect("content", "내용"));
        return selects;
    }
}
