package com.camel.test;

import static org.junit.Assert.assertEquals;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CamelApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldGetOrder() throws JSONException, IOException {
        ResponseEntity<String> response = restTemplate.getForEntity("/customers/c1b2a3/orders", String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        File resource = ResourceUtils.getFile("classpath:json/get-order-res.json");
        String expectedJson = new String(Files.readAllBytes(resource.toPath()));

        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }

    @Test
    public void shouldCreateOrder() throws JSONException, IOException {
        File resource = ResourceUtils.getFile("classpath:json/post-order-req.json");

        String reqJson = new String(Files.readAllBytes(resource.toPath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(reqJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/customers/orders", request, String.class);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void shouldReturnBadRequestOnCreateOrder() throws JSONException, IOException {
        String reqJson = "{}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(reqJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/customers/orders", request, String.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldGetOrdersFromDownStream() {
        ResponseEntity<String> response = restTemplate.getForEntity("/backend/customers/c1b2a3/orders", String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void shouldCreateOrderInDownStream() throws IOException {
        File resource = ResourceUtils.getFile("classpath:json/post-order-req.xml");

        String reqXml = new String(Files.readAllBytes(resource.toPath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        HttpEntity<String> request = new HttpEntity<>(reqXml, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/backend/customers/orders", request, String.class);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);

     }
}