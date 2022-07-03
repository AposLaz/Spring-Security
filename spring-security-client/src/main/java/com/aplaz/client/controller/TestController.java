package com.aplaz.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.*;

/***************
 *
 *      These Resources should be allowed only if
 *      User is authorized by authorization server
 *
 */

@RestController
public class TestController {

    @Autowired
    private WebClient webClient;

//    @GetMapping("/api/test")
//    public String test(Principal principal){
//
//        return "Hello " +principal.getName()+" !!";
//    }

    /**
     *  Trying to get Data from Resource Server that we have defined
     *  and after authenticating and authorizing I am getting the data
     *
     */

    @GetMapping("/api/users")
    public String[] users(
            @RegisteredOAuth2AuthorizedClient("api-client-authorization-code")
            OAuth2AuthorizedClient client){

        return this.webClient
                .get()
                .uri("http://127.0.0.1:8090/api/users")
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }

    /***************************************** STOPPED HERE
     *
     *
     * @param client
     * @param product_name
     * @param price
     * @return
     */

    @GetMapping("/api/product")
    public String products(
            @RegisteredOAuth2AuthorizedClient("api-client-authorization-code")
            OAuth2AuthorizedClient client,
            @RequestParam(name = "product_name") String product_name,
            @RequestParam(name = "price") float price){

        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();

        bodyValues.add("product_name",product_name);
        bodyValues.add("price", String.valueOf(price));

        return this.webClient
                .post()
                .uri("http://127.0.0.1:8090/api/product")
                .body(BodyInserters.fromFormData(bodyValues))
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
