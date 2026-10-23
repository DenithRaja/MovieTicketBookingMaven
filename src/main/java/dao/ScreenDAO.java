package dao;

import model.Screen;

import java.util.List;
import java.util.Optional;

public interface ScreenDAO {
    boolean add(Screen screen, int theatreId);
    boolean update(Screen screen);
    boolean deleteById(int screenId);
    Optional<Screen> findById(int screenId);
    List<Screen> findByTheatreId(int theatreId);
}