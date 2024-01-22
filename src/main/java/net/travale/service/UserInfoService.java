package net.travale.service;


import net.travale.model.User;
import net.travale.model.UserInfo;
import net.travale.repository.DbUserInfoRepository;
import net.travale.repository.DbUserRepository;
import net.travale.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {

    @Autowired
    private DbUserInfoRepository dbUserInfoRepository;

    public Optional<UserInfo> findByUserName(String userName) {
        return dbUserInfoRepository.findByUserUserName(userName);
    }


}
