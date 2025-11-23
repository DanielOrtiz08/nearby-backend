package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.Reply;
import com.edu.unimagdalena.nearby.entities.Review;
import com.edu.unimagdalena.nearby.entities.Reporte;

import java.util.List;
import java.util.Map;

public interface ReviewsService {
    Review createReview(Map<String, Object> payload);
    List<Review> reviewsByProperty(String propertyId);
    List<Review> reviewsByUser(String userId);
    Review updateReview(String id, Map<String, Object> payload);
    void deleteReview(String id);
    Reply replyToReview(String reviewId, Map<String, Object> payload);
    Reporte reportReview(String reviewId, Map<String, Object> payload);
}
