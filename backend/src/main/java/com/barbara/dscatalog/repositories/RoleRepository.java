package com.barbara.dscatalog.repositories;

import com.barbara.dscatalog.entities.Role;
import com.barbara.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
