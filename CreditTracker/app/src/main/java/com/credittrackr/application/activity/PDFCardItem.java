package com.credittrackr.application.activity;

/**
 * Created by ravi on 26/09/17.
 */

public class PDFCardItem {
    String _id;
    String cardNumber;
    String statementdate;
    String expirationDate;
    String minimumLimit;
    String availableLimit;
    String intrestrate;
    String maximumLimit;

    public PDFCardItem() {
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

    public String getStatementdate() {
        return statementdate;
    }

    public void setStatementdate(String statementdate) {
        this.statementdate = statementdate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(String availableLimit) {
        this.availableLimit = availableLimit;
    }

    public String getMinimumLimit() {
        return minimumLimit;
    }

    public void setMinimumLimit(String minimumLimit) {
        this.minimumLimit = minimumLimit;
    }

    public String getInterestRate() {
        return intrestrate;
    }

    public void setInterestRate(String intrestrate) {
        this.intrestrate = intrestrate;
    }

    public String getMaximumLimit() {
        return maximumLimit;
    }

    public void setMaximumLimit(String maximumLimit) {
        this.maximumLimit = maximumLimit;
    }
}
