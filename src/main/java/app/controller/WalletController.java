package app.controller;

import app.config.ApplicationPaths;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.WALLET_PAGE)
public class WalletController {

    @GetMapping
    public String getWalletPage(){
        return ApplicationPaths.WALLET_PAGE;
    }

}
