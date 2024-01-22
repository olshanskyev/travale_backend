package net.travale.controller;

import net.travale.model.ErrorResponse;
import net.travale.model.User;
import net.travale.model.UserInfo;
import net.travale.service.UserInfoService;
import net.travale.service.UserService;
import net.travale.utils.WebSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class UsersRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private WebSecurityUtils webSecurityUtils;

    @RequestMapping(value="/userInfo", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfo() throws Exception {
        String userName = webSecurityUtils.getAuthorizedUser().getUserName();
        Optional<UserInfo> found = userInfoService.findByUserName(userName);
        return (found.isPresent())?
            new ResponseEntity<>(found, HttpStatus.OK):
            new ResponseEntity<>(null, HttpStatus.OK);

    }

    @RequestMapping(value="/management/users/{userName}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserByName(@PathVariable String userName){

        Optional<User> found = userService.findByUserName(userName);
        if (found.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("userNotFound", new HashMap<>() {{
                put("user", userName);
            }}), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(found, HttpStatus.OK);

    }
}
