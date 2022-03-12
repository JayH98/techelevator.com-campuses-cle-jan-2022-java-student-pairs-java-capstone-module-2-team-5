package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TenmoService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public TenmoService(String url) {
        this.baseUrl = url;
    }

    public double getUserBalance(long userId) {
        double balance = 0.0;
        try {
            ResponseEntity<Double> response = restTemplate.exchange(baseUrl + "users/" + userId + "/balance", HttpMethod.GET, makeAuthEntity(), Double.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public Transfer[] getTransferHistory(long userId) {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfers/" + userId,
                    HttpMethod.GET,
                    makeAuthEntity(),
                    Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    public boolean sendMoney(Transfer transfer) {
        boolean success = false;
        try {
            restTemplate.put(baseUrl + "transfers", makeTransferEntity(transfer), Transfer[].class);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }


}










