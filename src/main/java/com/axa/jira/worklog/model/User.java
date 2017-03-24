package com.axa.jira.worklog.model;

/**
 * Created by A.Solianyk on 17.02.2017.
 */
public class User {
    private String userName;
    private String userId;
    private String avatarId;

    public User(String userName, String userId, String avatarId) {
        this.userName = userName;
        this.userId = userId;
        this.avatarId = avatarId;
    }

    public String getAvatarUrl(){
        return "http://jira/secure/useravatar?" + /*size=large&*/ "ownerId=" + userId + (avatarId != null ? "&avatarId=" + avatarId : "");
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
