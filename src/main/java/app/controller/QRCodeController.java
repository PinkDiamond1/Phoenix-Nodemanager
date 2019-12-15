package app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/code")
public class QRCodeController {

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "ApexManager";

    @GetMapping
    @ResponseBody
    public Map<String, String> getQRUrl(@RequestParam("username") final String username,
                                        @RequestParam("secret") final String secret) {
        final Map<String, String> result = new HashMap<>();
        result.put("url", generateQRUrl(secret, username));
        return result;
    }

    private String generateQRUrl(String secret, String username) {
        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                APP_NAME, username, secret, APP_NAME), StandardCharsets.UTF_8);
    }

}
