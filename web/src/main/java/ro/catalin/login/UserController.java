package ro.catalin.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ro.catalin.util.AccountMappings;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserDao userDao;

    HashMap<String, ServerMemory> memory = new HashMap<String, ServerMemory>();

    @GetMapping(AccountMappings.DASHBOARD)
    public ModelAndView showDashboard(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView dashboardMV = new ModelAndView("dashboard.html");
        Cookie[] cookies = req.getCookies();
        if (cookies == null){
            dashboardMV.addObject("err","Utilizatorul nu este autentificat");
            return dashboardMV;
        }

        boolean isAuthenticated = false;

        for (Cookie cookie:cookies){
            if (cookie.getName().equals("name")){
                dashboardMV.addObject("name",cookie.getValue());
                isAuthenticated=true;
            }
        }

        if (!isAuthenticated){
            dashboardMV.addObject("err","Utilizatorul nu este autentificat");
        }

        dashboardMV.addObject("isAuthenticated", isAuthenticated);
        return dashboardMV;
    }

    @GetMapping(AccountMappings.REGISTER)
    public ModelAndView register() {
        return new ModelAndView("register.html");
    }

    @GetMapping(AccountMappings.LOGIN)
    public ModelAndView login() {
        return new ModelAndView("login.html");
    }

    @PostMapping("/register-action")
    public ModelAndView registerAction(@RequestParam("email") String email, @RequestParam("password") String password,
                                       @RequestParam("name") String name, @RequestParam("password2") String password2) {
        User userByEmail = userDao.findByEmail(email);
        User user = new User();
        ModelAndView registerMV = new ModelAndView(AccountMappings.REGISTER);
        if (userByEmail == null) {
            if (!password.equals(password2)) {
                registerMV.addObject("errorPassword","");
                return registerMV;
            } else {
                user.setEmail(email);
                user.setPassword(password);
                user.setName(name);
                userDao.save(user);
                return new ModelAndView(AccountMappings.REDIRECT_LOGIN);
            }
        } else {
            registerMV.addObject("errorEmail","");
            return registerMV;
        }
    }

    @PostMapping("/login-action")
    public ModelAndView loginAction(HttpServletRequest req, HttpServletResponse resp,@RequestParam("email") String email, @RequestParam("password") String password) {
        User userByEmail = userDao.findByEmail(email);
        ModelAndView loginMV = new ModelAndView(AccountMappings.LOGIN);
        if (userByEmail == null){
            loginMV.addObject("errorLogin","");
            return loginMV;
        } else if (userByEmail.getPassword().equals(password)) {
            resp.addCookie(new Cookie("name", userByEmail.getName()));

            Cookie[] cookies = req.getCookies();
            Cookie sessionCookie = createUserCookies(userByEmail,cookies);

            resp.addCookie(sessionCookie);
            return new ModelAndView(AccountMappings.REDIRECT_DASHBOARD);
        } else {
            loginMV.addObject("errorLogin","");
            return loginMV;
        }

    }

    private Cookie createUserCookies(User dbUser, Cookie[] cookies) {
        Cookie sessionCookie = findCookie("sessionId", cookies);
        if (sessionCookie == null){
            UUID uuid = UUID.randomUUID();
            memory.put(uuid.toString(), new ServerMemory(dbUser.getName()));
            sessionCookie = new Cookie("sessionId", uuid.toString());
        }
        return sessionCookie;
    }

    private Cookie findCookie(String name, Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie: cookies){
            if (cookie.getName().equals(name)){
                return cookie;
            }
        }
        return null;
    }
}
