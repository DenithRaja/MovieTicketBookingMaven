package model;

import java.util.ArrayList;
import java.util.List;

public class Theatre {
    private int theatreId;
    private String name;
    private String location;
    private List<Screen> screens;

    public Theatre(int theatreId, String name, String location ) {
        this.theatreId = theatreId;
        this.name = name;
        this.location = location;
        this.screens = new ArrayList<>();
    }

    public void addScreen(Screen screen){
        screens.add(screen);
    }

    public void removeScreen(Screen screen){
        screens.remove(screen);
    }

    public int getScreenCount(){
        return screens.size();
    }

    public boolean matchesLocation(String location){
        return this.location.equalsIgnoreCase(location);
    }

    public int getTheatreId() {
        return theatreId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Theatre{" +
                "theatreId=" + theatreId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
