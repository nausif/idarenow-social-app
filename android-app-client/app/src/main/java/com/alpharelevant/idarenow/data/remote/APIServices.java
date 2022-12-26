package com.alpharelevant.idarenow.data.remote;

import com.alpharelevant.idarenow.data.models.CustomModel.AcceptRejectPost;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeById;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeFromUser;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeSummary;
import com.alpharelevant.idarenow.data.models.CustomModel.MessageFormat;
import com.alpharelevant.idarenow.data.models.CustomModel.MessageFormat2;
import com.alpharelevant.idarenow.data.models.CustomModel.Cities;
import com.alpharelevant.idarenow.data.models.CustomModel.Comments;
import com.alpharelevant.idarenow.data.models.CustomModel.EditProfile;
import com.alpharelevant.idarenow.data.models.CustomModel.MarketerChallengeModel;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedPost;
import com.alpharelevant.idarenow.data.models.CustomModel.ProfileDetails;
import com.alpharelevant.idarenow.data.models.CustomModel.TranscationsDetails;
import com.alpharelevant.idarenow.data.models.CustomModel.UserDetails;
import com.alpharelevant.idarenow.data.models.CustomModel.UserResult;
import com.alpharelevant.idarenow.data.models.CustomModel.notificationResponse;
import com.alpharelevant.idarenow.data.models.User_Accounts;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by NAUSIF on 27-Oct-17.
 */

public interface APIServices {

    // Credentials check karega server se.
    @GET("Login/GetAuthentication")
    Call<Integer> getAuthentication(@Query("email") String email, @Query("pass") String pass);

    @GET("Login/getUserDetails")
    Call<UserDetails> getUserDetails(@Query("user_id") int user_id);
    // Check User Exist or not
    @GET("Signup/getEmailExist")
    Call<Boolean> getEmailExist(@Query("email") String email);

    // Register User in Database
    @POST("Signup/postRegister")
    Call<Boolean> postRegisterUser(@Body User_Accounts user);

    @GET("User_accounts/GetUser_Accounts")
    Call<User_Accounts> getUser_Accounts(@Query("id") int id);

    @GET("Profile/getUser_Profile")
    Call<ProfileDetails> getUser_Profile(@Query("id") int id);

    @GET("User_accounts/getUserIdByEmail")
    Call<Integer> getUserIdByEmail(@Query("email") String email);

    @GET("Challenge/getChallengeIdByType")
    Call<Integer> getChallengeIdByType(@Query("type") String type);

    @POST("Challenge/setChallengeFromUser")
    Call<Boolean> setChallengeFromUser(@Body ChallengeFromUser type);

    @GET("Signup/fbCheckAndSave")
    Call<Integer> fbCheckAndSave(@Query("email") String email, @Query("name") String name);

    @Multipart
    @POST("Profile/postProfileImage")
    Call<ResponseBody> postProfileImage(@Part MultipartBody.Part filePart,@Query("id") String id);

    @GET("Profile/GetProfileImage")
    Call<ResponseBody> getProfileImage(@Query("id") int id);

    // Get Search Results
    @GET("Search_User/GetSearchUsers")
    Call<List<UserResult>> getSearchResults(@Query("search") String search,@Query("id") String id);

    @Multipart
    @POST("Video/postChallengeVideo")
    Call<ResponseBody> postChallengeVideo(@Part MultipartBody.Part filePart, @Query("description") String description, @Query("c_id") Integer id, @Query("user_to_id") Integer userId);

    @GET("Notification/getNotificationOnTapItem")
    Call<notificationResponse[]> getNotificationOnTapItem(@Query("user_id") Integer id,@Query("challenge_id") Integer challengeId);

    @GET("Notification/checkPendingAssignChallenges")
    Call<notificationResponse[]> checkPendingAssignChallenges(@Query("user_id") Integer id,@Query("count") Integer count);

    @GET("Notification/checkPendingAcceptChallenge")
    Call<notificationResponse[]> checkPendingAcceptChallenge(@Query("user_id") Integer id);

    @GET("Challenge/getChallengeById")
    Call<ChallengeById> getChallengeById(@Query("id") Integer id);

    @GET("Challenge/ChallengeStatusUpdate")
    Call<ResponseBody> ChallengeStatusUpdate(@Query("c_id") Integer c_id,@Query("a_r_challenge") Integer a_r_challenge);

    @GET("Video/GetVideoById")
    Call<String> GetVideoById(@Query("id") Integer id);

    @GET("Newsfeed/getNewsFeedList")
    Call<NewsfeedPost[]> getNewsFeedList(@Query("user_id") Integer user_id,@Query("offset") Integer offset);

    @GET("Notification/getAllNotifications")
    Call<notificationResponse[]> getNotificationList(@Query("user_id") Integer user_id, @Query("offset") Integer offset);

    @POST("Newsfeed/ApproveRejectPost")
    Call<AcceptRejectPost> ApproveRejectPost(@Query("c_id") Integer c_id, @Query("user_id") Integer user_id, @Query("status") Integer status);

    @GET("Profile/getUserVideos")
    Call<NewsfeedPost[]> getProfileChallengedVideo(@Query("user_id") Integer user_id,@Query("offset") Integer offset);




    @GET("EditProfile/getProfileData")
    Call<EditProfile> getProfileData(@Query("user_id") Integer user_id);

    @GET("EditProfile/getSelectedCitiesFromCountry")
    Call<List<Cities>> getSelectedCitiesFromCountry(@Query("country_id") Integer country_id);

    @POST("EditProfile/postUpdateProfile")
    Call<Void> postEditProfile(@Body EditProfile ed);

    @GET("Comments/getAllComments")
    Call<List<Comments>> getAllComments(@Query("post_id") Integer post_id);

    @POST("Comments/postComment")
    Call<Void> postComment(@Body Comments comment);

    @GET("ChallengesSummary/getChallengesList")
    Call<List<ChallengeSummary>> getChallengesList(@Query("user_id") Integer user_id,@Query("approval_status") Integer approval_status, @Query("offset") Integer offset);

    @GET("ChallengesSummary/getRespondChallengesList")
    Call<List<ChallengeSummary>> getRespondChallengesList(@Query("user_id") Integer user_id,@Query("approval_status") Integer approval_status, @Query("offset") Integer offset);

    @GET("ChallengesSummary/getChallengesList")
    Call<List<ChallengeSummary>> getInProgressList(@Query("user_id") Integer user_id,@Query("approval_status") Integer approval_status, @Query("offset") Integer offset);

    @GET("ChallengesSummary/getUncompletedChallengesList")
    Call<List<ChallengeSummary>> getUncompletedChallengesList(@Query("user_id") Integer user_id,@Query("approval_status") Integer approval_status, @Query("offset") Integer offset);

    @GET("MarketerChallenge/getMarketerChallengesList")
    Call<List<ChallengeSummary>> getMarketerChallengesList(@Query("offset") Integer offset);



    @POST("MarketerChallenge/postMarketerChallenge")
    Call<Boolean> postMarketerChallenge(@Body MarketerChallengeModel marketchallengepost);

    @GET("Transcations/getTranscationsList")
    Call<List<TranscationsDetails>> getTranscationsList(@Query("user_id") Integer user_id, @Query("offset") Integer offset);

    @GET("Transcations/getUsertotalBalanceAmount")
    Call<Double> getUserTotalBalanceAmount(@Query("user_id") Integer user_id);

    @GET("Challenge/getUserTotalBalance")
    Call<Integer> getUserTotalBalance(@Query("user_id") Integer user_id);

    @GET("MarketerChallenge/getCheckUserCompletedVideo")
    Call<Boolean> getCheckUserCompletedVideo(@Query("user_id") Integer user_id, @Query("challenge_id") Integer challenge_id);

    @GET("Challenge/getChallengeAssociatedVideos")
    Call<NewsfeedPost[]> getChallengeAssociatedVideos(@Query("user_id") Integer user_id,@Query("challenge_id") Integer challenge_id,@Query("offset") Integer offset);

    @GET("ChallengesSummary/getChallengesByYou")
    Call<List<ChallengeSummary>>  getChallengesByYou(@Query("user_id") Integer user_id, @Query("offset") Integer offset);

    @POST("MarketerChallenge/postMarketerSelectWinner")
    Call<Boolean> postMarketerSelectWinner(@Query("user_to_id") Integer user_to_id, @Query("challenge_id") Integer challenge_id);

    @GET("MarketerChallenge/getWinnerID_ifExists")
    Call<Integer> getWinnerID_ifExists(@Query("challenge_id") Integer challenge_id);

    @GET("Messaging/getMessagesFromId")
    Call<MessageFormat[]> getMessagesFromId(@Query("my_id") Integer my_id, @Query("from_id") Integer from_id);

    @POST("Messaging/postSendMsg")
    Call<Boolean> postSendMsg(@Query("my_id") Integer my_id, @Query("to_id") Integer to_id, @Query("message") String  message);

    @GET("Messaging/getAllMessages")
    Call<MessageFormat2[]> getAllMessages(@Query("my_id") Integer my_id);

    @GET("Login/getForgetPassword")
    Call<Boolean> getForgetPassword(@Query("email") String email);

    @POST("EditProfile/postChangePassword")
    Call<Boolean> postChangePassword(@Query("user_id") int user_id,@Query("current_pass") String current_pass,@Query("new_pass") String new_pass);

}