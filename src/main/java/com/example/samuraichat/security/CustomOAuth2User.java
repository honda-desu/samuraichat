package com.example.samuraichat.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.example.samuraichat.entity.User;

public class CustomOAuth2User implements OidcUser {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;
    private final OidcUser oidcUser;

    public CustomOAuth2User(User user,
                            Collection<? extends GrantedAuthority> authorities,
                            OidcUser oidcUser) {
        this.user = user;
        this.authorities = authorities;
        this.oidcUser = oidcUser;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return user.getName();
    }
}