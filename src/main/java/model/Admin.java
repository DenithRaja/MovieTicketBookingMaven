package model;

public class Admin extends User{
    public Admin(int userId, String name, String email, String phone) {
        super(userId, name, email, phone);
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
