package dao;

import model.Show;

import java.util.List;
import java.util.Optional;

public interface ShowDAO {
     boolean add(Show show, int movieId, int screenId);
     boolean update(Show show);
     boolean deleteById(int showId);
     Optional<Show> findById(int showId);
     List<Show> findByMovieId(int movieId);
     List<Show> findByScreenId(int screenId);
     List<Show> findAll();
}