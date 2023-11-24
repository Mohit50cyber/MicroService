package com.userservice.userservice.services;

import com.userservice.userservice.entities.Hotel;
import com.userservice.userservice.entities.Rating;
import com.userservice.userservice.entities.User;
import com.userservice.userservice.exception.ResourceNotFoundException;
import com.userservice.userservice.externalservices.HotelService;
import com.userservice.userservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        //generate userId
       String randomUserId = UUID.randomUUID().toString();
       user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {

        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        //get user from database with the help of user repository
        User user= userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User with given " +
                        "id is not found on server " + userId));
        //fetch rating of the above user from rating service
        //http://localhost:8083/rating/users/40332090-99e8-48ea-a5a1-c2ce51fd0128
        Rating[] ratingsOfUser = restTemplate.getForObject("http://RATING-SERVICE/rating/users/" + user.getUserId(), Rating[].class);
        logger.info("{}",ratingsOfUser);
        List<Rating> ratings= Arrays.stream(ratingsOfUser).toList();

        List<Rating> ratingList=ratings.stream().map(rating->{
            //api call to hotel service to get the hotel

            Hotel hotel = hotelService.getHotel(rating.getHotelId());
            //logger.info("response status code {}",forEntity.getStatusCode());
            //set the hotel to rating
            rating.setHotel(hotel);
            //return the rating
            return rating;
        }).collect(Collectors.toList());
        user.setRatings(ratingList);
        return user;
    }
}
