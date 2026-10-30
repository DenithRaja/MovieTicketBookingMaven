package model;

public class Payment {

    private int paymentId;
    private double amount;
    private String paymentMethod;
    private String status;
    private double refundAmount;
    private boolean simulateFailure = false;

    public Payment(
            int paymentId,
            double amount,
            String paymentMethod,
            String status) {

        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }


    public Payment(
            double amount,
            String paymentMethod,
            String status) {

        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }


    public boolean validatePaymentDetails() {
        return Double.isFinite(amount)
                && amount > 0
                && paymentMethod != null
                && !paymentMethod.isBlank();
    }

    public boolean processPayment() {
        if (!validatePaymentDetails()) {
            throw new IllegalArgumentException(
                    "Invalid payment details."
            );
        }

        if (simulateFailure) {
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
        if (status == null
                || !status.equalsIgnoreCase("COMPLETED")) {
            return false;
        }

        if (!Double.isFinite(refundAmount)
                || refundAmount <= 0
                || !Double.isFinite(amount)
                || refundAmount > amount) {
            return false;
        }

        this.refundAmount = refundAmount;
        status = "REFUNDED";

        return true;
    }

    public boolean retryPayment() {
        if (status == null
                || !status.equalsIgnoreCase("FAILED")) {
            return false;
        }

        return processPayment();
    }

    public void setRefundAmount(double refundAmount) {
        if (!Double.isFinite(refundAmount)
                || refundAmount < 0) {
            throw new IllegalArgumentException(
                    "Refund amount must be finite and non-negative."
            );
        }

        if (!Double.isFinite(amount)
                || refundAmount > amount) {
            throw new IllegalArgumentException(
                    "Refund amount cannot exceed the payment amount."
            );
        }

        this.refundAmount = refundAmount;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        if (paymentId <= 0) {
            throw new IllegalArgumentException(
                    "Payment ID must be greater than zero."
            );
        }

        this.paymentId = paymentId;
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

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", refundAmount=" + refundAmount +
                '}';
    }
}