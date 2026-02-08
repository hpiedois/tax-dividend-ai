package com.taxdividend.bff.client.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxdividend.bff.security.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserContextHeaderFilter implements ExchangeFilterFunction {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    Authentication auth = ctx.getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                        String userId = jwt.getSubject();
                        String email = jwt.getClaimAsString("email"); // Assumes 'email' claim exists
                        List<String> roles = auth.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

                        // Extract identity_provider claim (present for SSO logins: google, github, etc.)
                        // For classic email/password login, this will be null or "keycloak"
                        String identityProvider = jwt.getClaimAsString("identity_provider");

                        UserContext userContext = new UserContext(userId, email, roles, identityProvider);
                        try {
                            String json = objectMapper.writeValueAsString(userContext);
                            String base64Context = Base64.getEncoder().encodeToString(json.getBytes());

                            ClientRequest newRequest = ClientRequest.from(request)
                                    .headers(headers -> {
                                        headers.set("X-User-Context", base64Context);
                                    })
                                    .build();
                            return next.exchange(newRequest);
                        } catch (JsonProcessingException e) {
                            return Mono.error(new RuntimeException("Failed to serialize UserContext", e));
                        }
                    }
                    return next.exchange(request);
                })
                .switchIfEmpty(Mono.defer(() -> next.exchange(request)));
    }
}
