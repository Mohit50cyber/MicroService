package com.ratingservice.ratingservice.services;

import com.ratingservice.ratingservice.entities.Rating;

import java.util.List;

public interface RatingService {

    Rating create(Rating rating);
    List<Rating> getRatings();
    List<Rating> getRatingByUserId(String userId);
    List<Rating> getRatingsByHotelId(String hotelId);

}
