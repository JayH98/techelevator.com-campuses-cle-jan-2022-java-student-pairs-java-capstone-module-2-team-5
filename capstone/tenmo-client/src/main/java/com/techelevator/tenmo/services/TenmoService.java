package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class TenmoService {

private static final String API_BASE_URL = "http://localhost:8080/";
private final RestTemplate restTemplate = new RestTemplate();
private String authToken;

public void setAuthToken(String authToken) { this.authToken = authToken;}


public Double getCurrentUserBalance() {
    Double currentBalance = null;
    try {
        restT
    }
}


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }


}
