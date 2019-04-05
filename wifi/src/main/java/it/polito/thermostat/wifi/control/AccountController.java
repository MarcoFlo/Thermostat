package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.model.User;
import it.polito.thermostat.wifi.view.FormUserLogin;
import it.polito.thermostat.wifi.view.FormUserRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConcurrentHashMap<String, User> users;

    /**
     * Mapping verso la home dell'applicazione (pagina di login)
     *
     * @param uvm FormUserLogin
     * @return String
     */
    @GetMapping("/")
    public String home(@ModelAttribute("formUserLogin") FormUserLogin uvm) {
        return "login";
    }

    /**
     * Mapping verso la pagina di registrazione dell'applicazione
     *
     * @param uvm FormUserRegistration
     * @return String
     */
    @GetMapping("/register")
    public String viewFormRegistration(@ModelAttribute("formUserRegistration") FormUserRegistration uvm) {
        return "register";
    }

    /**
     * Elaborazione del form di registrazione compilato dall'utente.
     * In caso di successo si viene reindirizzati alla propria pagina privata,
     * altrimenti si ritorna alla pagina di registrazione.
     *
     * @param uvm FormUserRegistration
     * @param res BindingResult
     * @param m   Model
     * @return String
     */
    @PostMapping("/register")
    public String processForm(@Valid @ModelAttribute("formUserRegistration") FormUserRegistration uvm, BindingResult res, Model m) {
        if (!res.hasErrors()) {
            User user = User.builder()
                    .first(uvm.getFirst())
                    .last(uvm.getLast())
                    .pass(uvm.getPass())
                    .email(uvm.getEmail())
                    .privacy(uvm.isPrivacy())
                    .registrationDate(new Date())
                    .build();
            users.put(uvm.getEmail(), user);
            logger.info(uvm.getEmail() + " registrato correttamente.");
            // Metto tra attributi l'oggetto User per visualizzare dati in PrivateHome
            m.addAttribute("user", user);
            return "privatehome";
        } else {
            return "register";
        }
    }


    /**
     * Mapping verso la pagina di login dell'applicazione.
     *
     * @param uvm FormUserRegistration
     * @return String
     */
    @GetMapping("/login")
    public String login(@ModelAttribute("formUserLogin") FormUserLogin uvm) {
        return "login";
    }

    /**
     * Elaborazione del form di login compilato dall'utente.
     * In caso di successo si viene reindirizzati alla propria pagina privata,
     * altrimenti si ritorna alla pagina di login.
     *
     * @param uvm FormUserLogin
     * @param res BindingResult
     * @param m   Model
     * @return String
     */
    @PostMapping("/login")
    public String loginForm(@Valid @ModelAttribute("formUserLogin") FormUserLogin uvm, BindingResult res, Model m) {
        if (!res.hasErrors() && uvm.getPass().equals(users.get(uvm.getEmail()).getPass())) {
            // Login Corretto
            logger.info(uvm.getEmail() + " ha effettuato il login correttamente.");
            m.addAttribute("user", users.get(uvm.getEmail()));
            return "privatehome";
        } else {
            // Login errato
            // Errore globale generico per notificare l'errore senza svelare la vera natura all'utente (sicurezza)
            res.addError(new ObjectError("formUserLogin", "Email and/or Password are incorrect"));
            return "login";
        }
    }
}
