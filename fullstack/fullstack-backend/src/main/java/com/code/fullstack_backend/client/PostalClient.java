package com.code.fullstack_backend.client;

import com.code.fullstack_backend.config.FeignConfig;
import com.code.fullstack_backend.dto.PostalInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name: logical name for the client
// url: The address of your Postal Service (through the Gateway)
//@FeignClient(name = "postal-client", url = "http://localhost:8088/postal")
// 1. Create a Fallback class
@Component
class PostalClientFallback implements PostalClient {
    @Override
    public PostalInfoDTO getPostalInfo(String pincode) {
        PostalInfoDTO fallback = new PostalInfoDTO();
        fallback.setDistrict("Service Temporary Unavailable");
        fallback.setState("Maintenance in progress");
        return fallback;
    }
}

// 2. Update the @FeignClient annotation
@FeignClient(name = "postal-client",
        url = "http://localhost:8088/postal",
        configuration = FeignConfig.class,
        fallback = PostalClientFallback.class) // Add this
public interface PostalClient {
    @GetMapping("/pincode/{pincode}")
    PostalInfoDTO getPostalInfo(@PathVariable("pincode") String pincode);
}
//@FeignClient(name = "postal-client", url = "http://localhost:8088/postal", configuration = FeignConfig.class)
//public interface PostalClient {
//
//    @GetMapping("/pincode/{pincode}")
//    PostalInfoDTO getPostalInfo(@PathVariable("pincode") String pincode);
//}