package com.example.check_sec.security;

import com.example.check_sec.repository.SysUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserRepository userRepository;

    public CustomUserDetailsService(SysUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) {
        return userRepository.findByPhone(phone)
                .map(LoginUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    public LoginUser loadById(Long id) {
        return userRepository.findById(id)
                .map(LoginUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }
}
