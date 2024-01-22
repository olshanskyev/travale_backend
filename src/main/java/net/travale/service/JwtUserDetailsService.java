package net.travale.service;


import net.travale.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;


    @Override
    public ExtendedUserDetails loadUserByUsername(String userName){

        Optional<User> user = userService.findByUserName(userName);

        return new ExtendedUserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> list = new ArrayList<>();
                user.ifPresent(value -> value.getRoles().forEach(item -> list.add((GrantedAuthority) item::toString)));
                return list;
            }

            @Override
            public String getPassword() {
                return user.map(User::getPassword).orElse(null);
            }

            @Override
            public User getUser() { return user.orElse(null);}

            @Override
            public String getUsername() {
                return user.map(User::getUserName).orElse(null);
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

}
