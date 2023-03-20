package models;

import java.util.ArrayList;

/**
 * Model class for an email
 */
public class Email implements java.io.Serializable {

    private final String sender;
    private final ArrayList<String> recipients;
    private final String subject;
    private final String message;

    public Email(String sender, ArrayList<String> recipients, String subject, String message) {
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public ArrayList<String> getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

}
