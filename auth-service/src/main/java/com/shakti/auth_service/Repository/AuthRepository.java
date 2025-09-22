package com.shakti.auth_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shakti.auth_service.Entity.User;
import java.util.Optional;




@Repository
public interface AuthRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);

    @Query("Select u from User u where u.email =:email")
    Optional<User> findByEmail(String email);
}
