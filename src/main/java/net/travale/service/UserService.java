package net.travale.service;


import net.travale.model.User;
import net.travale.repository.DbUserRepository;
import net.travale.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private DbUserRepository dbUserRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private EmailService emailService;

    public List<User> selectAllUsers(){
        return dbUserRepository.findAll();
    }

    public User createUser(User user){
         return dbUserRepository.save(user);
    }

    public void deleteUser(Long id){
        dbUserRepository.deleteById(id);

    }

    public boolean alreadyExists(String userName){
        Optional<User> first = dbUserRepository.findByUserName(userName).stream().findFirst();
        return (first.isPresent());
    }

    public Optional<User> findByUserName(String userName){
        return dbUserRepository.findByUserName(userName).stream().findFirst();
    }

    public Optional<User> findByPasswordResetToken(String token){
        return dbUserRepository.findByPasswordResetToken(token).stream().findFirst();
    }

    public Optional<User> findById(Long id){
       return dbUserRepository.findById(id);
    }


    @Autowired
    private PasswordEncoder passwordEncoder;

    public void setNewPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password)).setPasswordResetToken(null);
        dbUserRepository.save(user);
    }

    public void requestPasswordReset(User user) {
        String token = jwtTokenUtil.generatePasswordResetToken(user.getUserName());
        user.setPasswordResetToken(token);
        dbUserRepository.save(user);
        emailService.sendPasswordResetLink(user.getUserName(), token);
    }


}
