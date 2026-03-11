package com.pfe.pfeaccdemie.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    List<User> findByRole(Role role);

    List<User> findByRoleAndAdminApproved(Role role, boolean adminApproved);

    List<User> findByRoleAndEnabled(Role role, boolean enabled);

    Optional<User> findByActivationToken(String activationToken);
    long countByRole(Role role);

    long countByRoleAndAdminApproved(Role role, boolean adminApproved);
}