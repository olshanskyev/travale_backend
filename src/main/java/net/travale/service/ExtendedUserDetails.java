package net.travale.service;

import net.travale.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface ExtendedUserDetails extends UserDetails {
    User getUser();
}
