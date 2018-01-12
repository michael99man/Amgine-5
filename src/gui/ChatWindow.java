
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chat_objects.User;
import io.Chatroom;

public class ChatWindow extends JFrame {

    private JPanel contentPane;
    private Chatroom chat;
    private JList<String> userList;

    private JPanel currentChat;
    private User selectedUser;

    private DefaultListModel<String> list;

    public ChatWindow(String name) {
        setVisible(true);
        setTitle("Amgine 5: " + name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 400);
        setMinimumSize(new Dimension(500, 400));

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        list = new DefaultListModel<String>();
        userList = new JList<String>(list);
        userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setPreferredSize(new Dimension(50, 200));
        userList.setFixedCellHeight(30);
        userList.setFixedCellWidth(130);
        userList.getSelectionModel()
                .addListSelectionListener(new ListSelectionHandler());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contentPane.add(userList, BorderLayout.WEST);

        // start chatroom code!
        chat = new Chatroom(name, this);

        // MAKE A NEW PANEL THAT DISPLAYS ALL THE DETAILED INFORMATION!!!
        // my public key etc
    }

    // add a new user to the list
    public void addUser(User u) {
        list.addElement(u.name);

        if (u.pane == null) {
            new ChatPanel(u, chat);
        }

        contentPane.revalidate();
        contentPane.repaint();
    }

    private synchronized void select(int i) {
        User user = chat.users.get(i);

        if (user == selectedUser)
            return;
        selectedUser = user;

        // remove the current JPanel
        if (currentChat != null) {
            contentPane.remove(currentChat);
        }

        currentChat = user.pane;
        contentPane.add(user.pane, BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();
    }

    private class ListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                int index = lsm.getMaxSelectionIndex();
                if (index == -1) {

                } else {
                    select(index);
                }
            }
        }
    }

}
