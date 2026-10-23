package dao;

import model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieDAO {
    boolean add(Movie movie);
    boolean update(Movie movie);
    boolean deleteById(int movieId);
    Optional<Movie> findById(int movieId);
    List<Movie> findAll();
    List<Movie> searchByTitle(String title);
}