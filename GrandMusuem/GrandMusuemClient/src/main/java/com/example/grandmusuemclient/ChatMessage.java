package com.example.grandmusuemclient;

public class ChatMessage {
    private String content;
    private boolean isSent; // true for sent messages, false for received messages

    public ChatMessage(String content, boolean isSent) {
        this.content = content;
        this.isSent = isSent;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }
}
