package com.insa.TeamOpsSystem.user;


import com.insa.TeamOpsSystem.exceptions.AlreadyExistException;
import com.insa.TeamOpsSystem.exceptions.EntityNotFoundException;
import com.insa.TeamOpsSystem.role.Roles;
import com.insa.TeamOpsSystem.role.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.insa.TeamOpsSystem.jwt.until.Util.getNullPropertyNames;


@Service
@RequiredArgsConstructor
public class UserService {

    private final RolesRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public SystemUsers createTeamMembers(SystemUsers signupRequest) throws IllegalAccessException {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new AlreadyExistException("User name '" + signupRequest.getUsername() + "' is already exist");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new AlreadyExistException("Email '" + signupRequest.getEmail() + "' is already exist");
        }
        Set<Roles> strRoles = signupRequest.getRole();
        Set<Roles> roles = new HashSet<>();
        strRoles.forEach(role -> {
            Roles adminRole = roleRepository.findById(role.getId())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
        });
        signupRequest.setPassword(encoder.encode(signupRequest.getPassword()));
        signupRequest.setRole(roles);
        return userRepository.save(signupRequest);
    }

    public SystemUsers getTeamMembersById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(SystemUsers.class, "  Type with an id: " + id + " was not found!"));
    }

    public Page<SystemUsers> getAllTeamMembers(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public SystemUsers updateTeamMembers(long id, SystemUsers systemUsers, UsernamePasswordAuthenticationToken token) throws IllegalAccessException {
        var et = getTeamMembersById(id);
        systemUsers.setPassword(encoder.encode(systemUsers.getPassword()));
        BeanUtils.copyProperties(systemUsers, et, getNullPropertyNames(systemUsers));
        return userRepository.save(et);
    }

    public SystemUsers updatePassword(long id, PasswordRequest passwordRequest, UsernamePasswordAuthenticationToken token) throws IllegalAccessException {

        var et = getTeamMembersById(id);
        if (encoder.matches(passwordRequest.getOldPassword(), et.getPassword())) {
            et.setPassword(encoder.encode(passwordRequest.getPassword()));
            return userRepository.save(et);
        } else {
            throw new AlreadyExistException("Incorrect old password");
        }
    }


    public void deleteTeamMembers(long id, JwtAuthenticationToken token) {
        userRepository.deleteById(id);
    }


}
