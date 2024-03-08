package com.techelevator.tebucks.TEARS;

import com.techelevator.tebucks.exception.DaoException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TearsService {
    private static final String API_BASE_URL = "https://tears.azurewebsites.net/";
    private final RestTemplate restTemplate = new RestTemplate();

    public TearsLog addLog(TearsLog tearsLog) { //need to be DTO?
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TearsLog> entity = new HttpEntity<>(tearsLog, headers);

        TearsLog returnedTears = null;
        try {
            returnedTears = restTemplate.postForObject(API_BASE_URL + "/api/TxLog", entity, TearsLog.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new DaoException("Something went wrong.", e);
        }
        return returnedTears;
    }
    
}
