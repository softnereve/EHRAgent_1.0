package com.softnerve.epic.service.Implimentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.softnerve.epic.constant.EpicConstants;
import dev.softnerve.annotation.IntentService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@Slf4j
@IntentService
public class EpicAuthService {

    private final WebClient webClient = WebClient.create();

//    public Mono<String> getAccessToken() {
//        try {
//            RSAKey rsaKey = RSAKey.parse(EpicConstants.PRIVATE_JWK_JSON);
//
//            JWTClaimsSet claims = new JWTClaimsSet.Builder()
//                    .issuer(EpicConstants.CLIENT_ID)
//                    .subject(EpicConstants.CLIENT_ID)
//                    .audience(EpicConstants.TOKEN_URL)
//                    .jwtID(UUID.randomUUID().toString())
//                    .issueTime(new Date())
//                    .expirationTime(new Date(System.currentTimeMillis() + 300_000))
//                    .build();
//
//            SignedJWT signedJWT = new SignedJWT(
//                    new JWSHeader.Builder(JWSAlgorithm.RS384)
//                            .keyID(EpicConstants.KID)
//                            .type(JOSEObjectType.JWT)
//                            .build(),
//                    claims
//            );
//
//            signedJWT.sign(new RSASSASigner(rsaKey));
//
//            return webClient.post()
//                    .uri(EpicConstants.TOKEN_URL)
//                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                    .body(BodyInserters.fromFormData("grant_type", "client_credentials")
//                            .with("client_assertion_type",
//                                    "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
//                            .with("client_assertion", signedJWT.serialize()))
//                    .retrieve()
//                    .bodyToMono(JsonNode.class)
//                    .map(json -> json.get("access_token").asText());
//
//        } catch (Exception e) {
//            return Mono.error(e);
//        }
//    }
public Mono<String> getAccessToken(String scope) {
    try {
        RSAKey rsaKey = RSAKey.parse(EpicConstants.PRIVATE_JWK_JSON);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(EpicConstants.CLIENT_ID)
                .subject(EpicConstants.CLIENT_ID)
                .audience(EpicConstants.TOKEN_URL)
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 300_000))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS384)
                        .keyID(EpicConstants.KID)
                        .type(JOSEObjectType.JWT)
                        .build(),
                claims
        );

        signedJWT.sign(new RSASSASigner(rsaKey));

        return webClient.post()
                .uri(EpicConstants.TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("scope", scope) // 🔥 THIS IS THE FIX
                        .with("client_assertion_type",
                                "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                        .with("client_assertion", signedJWT.serialize()))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("access_token").asText());

    } catch (Exception e) {
        return Mono.error(e);
    }
}

}
