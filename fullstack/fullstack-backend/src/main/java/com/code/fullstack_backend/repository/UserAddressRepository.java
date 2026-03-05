package com.code.fullstack_backend.repository;

import com.code.fullstack_backend.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {
}
