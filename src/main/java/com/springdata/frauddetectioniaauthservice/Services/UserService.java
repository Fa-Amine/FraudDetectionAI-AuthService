package com.springdata.frauddetectioniaauthservice.Services;


import com.springdata.frauddetectioniaauthservice.Entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public interface UserService {

    public void  registerUser(User user);
}
