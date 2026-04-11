package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.Category;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    List<User> findByRoleAndEnabledAndAdminApproved(Role role, boolean enabled, boolean adminApproved);

    List<User> findByRoleAndSport_Id(Role role, Long sportId);

    List<User> findByRoleAndSpecialite_Id(Role role, Long specialiteId);

    List<User> findByRoleAndSport_IdAndEnabledAndAdminApproved(Role role, Long sportId, boolean enabled, boolean adminApproved);

    List<User> findByRoleAndSpecialite_IdAndEnabledAndAdminApproved(Role role, Long specialiteId, boolean enabled, boolean adminApproved);

    List<User> findByRoleAndSportAndNiveau(Role role, Category sport, String niveau);
}