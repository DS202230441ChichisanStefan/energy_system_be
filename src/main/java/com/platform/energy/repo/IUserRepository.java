package com.platform.energy.repo;

import com.platform.energy.entities.ERole;
import com.platform.energy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findUserByUsernameAndPassword(String username, String password);
    User findUserByUsername(String username);
    List<User>findAllByRoleName(ERole role);
}
