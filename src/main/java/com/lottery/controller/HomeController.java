package com.lottery.controller;

import com.lottery.model.User;
import com.lottery.service.CartService;
import com.lottery.service.LotteryService;
import com.lottery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final LotteryService lotteryService;
    private final CartService cartService;

    @GetMapping
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("lotteryTypes", lotteryService.getAllLotteryTypes());
        model.addAttribute("cartCount", cartService.getCartCount(user));
        model.addAttribute("activePage", "home");
        return "home/index";
    }
}
