package com.platform.energy.repo;

import com.platform.energy.entities.ERole;
import com.platform.energy.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    Set<Role> findByNameIn(List<ERole> role);
    Role findRoleByName(ERole role);
}
