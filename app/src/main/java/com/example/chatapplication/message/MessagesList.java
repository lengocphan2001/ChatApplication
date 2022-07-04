package com.example.chatapplication.message;

public class MessagesList {
    private String name, lassMessage, profilePic, email;
    private int unseenMessages;
    public MessagesList(String email, String name, String lassMessage, String profilePic, int unseenMessages) {
        this.email = email;
        this.name = name;
        this.lassMessage = lassMessage;
        this.profilePic = profilePic;
        this.unseenMessages = unseenMessages;
    }

    public String getName() {
        return name;
    }

    public String getLassMessage() {
        return lassMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLassMessage(String lassMessage) {
        this.lassMessage = lassMessage;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
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
}
