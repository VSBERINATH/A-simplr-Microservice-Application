package com.postal_fetcher_service.postal_fetcher_service.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/postal")
public class PostalController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PostalController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/pincode/{pincode}")
    public ResponseEntity<Map<String, String>> getPostalInfo(@PathVariable String pincode) {
        try {
            String url = "https://api.zippopotam.us/in/" + pincode;
            String raw = restTemplate.getForObject(url, String.class);

            Map<String, String> dto = new HashMap<>();
            JsonNode root = objectMapper.readTree(raw);
            JsonNode places = root.get("places");
            if (places != null && places.isArray() && places.size() > 0) {
                JsonNode first = places.get(0);
                dto.put("district", textOrNull(first.get("place name")));
                dto.put("state", textOrNull(first.get("state")));
            }

            if ((dto.get("district") == null || dto.get("district").isBlank()) &&
                    (dto.get("state") == null || dto.get("state").isBlank())) {
                return ResponseEntity.status(404).body(Map.of("error", "No data found for pincode"));
            }
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", "Upstream API issue: " + e.getClass().getSimpleName());
            return ResponseEntity.status(502).body(err);
        }
    }

    private String textOrNull(JsonNode n) {
        return (n == null || n.isNull()) ? null : n.asText();
    }
}
