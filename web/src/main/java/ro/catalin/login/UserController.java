package ro.catalin.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ro.catalin.util.GameMappings;

@Controller
public class UserController {

    @Autowired
    private UserDao userDao;

    @GetMapping(GameMappings.REGISTER)
    public ModelAndView register() {
        return new ModelAndView("register.html");
    }
    @GetMapping(GameMappings.LOGIN)
    public ModelAndView login() {
        return new ModelAndView("login.html");
    }

    @PostMapping("/register-action")
    public ModelAndView registerAction(@RequestParam("email") String email, @RequestParam("password") String password,
                                       @RequestParam("name") String name) {
        User user = new User();

        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);

        userDao.save(user);

        return new ModelAndView(GameMappings.REDIRECT_LOGIN);
    }

    @PostMapping("/login-action")
    public ModelAndView loginAction(@RequestParam("email") String email, @RequestParam("password") String password) {
        User userByEmail = userDao.findByEmail(email);
        if (userByEmail.getPassword().equals(password)) {
            return new ModelAndView(GameMappings.REDIRECT_PLAY);
        } else {
            return new ModelAndView(GameMappings.REDIRECT_LOGIN);
        }

    }
}
