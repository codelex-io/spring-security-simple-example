package io.codelex;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/admin-api")
class AdminController {
    @GetMapping("/account")
    public String account(Principal principal) {
        return principal.getName();
    }
}
