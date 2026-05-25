package com.example.check_sec.service;

import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.enums.UserStatus;
import com.example.check_sec.common.exception.BusinessException;
import com.example.check_sec.dto.auth.*;
import com.example.check_sec.entity.SysUser;
import com.example.check_sec.repository.SysUserRepository;
import com.example.check_sec.security.JwtTokenProvider;
import com.example.check_sec.security.LoginUser;
import com.example.check_sec.security.SecurityUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(SysUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (request.getRole() == RoleType.ADMIN) {
            throw new BusinessException("不允许注册管理员账号");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号码已注册");
        }
        if (userRepository.existsByCreditCode(request.getCreditCode())) {
            throw new BusinessException("统一社会信用代码已注册");
        }
        SysUser user = new SysUser();
        user.setOrgName(request.getOrgName());
        user.setCreditCode(request.getCreditCode());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setContactPerson(request.getContactPerson());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setQualificationFileId(request.getQualificationFileId());
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ENABLED);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword()));
        SysUser user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException("账号已禁用，请联系管理员");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getPhone(), user.getRole().name());
        boolean suggestChange = Boolean.TRUE.equals(user.getMustChangePassword());
        return new AuthResponse(token, UserInfoResponse.from(user), suggestChange);
    }

    public UserInfoResponse me() {
        return UserInfoResponse.from(SecurityUtils.currentUser().getUser());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        LoginUser loginUser = SecurityUtils.currentUser();
        SysUser user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getPhone(), request.getOldPassword()));
        } catch (BadCredentialsException e) {
            throw new BusinessException("原密码不正确");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }
}
