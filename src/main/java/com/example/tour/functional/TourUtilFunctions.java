package com.example.tour.functional;

import com.example.tour.activity.Activity;
import com.example.tour.authentication.model.User;
import com.example.tour.place.Place;
import com.example.tour.rating.Rating;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TourUtilFunctions {

    public static final Comparator<List<Activity>> byListLength = (l1, l2) -> l1.size() - l2.size();
    public static final Function<List<Place>, List<Activity>> activitiesFromPlaces = places ->
            places.stream()
                .flatMap(p -> p.getActivities().stream())
                .collect(Collectors.toList());

    public static final Function<List<Place>, Optional<String>> contributorWithMoreProposedActivities = places ->
            activitiesFromPlaces.apply(places).stream()
                .collect(Collectors.groupingBy(a -> a.getProposedBy())).entrySet().stream()
                .max((e1, e2) -> byListLength.compare(e1.getValue(), e2.getValue()))
                .map(entry -> entry.getKey().getUsername());


    public static final BiPredicate<Activity, User> approvedByUser = (activity, user) ->
            activity.getApprovedBy() != null && activity.getApprovedBy().equals(user);
    public static final BiFunction<List<Place>, User, Long> activitiesApprovedByAdminInPlaces = (places, admin) ->
            activitiesFromPlaces.apply(places).stream()
                .filter(a -> approvedByUser.test(a, admin))
                .count();

    public static final BiPredicate<Activity, LocalDate> ratedBefore = (activity, date) ->
            List.of(activity).stream()
                .flatMap(a -> a.getRatings().stream())
                .anyMatch(r -> r.getDate().isBefore(date));
    public static final BiFunction<List<Place>, LocalDate, List<Activity>> activitiesRatedBefore = (places, date) ->
            activitiesFromPlaces.apply(places).stream()
                .filter(a -> ratedBefore.test(a, date))
                .collect(Collectors.toList());

    public static final Function<User, List<Rating>> ratingsFromContrib =
            contrib -> List.of(contrib).stream()
                .flatMap(c -> c.getRatings().stream())
                .collect(Collectors.toList());
    public static final BiPredicate<Rating, Integer> scoreHigherThan = (r, baseScore) -> r.getScore() > baseScore;
    public static final Predicate<Place> isInSouthHemisphere = p -> p.getLatitude() < 0;
    public static final BiFunction<User, Integer, List<String>> placeNamesWithContribHighRatingsInSouthHemisphere =
            (contrib, baseScore) -> ratingsFromContrib.apply(contrib).stream()
                .filter(r -> scoreHigherThan.test(r, baseScore))
                .map(r -> r.getActivity())
                .map(activity -> activity.getPlace())
                .distinct()
                .filter(isInSouthHemisphere)
                .map(p -> p.getName())
                .collect(Collectors.toList());
}