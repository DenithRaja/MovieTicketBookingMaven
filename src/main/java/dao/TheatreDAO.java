package dao;

import model.Theatre;

import java.util.List;
import java.util.Optional;

public interface TheatreDAO {
    boolean add(Theatre theatre);
    boolean update(Theatre theatre);
    boolean deleteById(int theatreId);
    Optional<Theatre> findById(int theatreId);
    List<Theatre> findAll();
}