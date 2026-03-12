package com.example.dtsolution.auth.presentation;

import com.example.dtsolution.auth.domain.dto.SignupRequest;
import com.example.dtsolution.global.exception.DuplicateUsernameException;
import com.example.dtsolution.global.exception.PasswordMismatchException;
import com.example.dtsolution.member.application.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final MemberService memberService;

  @GetMapping("/")
  public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
    if (userDetails != null) {
      model.addAttribute("member", memberService.getMemberByUsername(userDetails.getUsername()));
    }
    return "home";
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/signup")
  public String signupPage(Model model) {
    model.addAttribute("signupRequest", new SignupRequest("", "", ""));
    return "signup";
  }

  @PostMapping("/signup")
  public String signup(
      @Valid @ModelAttribute("signupRequest") SignupRequest request,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "signup";
    }
    try {
      memberService.signup(request);
      redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해주세요.");
      return "redirect:/login";
    } catch (PasswordMismatchException e) {
      bindingResult.rejectValue("confirmPassword", "mismatch", e.getMessage());
      return "signup";
    } catch (DuplicateUsernameException e) {
      bindingResult.rejectValue("username", "duplicate", e.getMessage());
      return "signup";
    }
  }

  @GetMapping("/home")
  public String home() {
    return "redirect:/";
  }
}
