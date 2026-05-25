package com.example.check_sec.repository;

import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.enums.UserStatus;
import com.example.check_sec.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {

    Optional<SysUser> findByPhone(String phone);

    Optional<SysUser> findByCreditCode(String creditCode);

    boolean existsByPhone(String phone);

    boolean existsByCreditCode(String creditCode);

    long countByRole(RoleType role);

    long countByStatus(UserStatus status);

    @Query("SELECT u FROM SysUser u WHERE u.role <> com.example.check_sec.common.enums.RoleType.ADMIN " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:keyword IS NULL OR u.orgName LIKE %:keyword% OR u.phone LIKE %:keyword% OR u.creditCode LIKE %:keyword%)")
    Page<SysUser> searchUsers(@Param("role") RoleType role, @Param("keyword") String keyword, Pageable pageable);
}
