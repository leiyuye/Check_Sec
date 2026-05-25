package com.example.check_sec.config;

import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.enums.UserStatus;
import com.example.check_sec.entity.SysUser;
import com.example.check_sec.repository.SysUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SysUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByPhone("admin").isEmpty()) {
            SysUser admin = new SysUser();
            admin.setOrgName("系统管理员");
            admin.setCreditCode("000000000000000000");
            admin.setPhone("admin");
            admin.setPassword(passwordEncoder.encode("admin123456"));
            admin.setContactPerson("管理员");
            admin.setAddress("系统");
            admin.setRole(RoleType.ADMIN);
            admin.setStatus(UserStatus.ENABLED);
            userRepository.save(admin);
        }
    }
}
