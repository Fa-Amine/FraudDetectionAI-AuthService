package com.springdata.frauddetectioniaauthservice.Services;

import com.springdata.frauddetectioniaauthservice.Entities.User;
import com.springdata.frauddetectioniaauthservice.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Autowired
    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void  registerUser(User user) {
         userRepository.save(user);
    }
}
