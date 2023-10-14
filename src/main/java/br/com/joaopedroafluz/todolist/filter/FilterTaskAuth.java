package br.com.joaopedroafluz.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.joaopedroafluz.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final var servletPath = request.getServletPath();

        if (!servletPath.startsWith("/tasks")) {
            filterChain.doFilter(request, response);
            return;
        }

        final var authorization = request.getHeader("Authorization");
        final var authEncoded = authorization.substring("Basic".length()).trim();
        final var authDecoded = Base64.getDecoder().decode(authEncoded);
        final var authString = new String(authDecoded);
        final var credentials = authString.split(":");
        final var username = credentials[0];
        final var password = credentials[1];

        final var user = this.userRepository.findByUsername(username);

        if (user == null) {
            response.sendError(401);
            return;
        }

        final var isPasswordValid = BCrypt.verifyer().
                verify(password.toCharArray(), user.getPassword().getBytes(StandardCharsets.UTF_8));

        if (!isPasswordValid.verified) {
            response.sendError(401);
            return;
        }

        request.setAttribute("userId", user.getId());
        filterChain.doFilter(request, response);
    }

}
