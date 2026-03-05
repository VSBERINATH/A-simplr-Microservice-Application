package com.code.fullstack_backend.controller;

import com.code.fullstack_backend.dto.PincodeVerificationResponse;
import com.code.fullstack_backend.dto.UserRequestDTO;
import com.code.fullstack_backend.dto.UserResponseDTO;
import com.code.fullstack_backend.dto.UserUpdateDTO;
import com.code.fullstack_backend.model.User;
import com.code.fullstack_backend.repository.UserAddressRepository;
import com.code.fullstack_backend.repository.UserRepository;
import com.code.fullstack_backend.service.CustomUserDetailsService;
import com.code.fullstack_backend.service.ExternalApiService;
import com.code.fullstack_backend.service.UserService;
import com.code.fullstack_backend.util.ExcelHelper;
import com.code.fullstack_backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;

@RestController
//@CrossOrigin("http://localhost:5173")
//@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAddressRepository userAddressRepository;


    @Autowired
    private  UserService userService;
    @Autowired
    private  ExternalApiService externalApiService;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;




    @PostMapping("/user")
    UserResponseDTO newUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
        logger.info("Created a New User: " + userRequestDTO);
        return  userService.createUser(userRequestDTO);
    }


    @GetMapping("/users")
    public Page<User> getAllUsers(Pageable pageable) {
        logger.info("Getting All Users");
        return userService.getAllUsers(pageable);
    }


    @GetMapping("/user/{id}")
    UserResponseDTO getUserById(@PathVariable Long id){

        logger.info("Getting Users by Id: " + id);
                return userService.getUserById(id);
    }

    @PutMapping("/user/{id}")
    UserResponseDTO updateUser(@Valid @RequestBody UserUpdateDTO newUser, @PathVariable Long id){

        logger.info("Updating user with ID: " + id + " and data: " + newUser);
        return userService.updateUser(id,newUser);
    }

    @DeleteMapping("/user/{id}")
    String deleteUser(@PathVariable Long id){
       userService.deleteUser(id);
       logger.info("Deleting user with ID: " + id);
       return  "User with id:"+id+" has been deleted";
    }



    @GetMapping("/user/{id}/verify-pincode")
    public ResponseEntity<PincodeVerificationResponse> verifyPincode(@PathVariable Long id,
                                                                     @RequestParam String pincode) {
        return ResponseEntity.ok(userService.verifyUserPincode(id, pincode));
    }

    // Add to UserController.java
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user/upload-excel")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (ExcelHelper.hasExcelFormat(file)) {
            List<UserResponseDTO> responses = userService.uploadExcel(file);
            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload an excel file!");
    }
    @GetMapping("/user/me")
    public UserResponseDTO getLoggedInUser() {
        // This gets the username of the person currently authenticated via Basic Auth
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        return userService.getUserByUsername(username);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, String> authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.get("username"), authRequest.get("password"))
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.get("username"));
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // Get the full user object to return role and other info to frontend
        UserResponseDTO user = userService.getUserByUsername(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwt);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

}
