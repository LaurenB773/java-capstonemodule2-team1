package com.techelevator.tebucks.TEARS;

import com.techelevator.tebucks.exception.DaoException;
import org.jboss.logging.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

public class TearsService {
    private static final String API_BASE_URL = "https://tears.azurewebsites.net/";
    private RestTemplate restTemplate = new RestTemplate();

    public TearsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String login(String userName, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("andie", userName);
        requestBody.put("me", password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_BASE_URL + "/login", entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new DaoException("Authentication failed", e);
        }
    }

    public TearsLogDto addLog(TearsLogDto tearsLog, String authToken) { //need to be DTO?
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer" + authToken);
        HttpEntity<TearsLogDto> entity = new HttpEntity<>(tearsLog, headers);

        TearsLogDto returnedTears = null;
        try {
            returnedTears = restTemplate.postForObject(API_BASE_URL + "/api/TxLog", entity, TearsLogDto.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new DaoException("Something went wrong.", e);
        }
        return returnedTears;
    }

}
