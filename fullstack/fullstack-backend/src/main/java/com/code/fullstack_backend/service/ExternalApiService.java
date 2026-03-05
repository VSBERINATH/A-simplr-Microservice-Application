package com.code.fullstack_backend.service;

import com.code.fullstack_backend.client.PostalClient;
import com.code.fullstack_backend.dto.PostalInfoDTO;
import com.code.fullstack_backend.dto.PincodeVerificationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExternalApiService {

    private final PostalClient postalClient;
    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);

    public ExternalApiService(PostalClient postalClient) {
        this.postalClient = postalClient;
    }

//    @CircuitBreaker(name = "postalServiceCB", fallbackMethod = "handlePostalFallback")
    public PostalInfoDTO fetchPostalInfo(String pincode) {
        logger.info("Feign call to Postal service for pincode: {}", pincode);
        return postalClient.getPostalInfo(pincode);
    }
//
    // This MUST match (String pincode, Throwable t)
    public PostalInfoDTO handlePostalFallback(String pincode, Throwable t) {
        logger.error("CIRCUIT BREAKER ACTIVE for pincode {}. Reason: {}", pincode, t.getMessage());
        PostalInfoDTO fallback = new PostalInfoDTO();
        fallback.setDistrict("Service Temporary Unavailable");
        fallback.setState("Maintenance in progress");
        return fallback;
    }

    public PincodeVerificationResponse buildVerification(String userAddress, String pincode) {
        PincodeVerificationResponse out = new PincodeVerificationResponse();
        out.setPincode(pincode);
        out.setUserAddress(userAddress);

        PostalInfoDTO info = fetchPostalInfo(pincode);
        String apiAddress = (info != null) ? (safeJoin(info.getDistrict(), info.getState())) : "Unknown";

        out.setApiAddress(apiAddress);
        out.setMatched(info != null && verifyPincodeMatchesAddress(userAddress, pincode));
        return out;
    }

    public boolean verifyPincodeMatchesAddress(String userAddress, String pincode) {
        if (userAddress == null || userAddress.isBlank()) return false;
        PostalInfoDTO info = fetchPostalInfo(pincode);
        if (info == null || "Service Temporary Unavailable".equals(info.getDistrict())) return false;

        String userNorm = normalize(userAddress);
        String combinedParts = normalize(info.getDistrict()) + " " + normalize(info.getState());

        for (String word : combinedParts.split("\\s+")) {
            if (word.length() > 3 && userNorm.contains(word)) return true;
        }
        return false;
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase().replace(",", " ").replace("-", " ").replaceAll("\\s+", " ").trim();
    }

    private String safeJoin(String d, String s) {
        return ((d == null ? "" : d.trim()) + ", " + (s == null ? "" : s.trim())).replaceAll("^, |, $", "");
    }
}