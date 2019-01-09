package org.tensorflow.demo.models;


public class UserNotice {
    String noticeID, userID, firstName, lastName, notice, imageURL
            , timestamp;
    Long userTimestamp;
    int image;

    public UserNotice(String firstName, String lastName, int image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    public UserNotice(String firstName, String lastName, String timestamp, int image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.timestamp = timestamp;
        this.image = image;
    }

    public UserNotice(String firstName, String lastName, String notice, String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.notice = notice;
        this.imageURL = imageURL;
    }

    public UserNotice(String noticeID, String userID, String firstName,
                      String lastName, String notice, String imageURL, Long userTimestamp, String timestamp) {
        this.noticeID = noticeID;
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.notice = notice;
        this.imageURL = imageURL;
        this.userTimestamp = userTimestamp;
        this.timestamp = timestamp;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public String getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNotice() {
        return notice;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getImage() {
        return image;
    }

    public Long getUserTimestamp() {
        return userTimestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
