
package impl;

import dao.TheatreDAO;
import model.Theatre;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TheatreDataStorageDAO implements TheatreDAO {

    private final Map<Integer, Theatre> theatres = new LinkedHashMap<>();

    @Override
    public boolean add(Theatre theatre) {
        if (theatre == null) {
            throw new IllegalArgumentException("Theatre cannot be null");
        }

        int theatreId = theatre.getTheatreId();

        if (theatres.containsKey(theatreId)) {
            throw new IllegalArgumentException("Theatre ID already exists");
        }

        theatres.put(theatreId, theatre);
        return true;
    }

    @Override
    public boolean update(Theatre theatre) {
        if (theatre == null) {
            throw new IllegalArgumentException("Theatre cannot be null");
        }

        int theatreId = theatre.getTheatreId();

        if (!theatres.containsKey(theatreId)) {
            throw new IllegalArgumentException("Theatre not found");
        }

        theatres.put(theatreId, theatre);
        return true;
    }

    @Override
    public boolean deleteById(int theatreId) {
        if (theatres.remove(theatreId) == null) {
            throw new IllegalArgumentException("Theatre not found");
        }

        return true;
    }

    @Override
    public Optional<Theatre> findById(int theatreId) {
        return Optional.ofNullable(theatres.get(theatreId));
    }

    @Override
    public List<Theatre> findAll() {
        return new ArrayList<>(theatres.values());
    }
}
