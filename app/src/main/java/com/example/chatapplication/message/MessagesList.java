package com.example.chatapplication.message;

import java.util.Comparator;

public class MessagesList{
    private String name, lassMessage, profilePic, email, time;
    public MessagesList(String email, String name, String lassMessage, String profilePic, String time) {
        this.email = email;
        this.name = name;
        this.lassMessage = lassMessage;
        this.profilePic = profilePic;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getLassMessage() {
        return lassMessage;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setLassMessage(String lassMessage) {
        this.lassMessage = lassMessage;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
