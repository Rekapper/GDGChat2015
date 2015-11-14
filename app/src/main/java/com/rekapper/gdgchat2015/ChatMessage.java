package com.rekapper.gdgchat2015;

/**
 * Created by Parker on 11/14/2015.
 */
    public class ChatMessage {
        private String name;
        private String text;

        public ChatMessage() {
            // necessary for Firebase's deserializer
        }
        public ChatMessage(String name, String text) {
            this.name = name;
            this.text = text;
        }

        public String getName() {
            return name;
        }

        public String getText() {
            return text;
        }
}
