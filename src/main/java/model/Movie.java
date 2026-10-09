package model;

public class Movie {
    private int movieId;
    private String title;
    private String genre;
    private String language;
    private int duration;

    public Movie(int movieId, String title, String genre, String language, int duration) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
    }

    public void updateDetails(String title, String genre, String language,int duration){
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
    }

    public boolean matchesGenre(String genre){
        return this.genre.equalsIgnoreCase(genre);
    }

    public boolean matchesLanguage(String language){
        return this.language.equalsIgnoreCase(language);
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", language='" + language + '\'' +
                ", duration=" + duration +
                '}';
    }
}

