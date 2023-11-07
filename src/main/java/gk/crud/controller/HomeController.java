package gk.crud.controller;

import gk.crud.controller.member.LoginMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@SessionAttribute(name = "loginMember", required = false) LoginMember member, Model model) {
        model.addAttribute("member", member);
        return "home";
    }
}
