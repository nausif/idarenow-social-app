package com.alpharelevant.idarenow.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NAUSIF on 27-Oct-17.
 */

public class User_Accounts {

    @SerializedName("assign_Challenge")
    @Expose
    private List<Object> assignChallenge = null;
    @SerializedName("challenged_Users")
    @Expose
    private List<Object> challengedUsers = null;

    @SerializedName("transactions")
    @Expose
    private List<Object> transactions = null;
    @SerializedName("transactions1")
    @Expose
    private List<Object> transactions1 = null;

    @SerializedName("user_Balance")
    @Expose
    private List<Object> userBalance = null;
    @SerializedName("user_Messages")
    @Expose
    private List<Object> userMessages = null;
    @SerializedName("user_Messages1")
    @Expose
    private Object userMessages1;
    @SerializedName("video_Comments")
    @Expose
    private List<Object> videoComments = null;
    @SerializedName("video_Ratings")
    @Expose
    private List<Object> videoRatings = null;
    @SerializedName("user_ID")
    @Expose
    private Integer userID;
    @SerializedName("user_FullName")
    @Expose
    private String userFullName;
    @SerializedName("user_Password")
    @Expose
    private String userPassword;
    @SerializedName("user_Gender")
    @Expose
    private String userGender;
    @SerializedName("user_Email")
    @Expose
    private String userEmail;
    @SerializedName("user_PhoneNumber")
    @Expose
    private Object userPhoneNumber;
    @SerializedName("user_Birthdate")
    @Expose
    private Object userBirthdate;
    @SerializedName("user_Registration_Date")
    @Expose
    private String userRegistrationDate;
    @SerializedName("user_Profile_Picture")
    @Expose
    private Object userProfilePicture;
    @SerializedName("user_Account_Type_ID")
    @Expose
    private Integer userAccountTypeID;
    @SerializedName("user_Facebook_ID")
    @Expose
    private Object userFacebookID;
    @SerializedName("user_Country_ID")
    @Expose
    private Integer userCountryID;
    @SerializedName("user_City_ID")
    @Expose
    private Integer userCityID;
    @SerializedName("user_Account_Status")
    @Expose
    private Object userAccountStatus;
    @SerializedName("user_Ratings")
    @Expose
    private Double userRatings;
    @SerializedName("user_Challenged_Completed")
    @Expose
    private Integer userChallengedCompleted;
    @SerializedName("user_Approval_Rate")
    @Expose
    private Integer userApprovalRate;
    @SerializedName("user_Total_Earnings")
    @Expose
    private Integer userTotalEarnings;

    public List<Object> getAssignChallenge() {
        return assignChallenge;
    }

    public void setAssignChallenge(List<Object> assignChallenge) {
        this.assignChallenge = assignChallenge;
    }

    public List<Object> getChallengedUsers() {
        return challengedUsers;
    }

    public void setChallengedUsers(List<Object> challengedUsers) {
        this.challengedUsers = challengedUsers;
    }



    public List<Object> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Object> transactions) {
        this.transactions = transactions;
    }

    public List<Object> getTransactions1() {
        return transactions1;
    }

    public void setTransactions1(List<Object> transactions1) {
        this.transactions1 = transactions1;
    }



    public List<Object> getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(List<Object> userBalance) {
        this.userBalance = userBalance;
    }

    public List<Object> getUserMessages() {
        return userMessages;
    }

    public void setUserMessages(List<Object> userMessages) {
        this.userMessages = userMessages;
    }

    public Object getUserMessages1() {
        return userMessages1;
    }

    public void setUserMessages1(Object userMessages1) {
        this.userMessages1 = userMessages1;
    }

    public List<Object> getVideoComments() {
        return videoComments;
    }

    public void setVideoComments(List<Object> videoComments) {
        this.videoComments = videoComments;
    }

    public List<Object> getVideoRatings() {
        return videoRatings;
    }

    public void setVideoRatings(List<Object> videoRatings) {
        this.videoRatings = videoRatings;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Object getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(Object userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public Object getUserBirthdate() {
        return userBirthdate;
    }

    public void setUserBirthdate(Object userBirthdate) {
        this.userBirthdate = userBirthdate;
    }

    public String getUserRegistrationDate() {
        return userRegistrationDate;
    }

    public void setUserRegistrationDate(String userRegistrationDate) {
        this.userRegistrationDate = userRegistrationDate;
    }

    public Object getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(Object userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public Integer getUserAccountTypeID() {
        return userAccountTypeID;
    }

    public void setUserAccountTypeID(Integer userAccountTypeID) {
        this.userAccountTypeID = userAccountTypeID;
    }

    public Object getUserFacebookID() {
        return userFacebookID;
    }

    public void setUserFacebookID(Object userFacebookID) {
        this.userFacebookID = userFacebookID;
    }

    public Integer getUserCountryID() {
        return userCountryID;
    }

    public void setUserCountryID(Integer userCountryID) {
        this.userCountryID = userCountryID;
    }

    public Integer getUserCityID() {
        return userCityID;
    }

    public void setUserCityID(Integer userCityID) {
        this.userCityID = userCityID;
    }

    public Object getUserAccountStatus() {
        return userAccountStatus;
    }

    public void setUserAccountStatus(Object userAccountStatus) {
        this.userAccountStatus = userAccountStatus;
    }

    public Double getUserRatings() {
        if(userRatings==null){
            return  Double.valueOf(0);
        }
        return userRatings;
    }

    public void setUserRatings(Double userRatings) {
        this.userRatings = userRatings;
    }

    public Integer getUserChallengedCompleted() {
        if(userChallengedCompleted==null){
            return 0;
        }
        return userChallengedCompleted;
    }

    public void setUserChallengedCompleted(Integer userChallengedCompleted) {
        this.userChallengedCompleted = userChallengedCompleted;
    }

    public Integer getUserApprovalRate() {
        if(userApprovalRate==null){
            return 0;
        }
        return userApprovalRate;
    }

    public void setUserApprovalRate(Integer userApprovalRate) {
        this.userApprovalRate = userApprovalRate;
    }

    public Integer getUserTotalEarnings() {
        if(userTotalEarnings==null){
            return 0;
        }
        return userTotalEarnings;
    }

    public void setUserTotalEarnings(Integer userTotalEarnings) {
        this.userTotalEarnings = userTotalEarnings;
    }

}
