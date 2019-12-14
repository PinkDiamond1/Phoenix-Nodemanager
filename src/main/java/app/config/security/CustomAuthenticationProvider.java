package app.config.security;

import app.entity.ApplicationUser;
import app.repository.ApplicationUserRepository;
import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ApplicationUserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        final String username = auth.getName();
        final String password = auth.getCredentials().toString();
        final String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();

        final Optional<ApplicationUser> user = userRepository.findByUsername(username);

        if(user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                final Totp totp = new Totp(user.get().getSecret());
                try {
                    if (!totp.verify(verificationCode)) {
                        log.warn("Invalid verfication code: " + verificationCode);
                        throw new BadCredentialsException("Invalid verfication code");
                    }
                } catch (Exception e) {
                    log.warn("Invalid verfication code: " + verificationCode);
                    throw new BadCredentialsException("Invalid verfication code");
                }
                return new UsernamePasswordAuthenticationToken(user, password, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            }
        }

        log.warn("Invalid username or password");
        throw new BadCredentialsException("Invalid username or password");

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}