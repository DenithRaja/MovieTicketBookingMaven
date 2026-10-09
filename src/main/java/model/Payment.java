package model;

public class Payment {

    private int paymentId;
    private double amount;
    private String paymentMethod;
    private String status;

    public Payment(int paymentId, double amount, String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public boolean validatePaymentDetails() {

        if (amount <= 0) {
            return false;
        }

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return false;
        }
    
        return true;
    }

    public boolean processPayment() {

        if (!validatePaymentDetails()) {
            status = "FAILED";
            return false;
        }

        status = "COMPLETED";
        return true;
    }

    public String verifyPaymentStatus() {
        return status;
    }

    public boolean refundPayment(double refundAmount) {

        if (refundAmount <= 0 || refundAmount > amount) {
            return false;
        }

        status = "REFUNDED";
        return true;
    }

    public boolean retryPayment() {

        if (!status.equalsIgnoreCase("FAILED")) {
            return false;
        }

        return processPayment();
    }

    public int getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Payment{"+
                "paymentId=" + paymentId+
                ", amount=" + amount+
                ", paymentMethod='" + paymentMethod + '\''+
                ", status='" + status + '\''+
                "}";
    }
}