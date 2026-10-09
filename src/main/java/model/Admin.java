package model;

public class Admin extends User{
    public Admin(int userId, String name, String email, String phone) {
        super(userId, name, email, phone);
    }

    public void addMovie(Movie movie){

    }

    public void updateMovie(Movie movie){

    }

    public void removeMovie(int movieId){

    }

    public void addTheatre(Theatre theatre){

    }

    public void scheduleShow(Show show){

    }

    public void cancelShow(int showId){

    }

    @Override
    public String toString() {
        return "Admin{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
