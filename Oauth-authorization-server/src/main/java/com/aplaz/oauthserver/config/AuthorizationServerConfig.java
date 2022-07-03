package com.aplaz.oauthserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** All Security Apis (JwtEncoder etc...) exists in applyDefaultSecurity
     *
     *
     */

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        return http.formLogin(Customizer.withDefaults()).build();
    }

    /**
     *  Every client register in authorization server.
     *  So here we need to register clients
     *  We need enable registration over here to add the configuration
     *
     * Registered Client Repository = the repository that has been provived for all clients to get registered
     *
     * SOS! NOW I HAVE ONLY ONE CLIENT AVAILABLE -> THE spring-security-client
     *
     * This is the ````client```` that I want to register with my ```` authorization server ````
     *
     */

    @Bean
    public RegisteredClientRepository registeredClientRepository(){                                     //registered client Repository
        //Register a client with a random ID
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())

                //name of my client is ```api-client``` This name should be given to spring-security-client
                .clientId("api-client")

                //client secret
                .clientSecret(passwordEncoder.encode("secret"))

                //client authentication method is the basic
                //So whatever secret information given above is given in ClientAuthenticationMethod. So I will Authenticate My client
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

                //Authorization Grant Types
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

                //These are the URLS of my client
                //When I authenticated by the Server I want to redirect Here
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/api-client-oidc")

                //When I authorized by the Server I want redirect here
                .redirectUri("http://127.0.0.1:8080/authorized")

                //For OpenID connect
                .scope(OidcScopes.OPENID)
                .scope("api.read")

                //extra clients Settings that I have added
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        //I save in RAM but I can use databases to register our clients
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    /*************  NOW I HAVE TO ADD STANDARD CONFIGURATION FOR PUBLIC AND PRIVATE KEYS
     *
     * JWK = JSON_Web_Key
     * Generates a JWK
     */

    @Bean
    public JWKSource<SecurityContext> jwkSource(){
        RSAKey rsaKey = generateRSA();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return ((jwkSelector, securityContext) -> jwkSelector.select(jwkSet));
    }

    private static RSAKey generateRSA() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex){
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /************************* FINALLY PROVIDE SETTINGS FOR YOUR PROVIDER ****************
     *
     */

    @Bean
    public ProviderSettings providerSettings(){
        return ProviderSettings.builder()
                .issuer("http://oauth-server:9000")
                .build();
    }

}
