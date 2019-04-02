package io.codelex;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
class CustomerController {
    private final AuthService authService;

    CustomerController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-in")
    public void signIn(@RequestParam("email") String email) {
        authService.authorise(email, "ROLE_CUSTOMER");
    }

    @PostMapping("/register")
    public void register(@RequestParam("email") String email) {
        authService.authorise(email, "ROLE_CUSTOMER");
    }

    @PostMapping("/sign-out")
    public void signOut() {
        authService.clearAuthentication();
    }

    @GetMapping("/account")
    public String account(Principal principal) {
        return principal.getName();
    }
}
