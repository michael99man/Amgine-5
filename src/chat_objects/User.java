package chat_objects;

import java.security.PublicKey;
import java.util.ArrayList;

import gui.ChatPanel;

public class User {

    public PublicKey publicKey;
    public String name;
    public ChatPanel pane;
    private ArrayList<Message> messages;

    public User(String name, PublicKey publicKey) {
        this.publicKey = publicKey;
        this.name = name;
        messages = new ArrayList<Message>();
    }

    @Override
    public boolean equals(Object o) {
        if ((o instanceof User) && (((User) o).name.equals(this.name))
                && ((User) o).publicKey.equals(this.publicKey)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name; // " : " + Crypto.getPublicKeyString(publicKey);
    }

    public void add(Message m) {
        if (contains(m)) {
            return;
        } else {
            messages.add(m);
            pane.addMessage(m);
        }
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public boolean contains(Message m) {
        for (Message msg : messages) {
            if (m.equals(msg)) {
                return true;
            }
        }
        return false;
    }

}
