package databases;


import actor.ActorsAwards;
import entertainment.Season;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public final class MyDatabase {
    private final ArrayList<ActorData> actors = new ArrayList<>();
    private final ArrayList<MovieData> movies = new ArrayList<>();
    private final ArrayList<SerialData> serials = new ArrayList<>();
    private final ArrayList<UserData> users = new ArrayList<>();

    /**
     *
     * @param actor to be added to db
     */
    public void add(final ActorData actor) {
        actors.add(actor);
    }

    /**
     *
     * @param movie to be added to db
     */
    public void add(final MovieData movie) {
        movies.add(movie);
    }

    /**
     *
     * @param serial to be added to db
     */
    public void add(final SerialData serial) {
        serials.add(serial);
    }

    /**
     *
     * @param user to be added to db
     */
    public void add(final UserData user) {
        users.add(user);
    }

    /**
     *
     * @param userName user to be searched in the db
     * @return user if found, else null
     */
    public UserData getUser(final String userName) {
        for (UserData user : this.getUsers()) {
            if (user.getUsername().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    /**
     *
     * @param title Title of the movie we are searching
     * @return movie if found, else null
     */
    public MovieData searchMovie(final String title) {
        for (MovieData movie : this.getMovies()) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

    /**
     *
     * @param title Title of the serial we are searching
     * @return serial if found, else null
     */
    public SerialData searchSerial(final String title) {
        for (SerialData serial : this.getSerials()) {
            if (serial.getTitle().equals(title)) {
                return serial;
            }
        }
        return null;
    }

    /**
     *
     * @param user to which we update favorite list
     * @param title Video that we will add to list
     * @return message success/fail to be written in JSONArray at output
     */
    public String view(final UserData user, final String title) {
        if (user != null) {
            if (user.getHistory().containsKey(title)) {
                user.getHistory().put(title, user.getHistory().get(title) + 1);
            } else {
                user.getHistory().put(title, 1);
            }
            return "success -> " + title + " was viewed with total views of "
                    + user.getHistory().get(title);
        }
        return "FATAL ERROR!";
    }

    /**
     *
     * @param user to which we update favorite list
     * @param title Video that we will add to list
     * @return message success/fail to be written in JSONArray at output
     */
    public String addFavorite(final UserData user, final String title) {
        if (user != null) {
            if (user.getHistory().containsKey(title)) {
                if (user.getFavoriteMovies().contains(title)) {
                    return "error -> " + title + " is already in favourite list";
                } else {
                    user.getFavoriteMovies().add(title);
                    return "success -> " + title + " was added as favourite";
                }
            } else {
                return "error -> " + title + " is not seen";
            }
        }
        return "FATAL ERROR!";
    }

    /**
     * Add new grade to a title
     * Applies to Movies only
     * @param user user that gives a grade
     * @param movie movie that is graded
     * @param grade value that is applied
     * @return message success/fail to be written in JSONArray at output
     */
    public String rate(final UserData user, final MovieData movie, final double grade) {
        if (user.getHistory().containsKey(movie.getTitle())) {
            if (movie.getRatings().containsKey(user.getUsername())) {
                return "error -> " + movie.getTitle() + " has been already rated";
            } else {
                movie.getRatings().put(user.getUsername(), grade);
                return "success -> " + movie.getTitle() + " was rated with " + grade + " by "
                        + user.getUsername();
            }
        } else {
            return "error -> " + movie.getTitle() + " is not seen";
        }
    }

    /**
     * Add new grade to a title
     * Applies to Serials only
     * @param user user that gives a grade
     * @param serial serial that is graded
     * @param seasonNumber season of the serial that is graded
     * @param grade value that is applied
     * @return message success/fail to be written in JSONArray at output
     */
    public String rate(final UserData user, final SerialData serial, final int seasonNumber,
                       final double grade) {
        if (user.getHistory().containsKey(serial.getTitle())) {
            Season season = serial.getSeasons().get(seasonNumber - 1);
            if (season.getRatings().containsKey(user.getUsername())) {
                return "error -> " + serial.getTitle() + " has been already rated";
            } else {
                season.getRatings().put(user.getUsername(), grade);
                return "success -> " + serial.getTitle() + " was rated with " + grade + " by "
                        + user.getUsername();
            }
        } else {
            return "error -> " + serial.getTitle() + " is not seen";
        }

    }

    /**
     * Used to filter movies by year and/or genres
     * @param list list of movies to be filtered
     * @param filters filters to be applied(only indexes 0(year) and 1(genres) will be used.
     */
    public void filterMovies(final ArrayList<MovieData> list,
                              final List<List<String>> filters) {
        if (filters.get(0).get(0) != null) {
            int year = Integer.parseInt(filters.get(0).get(0));
            list.removeIf(e -> e.getYear() != year);
        }
        if (filters.get(1).get(0) != null) {
            for (String currentGenre : filters.get(1)) {
                list.removeIf(e -> !e.getGenres().contains(currentGenre));
            }
        }
    }

    /**
     * Used to filter serials by year and/or genres
     * @param list list of serials to be filtered
     * @param filters filters to be applied(only indexes 0(year) and 1(genres) will be used.
     */
    public void filterSerials(final ArrayList<SerialData> list,
                              final List<List<String>> filters) {
        if (filters.get(0).get(0) != null) {
            int year = Integer.parseInt(filters.get(0).get(0));
            list.removeIf(e -> e.getYear() != year);
        }
        if (filters.get(1).get(0) != null) {
            for (String currentGenre : filters.get(1)) {
                list.removeIf(e -> !e.getGenres().contains(currentGenre));
            }
        }
    }

    /**
     * Used for queries on Favorite Movies
     * @param filters filters to be applied on query
     * @param sortType asc/desc
     * @param n number of maximum elements expected at output
     * @return output text
     */
    public StringBuilder queryFavoriteMovies(final List<List<String>> filters,
                                             final String sortType, final int n) {
        ArrayList<MovieData> result = (ArrayList<MovieData>) this.movies.clone();
        getFavoriteRankingMovies(result);
        filterMovies(result, filters);
        result.removeIf(e -> e.getAddedToFavorite() == 0);

        result.sort(new Comparators.SortByTitle());
        result.sort(new Comparators.SortByFavorite());

        if (sortType.equals("desc")) {
            result = reverseOrderMovies(result);
        }


        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getTitle());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;
    }


    /**
     * Used for queries based on ratings for movies
     * @param filters filters to be applied during the query
     * @param sortType asc/desc
     * @param n maximum number of elements expected as output
     * @return text result
     */
    public StringBuilder queryRatingMovies(final List<List<String>> filters,
                                              final String sortType, final int n) {
        ArrayList<MovieData> result = (ArrayList<MovieData>) this.movies.clone();
        filterMovies(result, filters);
        getRatingMovies(result);

        result.removeIf(e -> e.getOverallRating() == 0);

        result.sort(new Comparators.SortByTitle());
        result.sort(new Comparators.SortByFinalRating());

        if (sortType.equals("desc")) {
            result = reverseOrderMovies(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getTitle());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;
    }

    /**
     * Used for queries based on ratings for serials
     * @param filters filters to be applied during the query
     * @param sortType asc/desc
     * @param n maximum number of elements expected as output
     * @return text result
     */
    public StringBuilder queryRatingSerials(final List<List<String>> filters,
                                           final String sortType, final int n) {
        ArrayList<SerialData> result = (ArrayList<SerialData>) this.serials.clone();
        filterSerials(result, filters);
        getRatingSerials(result);

        result.removeIf(e -> e.getOverallRating() == 0);

        result.sort(new Comparators.SortByTitle());
        result.sort(new Comparators.SortByFinalRating());

        if (sortType.equals("desc")) {
            result = reverseOrderSerials(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getTitle());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;
    }

    /**
     * Used for queries on Favorite Shows
     * @param filters filters to be applied on query
     * @param sortType asc/desc
     * @param n number of maximum elements expected at output
     * @return output text
     */
    public StringBuilder queryFavoriteSerials(final List<List<String>> filters,
                                              final String sortType, final int n) {
        ArrayList<SerialData> result = (ArrayList<SerialData>) this.serials.clone();
        filterSerials(result, filters);
        getFavoriteRankingSerials(result);

        result.removeIf(e -> e.getAddedToFavorite() == 0);

        result.sort(new Comparators.SortByTitle());
        result.sort(new Comparators.SortByFavorite());

        if (sortType.equals("desc")) {
            result = reverseOrderSerials(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getTitle());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;
    }

    /**
     * Used on queries for longest movies
     * @param filters filters to be applied on query
     * @param sortType asc/desc
     * @param n number of maximum elements expected at output
     * @return output text
     */
    public StringBuilder queryLongestMovies(final List<List<String>> filters,
                                            final String sortType, final int n) {
        ArrayList<MovieData> result = (ArrayList<MovieData>) this.movies.clone();
        filterMovies(result, filters);

        result.sort(new Comparators.SortByTitle());
        result.sort(new Comparators.SortByDurationMovie());

        if (sortType.equals("desc")) {
            result = reverseOrderMovies(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getTitle());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;
    }

    /**
     * Used on queries for longest serials
     * @param filters filters to be applied on query
     * @param sortType asc/desc
     * @param n number of maximum elements expected at output
     * @return output text
     */
    public StringBuilder queryLongestSerials(final List<List<String>> filters,
                                            final String sortType, final int n) {
        ArrayList<SerialData> result = (ArrayList<SerialData>) this.serials.clone();
        filterSerials(result, filters);
        getDurationSerials(result);

        result.sort(new Comparators.SortByTitle());
        result.sort(new Comparators.SortByDurationSerial());

        if (sortType.equals("desc")) {
            result = reverseOrderSerials(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getTitle());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;
    }

    /**
     * Used on query for most viewed movies
     * @param filters filters to be applied on query
     * @param sortType asc/desc
     * @param n number of maximum elements expected at output
     * @return output text
     */
    public StringBuilder queryMostViewedMovies(final List<List<String>> filters,
                                               final String sortType, final int n) {
        ArrayList<MovieData> result = (ArrayList<MovieData>) this.movies.clone();
        filterMovies(result, filters);
        getViewedRankingMovies(result);
        result.removeIf(e -> e.getViewed() == 0);

        result.sort(new Comparators.SortByTitle());
        result.sort(new Comparators.SortByViewedMovie());

        if (sortType.equals("desc")) {
            result = reverseOrderMovies(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getTitle());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;

    }

    /**
     * Used on query for most viewed serials
     * @param filters filters to be applied on query
     * @param sortType asc/desc
     * @param n number of maximum elements expected at output
     * @return output text
     */
    public StringBuilder queryMostViewedSerials(final List<List<String>> filters,
                                               final String sortType, final int n) {
        ArrayList<SerialData> result = (ArrayList<SerialData>) this.serials.clone();
        filterSerials(result, filters);
        getViewedRankingSerials(result);
        result.removeIf(e -> e.getViewed() == 0);

        result.sort(new Comparators.SortByViewedSerial());

        if (sortType.equals("desc")) {
            result = reverseOrderSerials(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getTitle());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;

    }


    /**
     * Used on query for filtering description of an actor
     * @param filters filters to be applied on query
     * @param sortType asc/desc
     * @return output text
     */
    public StringBuilder queryDescriptionActors(final List<List<String>> filters,
                                                final String sortType) {
        ArrayList<ActorData> result = (ArrayList<ActorData>) this.actors.clone();
        for (String keyword : filters.get(2)) {
            result.removeIf(currentActor -> !currentActor.getCareerDescription().toLowerCase()
                    .contains(keyword.toLowerCase()));
        }

        result.sort(new Comparators.SortByName());

        if (sortType.equals("desc")) {
            reverseOrderActors(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            outText.append(result.get(i).getName());
            if (i < result.size() - 1) {
                outText.append(", ");
            }
        }
        return outText;
    }

    /**
     * Counts the number of total awards for each actor
     * @param list list of actors
     */
    private void getNumberOfAwardsActor(final ArrayList<ActorData> list) {
        int numberOfAwards;
        for (ActorData currentActor : list) {
            numberOfAwards = 0;
            for (Map.Entry<ActorsAwards, Integer> entry : currentActor.getAwards().entrySet()) {
                numberOfAwards += entry.getValue();
            }
            currentActor.setNumberOfAwards(numberOfAwards);
        }
    }

    /**
     * Used on query for awards of actors
     * @param filters filters to be applied on query
     * @param sortType asc/desc
     * @return output text
     */
    public StringBuilder queryAwardsActors(final List<List<String>> filters,
                                                final String sortType) {
        ArrayList<ActorData> result = (ArrayList<ActorData>) this.actors.clone();
        for (String award : filters.get(3)) {
            result.removeIf(e -> !(e.getAwards().containsKey(award)));
        }

        getNumberOfAwardsActor(result);
        result.sort(new Comparators.SortByName());
        result.sort(new Comparators.SortByNumberOfAwards());

        if (sortType.equals("desc")) {
            result = reverseOrderActors(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            outText.append(result.get(i).getName());
            if (i < result.size() - 1) {
                outText.append(", ");
            }
        }
        return outText;
    }

    /**
     * Used on query for ratings of each actor
     * @param sortType asc/desc
     * @param n number of maximum elements expected as output
     * @return output text
     */
    public StringBuilder queryAverageActors(final String sortType, final int n) {
        ArrayList<ActorData> result = (ArrayList<ActorData>) this.actors.clone();


        getActorsRating(result);

        result.removeIf(e -> e.getRating() == 0);

        result.sort(new Comparators.SortByName());
        result.sort(new Comparators.SortByRatingActor());


        if (sortType.equals("desc")) {
            result = reverseOrderActors(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < n & i < result.size(); i++) {
            outText.append(result.get(i).getName());
            if (i < n - 1 & i < result.size() - 1) {
                outText.append(", ");
            }
        }

        return outText;
    }

    /**
     * Used on query for most active users, based on their ratings
     * @param sortType asc/desc
     * @param n number of maximum elements expected as output
     * @return output text
     */
    public StringBuilder queryUsers(final String sortType, final int n) {
        ArrayList<UserData> result = (ArrayList<UserData>) this.users.clone();
        getNumberOfReviewsUsers(result);
        result.removeIf(e -> e.getNumberOfReviews() == 0);

        result.sort(new Comparators.SortByUsername());
        result.sort(new Comparators.SortByNumberOfReviewsActors());


        if (sortType.equals("desc")) {
            result = reverseOrderUsers(result);
        }

        StringBuilder outText = new StringBuilder();
        for (int i = 0; i < result.size() && i < n; i++) {
            outText.append(result.get(i).getUsername());
            if (i < result.size() - 1) {
                outText.append(", ");
            }
        }
        return outText;
    }

    /**
     * Used to get Standard Recommendation
     * @param user user that requests the query
     * @return output text
     */
    public String getStandardRecommendation(final UserData user) {
        for (MovieData currentMovie : this.movies) {
            if (!user.getHistory().containsKey(currentMovie.getTitle())) {
                return "StandardRecommendation result: " + currentMovie.getTitle();
            }
        }
        for (SerialData currentSerial : this.serials) {
            if (!user.getHistory().containsKey(currentSerial.getTitle())) {
                return "StandardRecommendation result: " + currentSerial.getTitle();
            }
        }
        return "StandardRecommendation cannot be applied!";
    }

    /**
     * Used to get Best Unseen Recommendation
     * @param user user that requests the query
     * @return output text
     */
    public String getBestUnseen(final UserData user) {
        ArrayList<SerialData> copyOfSerials = (ArrayList<SerialData>) this.serials.clone();
        ArrayList<MovieData> copyOfMovies = (ArrayList<MovieData>) this.movies.clone();

        getRatingSerials(copyOfSerials);
        getRatingMovies(copyOfMovies);

        copyOfSerials.sort(new Comparators.SortByFinalRating());
        copyOfMovies.sort(new Comparators.SortByFinalRating());

        for (MovieData currentMovie : copyOfMovies) {
            if (!user.getHistory().containsKey(currentMovie.getTitle())) {
                return "BestRatedUnseenRecommendation result: " + currentMovie.getTitle();
            }
        }
        for (SerialData currentSerial : copyOfSerials) {
            if (!user.getHistory().containsKey(currentSerial.getTitle())) {
                return "BestRatedUnseenRecommendation result: " + currentSerial.getTitle();
            }
        }
        return "BestRatedUnseenRecommendation cannot be applied!";
    }

    /**
     * Used to get Popular Recommendation(not working)
     * @param user user that requests the query
     * @return output text
     */
    public StringBuilder getPopularRecommendation(final UserData user) {
        StringBuilder output = new StringBuilder();

        if (user.getSubscriptionType().equals("PREMIUM")) {
            output.append("PopularRecommendation is not ready yet :(");
        } else {
            output.append("PopularRecommendation cannot be applied!");
        }


        return output;
    }


    /**
     * Used to get Favorite Recommendation
     * @param user user that requests the query
     * @return output text
     */
    public String getMostFavorite(final UserData user) {
        if (user.getSubscriptionType().equals("PREMIUM")) {
            ArrayList<SerialData> copyOfSerials = (ArrayList<SerialData>) this.serials.clone();
            ArrayList<MovieData> copyOfMovies = (ArrayList<MovieData>) this.movies.clone();

            getFavoriteRankingSerials(copyOfSerials);
            getFavoriteRankingMovies(copyOfMovies);

            copyOfSerials.sort(new Comparators.SortByFavorite());
            copyOfMovies.sort(new Comparators.SortByFavorite());

            for (MovieData currentMovie : copyOfMovies) {
                if (!user.getHistory().containsKey(currentMovie.getTitle())) {
                    return "FavoriteRecommendation result: " + currentMovie.getTitle();
                }
            }
            for (SerialData currentSerial : copyOfSerials) {
                if (!user.getHistory().containsKey(currentSerial.getTitle())) {
                    return "FavoriteRecommendation result: " + currentSerial.getTitle();
                }
            }
        }
        return "FavoriteRecommendation cannot be applied!";
    }

    /**
     * Used to get Search Recommendation
     * @param user user that requests the query
     * @return output text
     */
    public StringBuilder getSearch(final UserData user, final String genre) {
        StringBuilder output = new StringBuilder();
        if (user.getSubscriptionType().equals("PREMIUM")) {
            ArrayList<SerialData> copyOfSerials = (ArrayList<SerialData>) this.serials.clone();
            ArrayList<MovieData> copyOfMovies = (ArrayList<MovieData>) this.movies.clone();

            getRatingSerials(copyOfSerials);
            getRatingMovies(copyOfMovies);

            copyOfMovies.removeIf(e -> !e.getGenres().contains(genre));
            copyOfSerials.removeIf(e -> !e.getGenres().contains(genre));

            copyOfSerials.sort(new Comparators.SortByTitle());
            copyOfMovies.sort(new Comparators.SortByTitle());
            copyOfSerials.sort(new Comparators.SortByFinalRating());
            copyOfMovies.sort(new Comparators.SortByFinalRating());

            if (copyOfMovies.size() == 0 && copyOfSerials.size() == 0) {
                output.append("SearchRecommendation cannot be applied!");
            } else {
                output.append("SearchRecommendation result: [");

                for (MovieData currentMovie : copyOfMovies) {
                    if (!user.getHistory().containsKey(currentMovie.getTitle())) {
                        output.append(currentMovie.getTitle());
                    }
                    if (copyOfMovies.indexOf(currentMovie) < copyOfMovies.size() - 1) {
                        output.append(", ");
                    }
                }
                for (SerialData currentSerial : copyOfSerials) {
                    if (!user.getHistory().containsKey(currentSerial.getTitle())) {
                        output.append(currentSerial.getTitle());
                    }
                    if (copyOfSerials.indexOf(currentSerial) < copyOfSerials.size() - 1) {
                        output.append(", ");
                    }
                }
                output.append("]");
            }
        } else {
            output.append("SearchRecommendation cannot be applied!");
        }

        return output;
    }

    /**
     * Generate rating for each actor given in list
     * @param list list of actors
     */
    private void getActorsRating(final ArrayList<ActorData> list) {
        double numberOfVideos;
        double rating;
        ArrayList<SerialData> copyOfSerials = (ArrayList<SerialData>) this.serials.clone();
        ArrayList<MovieData> copyOfMovies = (ArrayList<MovieData>) this.movies.clone();


        getRatingSerials(copyOfSerials);
        getRatingMovies(copyOfMovies);

        for (ActorData currentActor : list) {
            numberOfVideos = 0;
            rating = 0;
            for (SerialData currentSerial : copyOfSerials) {
                if (currentSerial.getCast().contains(currentActor.getName())) {
                    if (currentSerial.getOverallRating() != 0) {
                        rating += currentSerial.getOverallRating();
                        numberOfVideos = numberOfVideos + 1;
                    }
                }
            }

            for (MovieData currentMovie : copyOfMovies) {
                if (currentMovie.getCast().contains(currentActor.getName())) {
                    if (currentMovie.getOverallRating() != 0) {
                        rating += currentMovie.getOverallRating();
                        numberOfVideos = numberOfVideos + 1;
                    }
                }
            }
            if (numberOfVideos != 0) {
                rating /= numberOfVideos;
                currentActor.setRating(rating);
            }

        }
    }

    /**
     * Generate number of reviews for each user in list
     * @param list list of users
     */
    private void getNumberOfReviewsUsers(final ArrayList<UserData> list) {
        for (UserData currentUser : list) {
            currentUser.setNumberOfReviews(0);
            for (MovieData currentMovie : this.movies) {
                if (currentMovie.getRatings().containsKey(currentUser.getUsername())) {
                    currentUser.setNumberOfReviews(currentUser.getNumberOfReviews() + 1);
                }
            }

            for (SerialData currentSerial : this.serials) {
                for (Season currentSeason : currentSerial.getSeasons()) {
                    if (currentSeason.getRatings().containsKey(currentUser.getUsername())) {
                        currentUser.setNumberOfReviews(currentUser.getNumberOfReviews() + 1);
                    }
                }
            }
        }
    }


    /**
     * Generate duration for serials in list
     * @param list list of serials
     */
    private void getDurationSerials(final ArrayList<SerialData> list) {
        for (SerialData currentSerial : list) {
            currentSerial.setDuration(0);
            for (Season currentSeason : currentSerial.getSeasons()) {
                currentSerial.setDuration(currentSerial.getDuration()
                        + currentSeason.getDuration());
            }
        }
    }

    /**
     * Computes viewed for each movie
     * @param list Movies in DB
     */
    private void getViewedRankingMovies(final ArrayList<MovieData> list) {
        for (MovieData currentMovie : list) {
            currentMovie.setViewed(0);
            for (UserData currentUser : this.users) {
                if (currentUser.getHistory().containsKey(currentMovie.getTitle())) {
                    currentMovie.setViewed(currentMovie.getViewed() + 1);
                }
            }
        }
    }

    /**
     * Computes viewed for each serial
     * @param list Movies in DB
     */
    private void getViewedRankingSerials(final ArrayList<SerialData> list) {
        for (SerialData currentSerial : list) {
            currentSerial.setViewed(0);
            for (UserData currentUser : this.users) {
                if (currentUser.getHistory().containsKey(currentSerial.getTitle())) {
                    currentSerial.setViewed(currentSerial.getViewed() + 1);
                }
            }
        }
    }


    /**
     * Computes addedToFavorite for each movie
     * @param list Movies in DB
     */
   private void getFavoriteRankingMovies(final ArrayList<MovieData> list) {
        for (MovieData currentMovie : list) {
            for (UserData currentUser : this.users) {
                if (currentUser.getFavoriteMovies().contains(currentMovie.getTitle())) {
                    currentMovie.setAddedToFavorite(currentMovie.getAddedToFavorite() + 1);
                }
            }
        }
    }


    /**
     * Computes addedToFavorite for each Serial
     * @param list Serials in DB
     */
    private void getFavoriteRankingSerials(final ArrayList<SerialData> list) {
        for (SerialData currentSerial : list) {
            for (UserData currentUser : this.users) {
                if (currentUser.getFavoriteMovies().contains(currentSerial.getTitle())) {
                    currentSerial.setAddedToFavorite(currentSerial.getAddedToFavorite() + 1);
                }
            }
        }
    }

    /**
     * Used to reverse order of actors in ArrayList
     * @param list list of actors
     * @return reversed list
     */
    private ArrayList<ActorData> reverseOrderActors(final ArrayList<ActorData> list) {
        ArrayList<ActorData> result = new ArrayList<>();
        for (int i = list.size() - 1;  i >= 0; i--) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * Used to reverse order of movies in ArrayList
     * @param list list of Movies
     * @return reversed list
     */
    private ArrayList<MovieData> reverseOrderMovies(final ArrayList<MovieData> list) {
        ArrayList<MovieData> result = new ArrayList<>();
        for (int i = list.size() - 1;  i >= 0; i--) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * Used to reverse order of serials in ArrayList
     * @param list list of Serials
     * @return reversed list
     */
    private ArrayList<SerialData> reverseOrderSerials(final ArrayList<SerialData> list) {
        ArrayList<SerialData> result = new ArrayList<>();
        for (int i = list.size() - 1;  i >= 0; i--) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * Used to reverse order of users in ArrayList
     * @param list list of Serials
     * @return reversed list
     */
    private ArrayList<UserData> reverseOrderUsers(final ArrayList<UserData> list) {
        ArrayList<UserData> result = new ArrayList<>();
        for (int i = list.size() - 1;  i >= 0; i--) {
            result.add(list.get(i));
        }
        return result;
    }

    private void getRatingMovies(final ArrayList<MovieData> list) {
        double rating = 0;
        for (MovieData currentMovie : list) {
            for (Map.Entry<String, Double> currentRating : currentMovie.getRatings().entrySet()) {
                rating += currentRating.getValue();
            }

            if (currentMovie.getRatings().size() != 0) {
                currentMovie.setOverallRating(rating / currentMovie.getRatings().size());
            }
        }
    }

    private void getRatingSerials(final ArrayList<SerialData> list) {
        double rating;
        for (SerialData currentSerial : list) {
            currentSerial.setOverallRating(0);
            for (Season currentSeason : currentSerial.getSeasons()) {
                rating = 0;
                for (Map.Entry<String, Double> currentRating : currentSeason.getRatings()
                        .entrySet()) {
                    rating += currentRating.getValue();
                }
                if (currentSeason.getRatings().size() != 0) {
                    currentSerial.setOverallRating(currentSerial.getOverallRating()
                            + rating / currentSeason.getRatings().size());
                }
            }
            currentSerial.setOverallRating(currentSerial.getOverallRating()
                    / currentSerial.getNumberOfSeasons());
        }
    }





    public ArrayList<ActorData> getActors() {
        return actors;
    }

    public ArrayList<MovieData> getMovies() {
        return movies;
    }

    public ArrayList<SerialData> getSerials() {
        return serials;
    }

    public ArrayList<UserData> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "myDatabase{"
                + "actors=" + actors
                + ", movies=" + movies
                + ", serials=" + serials
                + ", users=" + users + '}';
    }
}
