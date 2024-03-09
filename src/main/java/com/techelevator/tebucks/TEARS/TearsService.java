package com.techelevator.tebucks.TEARS;

import com.techelevator.tebucks.exception.DaoException;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TearsService {
    private static final String API_BASE_URL = "https://tears.azurewebsites.net/";
    private RestTemplate restTemplate = new RestTemplate();

    public String login() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        TearsLoginDto tearsLoginDto = new TearsLoginDto();
        tearsLoginDto.setUsername("andie");
        tearsLoginDto.setPassword("me");

        HttpEntity<TearsLoginDto> entity = new HttpEntity<>(tearsLoginDto, headers);

        try {
            ResponseEntity<TearsTokenDto> response = restTemplate.postForEntity(API_BASE_URL + "login", entity, TearsTokenDto.class);
            String token = response.getBody().getToken();
            if (token.length() > 0) {
                return token;
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new DaoException("Authentication failed", e);
        }
        return null;
    }

    public void addLog(TearsLogDto tearsLog) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(login());
        HttpEntity<TearsLogDto> entity = new HttpEntity<>(tearsLog, headers);

        try {
           restTemplate.postForObject(API_BASE_URL + "api/TxLog", entity, TearsLogDto.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new DaoException("Something went wrong.", e);
        }
    }

}
