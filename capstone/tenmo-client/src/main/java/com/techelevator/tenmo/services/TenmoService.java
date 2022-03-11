package com.techelevator.tenmo.services;

import org.springframework.web.client.RestTemplate;

public class TenmoService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public TenmoService(String url){
        this.baseUrl = url;
    }







}










