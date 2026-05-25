package com.example.check_sec.service;

import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.enums.UserStatus;
import com.example.check_sec.common.exception.BusinessException;
import com.example.check_sec.dto.auth.UserInfoResponse;
import com.example.check_sec.dto.common.PageResult;
import com.example.check_sec.dto.user.UserAdminRequest;
import com.example.check_sec.dto.user.UserListResponse;
import com.example.check_sec.entity.SysUser;
import com.example.check_sec.repository.FilingRecordRepository;
import com.example.check_sec.repository.SysUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class UserService {

    private final SysUserRepository userRepository;
    private final FilingRecordRepository filingRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(SysUserRepository userRepository,
                       FilingRecordRepository filingRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.filingRepository = filingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResult<UserListResponse> list(RoleType role, String keyword, int page, int size) {
        PageRequest pr = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SysUser> result = userRepository.searchUsers(
                role == RoleType.ADMIN ? null : role,
                StringUtils.hasText(keyword) ? keyword.trim() : null,
                pr);
        return new PageResult<>(
                result.getContent().stream().map(UserListResponse::from).toList(),
                result.getTotalElements(),
                page,
                size);
    }

    public UserInfoResponse detail(Long id) {
        return UserInfoResponse.from(getUser(id));
    }

    @Transactional
    public UserInfoResponse create(UserAdminRequest request) {
        if (request.getRole() == RoleType.ADMIN) {
            throw new BusinessException("请通过系统初始化创建管理员");
        }
        validateUnique(request.getPhone(), request.getCreditCode(), null);
        SysUser user = mapRequest(new SysUser(), request);
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return UserInfoResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserInfoResponse update(Long id, UserAdminRequest request) {
        SysUser user = getUser(id);
        if (user.getRole() == RoleType.ADMIN && request.getRole() != RoleType.ADMIN) {
            throw new BusinessException("不能修改管理员角色");
        }
        validateUnique(request.getPhone(), request.getCreditCode(), id);
        mapRequest(user, request);
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return UserInfoResponse.from(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        SysUser user = getUser(id);
        if (user.getRole() == RoleType.ADMIN) {
            throw new BusinessException("不能删除管理员账号");
        }
        boolean hasFiling = filingRepository.existsByFilingUnitUserId(id)
                || filingRepository.existsBySubmitterId(id);
        if (hasFiling) {
            user.setStatus(UserStatus.DISABLED);
            userRepository.save(user);
        } else {
            userRepository.delete(user);
        }
    }

    @Transactional
    public void updateStatus(Long id, UserStatus status) {
        SysUser user = getUser(id);
        if (user.getRole() == RoleType.ADMIN) {
            throw new BusinessException("不能禁用管理员账号");
        }
        user.setStatus(status);
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(Long id, Map<String, String> body) {
        SysUser user = getUser(id);
        String newPassword = body.get("password");
        if (!StringUtils.hasText(newPassword)) {
            newPassword = "123456";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private SysUser mapRequest(SysUser user, UserAdminRequest request) {
        user.setOrgName(request.getOrgName());
        user.setCreditCode(request.getCreditCode());
        user.setPhone(request.getPhone());
        user.setContactPerson(request.getContactPerson());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setQualificationFileId(request.getQualificationFileId());
        if (request.getRole() != null && request.getRole() != RoleType.ADMIN) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        return user;
    }

    private void validateUnique(String phone, String creditCode, Long excludeId) {
        userRepository.findByPhone(phone).ifPresent(u -> {
            if (excludeId == null || !u.getId().equals(excludeId)) {
                throw new BusinessException("手机号码已存在");
            }
        });
        userRepository.findByCreditCode(creditCode).ifPresent(u -> {
            if (excludeId == null || !u.getId().equals(excludeId)) {
                throw new BusinessException("统一社会信用代码已存在");
            }
        });
    }

    private SysUser getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BusinessException("用户不存在"));
    }
}
