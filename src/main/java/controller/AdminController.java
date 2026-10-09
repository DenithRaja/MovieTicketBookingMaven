package controller;

import view.AdminView;

public class AdminController {
    private AdminView adminView;

    public AdminController(AdminView adminView) {
        this.adminView = adminView;
    }

    public void start() {
        while (true) {
            adminView.showAdminMenu();

            int choice = adminView.getChoice();

            switch (choice) {
                case 1:
                    adminView.showMessage("Add Movie.");
                    break;

                case 2:
                    adminView.showMessage("Update Movie.");
                    break;

                case 3:
                    adminView.showMessage("Remove Movie.");
                    break;

                case 4:
                    adminView.showMessage("Add Theatre.");
                    break;

                case 5:
                    adminView.showMessage("Add Screen.");
                    break;

                case 6:
                    adminView.showMessage("Add Seat.");
                    break;

                case 7:
                    adminView.showMessage("Schedule Show.");
                    break;

                case 8:
                    adminView.showMessage("Cancel Show.");
                    break;

                case 9:
                    adminView.showMessage("Logging out...");
                    return;

                default:
                    adminView.showMessage(
                            "Invalid choice. Please select 1-9."
                    );
            }
        }
    }

}
