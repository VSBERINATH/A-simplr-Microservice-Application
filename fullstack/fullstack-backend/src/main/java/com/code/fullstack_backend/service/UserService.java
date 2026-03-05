package com.code.fullstack_backend.service;

import com.code.fullstack_backend.dto.PincodeVerificationResponse;
import com.code.fullstack_backend.dto.UserRequestDTO;
import com.code.fullstack_backend.dto.UserResponseDTO;
import com.code.fullstack_backend.dto.UserUpdateDTO;
import com.code.fullstack_backend.exception.DuplicateEmailException;
import com.code.fullstack_backend.exception.UserNotFoundException;
import com.code.fullstack_backend.model.User;
import com.code.fullstack_backend.model.UserAddress;
import com.code.fullstack_backend.repository.UserAddressRepository;
import com.code.fullstack_backend.repository.UserRepository;
import com.code.fullstack_backend.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private  final UserAddressRepository userAddressRepository;
    private  final UserRepository userRepository;
    @Autowired
    private  ExternalApiService externalApiService;
    @Autowired
    private   EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserService(UserAddressRepository userAddressRepository, UserRepository userRepository) {
        this.userAddressRepository = userAddressRepository;
        this.userRepository = userRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequest) {
        // 1. Check for duplicate email (Existing logic)
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + userRequest.getEmail());
        }

        // 2. Map DTO to Entity
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());

        // NEW: Encode the password before saving
        // Assuming UserRequestDTO has a getPassword() method
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        // Default role if not provided
        String requestedRole = userRequest.getRole();
        if (requestedRole == null || requestedRole.isEmpty()) {
            user.setRole("USER");
        } else {
            user.setRole(requestedRole.toUpperCase());
        }

        // 3. Save User (Existing logic)
        User savedUser = userRepository.save(user);

        // 4. Save Address to the separate table (Existing logic)
        if (userRequest.getAddress() != null) {
            UserAddress userAddress = new UserAddress();
            userAddress.setUserId(savedUser.getId());
            userAddress.setAddress(userRequest.getAddress());
            userAddressRepository.save(userAddress);
            savedUser.setAddress(userRequest.getAddress());
        }

        return mapToResponseDTO(savedUser);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> {
            userAddressRepository.findById(user.getId())
                    .ifPresent(address -> user.setAddress(address.getAddress()));
            return user;
        });
    }


    public UserResponseDTO getUserById(Long id){
       User user = userRepository.findById(id).orElseThrow(()->new UserNotFoundException(id));
        userAddressRepository.findById(id).ifPresent(address->user.setAddress(address.getAddress()));
        user.setLastEdited(user.getLastEdited());
        return mapToResponseDTO(user);
    }


    public UserResponseDTO updateUser(Long id, UserUpdateDTO newUserDTO) {
        // --- NEW SECURITY LOGIC ---
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // If NOT an admin and trying to edit someone else's ID, block it
        if (!isAdmin && !user.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are only authorized to edit your own profile.");
        }
        // ---------------------------

        // Existing Update Logic
        user.setName(newUserDTO.getName());
        user.setUsername(newUserDTO.getUsername());
        user.setEmail(newUserDTO.getEmail());

        // Update Address in the separate table
        if (newUserDTO.getAddress() != null) {
            UserAddress userAddress = new UserAddress();
            userAddress.setUserId(id);
            userAddress.setAddress(newUserDTO.getAddress());
            userAddressRepository.save(userAddress);
            user.setAddress(newUserDTO.getAddress());
        }

        return mapToResponseDTO(userRepository.save(user));
    }

    public void deleteUser(Long id){
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        userAddressRepository.deleteById(id);
    }



    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setLastEdited(user.getLastEdited());
        dto.setRole(user.getRole());
        return dto;
    }


    @Transactional(readOnly = true)
    public PincodeVerificationResponse verifyUserPincode(Long id, String pincode) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userAddressRepository.findById(id)
                .ifPresent(addr -> user.setAddress(addr.getAddress()));

        return externalApiService.buildVerification(user.getAddress(), pincode);
    }

    // Add to UserService.java
    public List<UserResponseDTO> uploadExcel(MultipartFile file) {
        try {
            List<UserRequestDTO> userRequests = ExcelHelper.excelToUsers(file.getInputStream());
            return userRequests.stream()
                    .map(this::createUser) // Reuses duplicate email checks and address saving logic
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Fail to store excel data: " + e.getMessage());
        }
    }

    // Add to UserService.java
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("0L")); // Or a custom UsernameNotFoundException
        return mapToResponseDTO(user);
    }


}
