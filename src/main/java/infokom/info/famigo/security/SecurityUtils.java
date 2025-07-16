package infokom.info.famigo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getLoggedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new IllegalStateException("kein User hat gefunden");
    }
}
