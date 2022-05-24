package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/")
public class UsersController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UsersController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String index(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        model.addAttribute("users", userService.index());

        return "users/index";
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {
        if (principal != null) {
            User user = (User) userService.loadUserByUsername(principal.getName());
            Set<String> roles = AuthorityUtils.authorityListToSet(user.getAuthorities());
            boolean roleAdmin = roles.contains("ROLE_ADMIN");
            model.addAttribute("username", principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("roleAdmin", roleAdmin);
        }
        model.addAttribute("users", userService.index());
        return "users/user";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        if (principal != null) {
            String stringRoleList = new String();
            User user = (User) userService.loadUserByUsername(principal.getName());
            model.addAttribute("username", principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("rolesAll", roleService.index());
            model.addAttribute("stringRoleList", stringRoleList);
        }
        model.addAttribute("users", userService.index());
        return "users/admin";
    }


    @PostMapping("/admin/create")
    public String create(@ModelAttribute("user") User user,
                         @RequestParam String stringRoleList) {
        updateUser(user, stringRoleList);
        userService.save(user);
        return "redirect:/admin";
    }


    @PatchMapping("/admin/edit/{id}")
    public String update(@ModelAttribute("user") User user, @PathVariable("id") long id,
                         @RequestParam(required = false) String stringRoleList) {
        updateUser(user, stringRoleList);
        userService.update(id, user);
        return "redirect:/admin";
    }


    @DeleteMapping("/admin/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    private void updateUser(User user, String stringRoleList) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String bCryptPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(bCryptPassword);

        Collection<Role> roleCollection = new HashSet<>();
        if (stringRoleList == null) {
            User oldUser = userService.show(user.getId());
            roleCollection = oldUser.getRoles();
        } else {

            String[] RoleList = stringRoleList.split(",");
            for (String roleString : RoleList) {
                Role role = roleService.index()
                        .stream()
                        .filter(r -> r.getId()
                                == Long.valueOf(roleString))
                        .findFirst()
                        .get();
                roleCollection.add(role);
            }
        }
        user.setRoles(roleCollection);
    }


}
