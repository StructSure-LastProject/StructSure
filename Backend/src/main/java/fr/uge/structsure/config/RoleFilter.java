package fr.uge.structsure.config;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import fr.uge.structsure.services.AuthValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Filter that applies role restrictions for API endpoints
 */
@Component
public class RoleFilter extends OncePerRequestFilter {

    private final AuthValidationService authValidationService;
    private final AccountRepository accountRepository;
    private final RequestMappingHandlerMapping handlerMapping;

    /**
     * Internal constructor intended to be used by Spring only to set
     * autowired fields.
     * @param authValidationService authentication validation service
     * @param accountRepository access to the accounts in the database
     * @param handlerMapping to retrieve Java method for a given endpoint
     */
    public RoleFilter(AuthValidationService authValidationService,
        AccountRepository accountRepository,
        RequestMappingHandlerMapping handlerMapping) {
        this.authValidationService = authValidationService;
        this.accountRepository = accountRepository;
        this.handlerMapping = handlerMapping;
    }

    /**
     * Makes sure that the request is authorized for the current user
     * role checking if the handler method has a {@link RequiresRole}
     * @param request the request to get the handler method for
     * @param response to put errors in if any
     * @param filterChain to call other filters if the request is valid
     * @throws ServletException in case of unexpected exception
     */
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException {
        try {
            var handlerChain = handlerMapping.getHandler(request);
            if (handlerChain != null && handlerChain.getHandler() instanceof HandlerMethod handlerMethod) {
                var requiredRole = handlerMethod.getMethodAnnotation(RequiresRole.class);

                if (requiredRole != null) {
                    var user = authValidationService.checkTokenValidityAndUserAccessVerifier(request, accountRepository);
                    if (user.getRole().ordinal() < requiredRole.value().ordinal()){
                        throw new TraitementException(Error.UNAUTHORIZED_OPERATION);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (TraitementException e) {
            response.setStatus(e.error.code);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

