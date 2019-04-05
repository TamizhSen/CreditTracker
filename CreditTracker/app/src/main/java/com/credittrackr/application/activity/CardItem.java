package com.credittrackr.application.activity;

/**
 * Created by ravi on 26/09/17.
 */

public class CardItem {
    String _id;
    String cardNumber;
    String cardType;
    String expirationDate;
    String thumbnail;
    String bank;
    String interestRate;
    String startDate;
    String endDate;
    String maximumLimit;
    float totalExpense;
    float totalPaid;

    public CardItem() {
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setcardType(String cardType) {
        this.cardType = cardType;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String expense) {
        this.bank = bank;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMaximumLimit() {
        return maximumLimit;
    }

    public void setMaximumLimit(String maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public float getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(float totalExpense) {
        this.totalExpense = totalExpense;
    }

    public float getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(float totalPaid) {
        this.totalPaid = totalPaid;
    }
}
