package com.project.models;

import java.util.ArrayList;

public class EmailConverter {

        /**
         * It transforms a EmailSerializable into a Email object keeping all its details
         * @param es is the EmailSerializable
         * @return an Email object
         */
        public static Email toMail(EmailSerializable es) {
            return new Email(es.getId(), es.getSender(), es.getRecipients(), es.getSubject(), es.getMessage(), es.getDate());
        }

        /**
         * It transforms an Email into an EmailSerializable object keeping all its details
         * @param e is the Email
         * @return a EmailSerializable object
         */
        public static EmailSerializable toMailSerializable(Email e) {
            return new EmailSerializable(e.getId(), e.getSender(), (ArrayList<String>) e.getRecipients(), e.getSubject(), e.getMessage(), e.getDate());
        }


}
