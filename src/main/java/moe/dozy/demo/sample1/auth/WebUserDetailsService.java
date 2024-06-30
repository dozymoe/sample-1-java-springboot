package moe.dozy.demo.sample1.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.services.UserService;

@Service
public class WebUserDetailsService implements UserDetailsService {

    @Autowired
    private ApplicationContext appContext;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userService = new UserService(appContext);
        User userObj = userService.findByName(username);
        if (userObj != null) {
            return new WebUserDetails(userObj);
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
