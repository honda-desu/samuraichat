package com.example.samuraichat.security;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.example.samuraichat.entity.Role;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.repository.UserRepository;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest request) {
        OidcUser oidcUser = super.loadUser(request);

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setEnabled(true);
            
         // ★ 追加：furigana を仮で name と同じにする
            user.setFurigana(name);
         // ★ password の仮値（NOT NULL 対策）
            user.setPassword("GOOGLE_LOGIN_USER");



            Role role = new Role();
            role.setId(1);
            role.setName("ROLE_GENERAL");
            user.setRole(role);

            userRepository.save(user);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());

        return new CustomOAuth2User(user, Collections.singleton(authority), oidcUser);
    }
}