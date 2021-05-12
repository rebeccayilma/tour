package com.example.tour.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService  {
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository){
        this.ratingRepository = ratingRepository;

    }
    public List<Rating> getRatings(Long activity_id){
        return ratingRepository.findAllByActivity_Id(activity_id);
    }
    public void addNewRating(Rating rating) {
        ratingRepository.save(rating);
    }


}
