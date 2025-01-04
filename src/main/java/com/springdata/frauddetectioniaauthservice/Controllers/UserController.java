package com.springdata.frauddetectioniaauthservice.Controllers;

import com.springdata.frauddetectioniaauthservice.Config.JwtProvider;
import com.springdata.frauddetectioniaauthservice.Entities.User;
import com.springdata.frauddetectioniaauthservice.Repositories.UserRepository;
import com.springdata.frauddetectioniaauthservice.Request.LoginRequest;
import com.springdata.frauddetectioniaauthservice.Response.AuthResponse;
import com.springdata.frauddetectioniaauthservice.Services.CustomerUserServiceImplementation;
import com.springdata.frauddetectioniaauthservice.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/Auth")
public class UserController {



    private UserRepository userRepository;


    private PasswordEncoder passwordEncoder;


    private CustomerUserServiceImplementation customUserDetails;

    @Autowired
    public UserController(UserRepository userRepository,PasswordEncoder passwordEncoder,CustomerUserServiceImplementation customUserDetails) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetails = customUserDetails;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user
    ) {

        String email = user.getEmail();
        String password = user.getPassword();

        User isEmailExist = userRepository.findByEmail(email);

        if (isEmailExist != null) {

            return new ResponseEntity<>("Email is already registered!", HttpStatus.CONFLICT);
        }

        User newUser = new User();

        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));

        userRepository.save(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email , password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();

        authResponse.setJwt(token);
        authResponse.setMessage("Registered Successfully");
        authResponse.setStatus(true);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {

        AuthResponse authResponse = new AuthResponse();

        try {
            String username = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            System.out.println(username + " ------- " + password);

            Authentication authentication = authenticate(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = JwtProvider.generateToken(authentication);

            authResponse.setMessage("Bibish!");
            authResponse.setJwt(token);
            authResponse.setStatus(true);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();
            authResponse.setMessage("no Bibish "+ e.getMessage());
            authResponse.setJwt(null);
            authResponse.setStatus(false);

            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }


    }

    //authenticate methode to check user and motdepasse
    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        System.out.println("Sign in userDetails - " +userDetails);

        if(userDetails == null) {
            System.out.println("Sign in UserDetails - null " + userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("sign in userDetails - password not match " +userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null , userDetails.getAuthorities());


    }



}
