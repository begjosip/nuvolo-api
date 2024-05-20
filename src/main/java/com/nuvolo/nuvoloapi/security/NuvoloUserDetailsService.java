package com.nuvolo.nuvoloapi.security;

import com.nuvolo.nuvoloapi.exceptions.UserVerificationException;
import com.nuvolo.nuvoloapi.model.entity.NuvoloUser;
import com.nuvolo.nuvoloapi.model.entity.Role;
import com.nuvolo.nuvoloapi.repository.NuvoloUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NuvoloUserDetailsService implements UserDetailsService {

    private final NuvoloUserRepository nuvoloUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        NuvoloUser user = nuvoloUserRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with %s email does not exist.", email)));
        if (Boolean.FALSE.equals(user.getIsEnabled())) {
            throw new UserVerificationException("User is not verified. Check your email.");
        }
        return new User(user.getEmail(), user.getPassword(), this.mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name())).collect(Collectors.toList());
    }
}
