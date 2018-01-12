package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import chat_objects.Message;
import chat_objects.User;
import io.Chatroom;

public class ChatPanel extends JPanel {
    private User user;
    @SuppressWarnings("unused")
    private Chatroom chat;
    private Box box;

    private Box leftBox;
    private Box rightBox;

    private static final long serialVersionUID = -3466616916555224742L;

    public ChatPanel(User u, Chatroom chat) {
        user = u;
        this.chat = chat;

        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(300, 300));

        // box holds all the messages!
        // box = Box.createHorizontalBox();
        // box.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        // box.setSize(getSize());
        // box.setAlignmentY(TOP_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        box = Box.createHorizontalBox();

        leftBox = Box.createVerticalBox();
        rightBox = Box.createVerticalBox();

        // leftBox.setPreferredSize(new
        // Dimension(getPreferredSize().width/2,getPreferredSize().height));
        // brightBox.setPreferredSize(new
        // Dimension(getPreferredSize().width/2,getPreferredSize().height));
        leftBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
        box.add(leftBox);
        box.add(rightBox);

        panel.add(box, BorderLayout.NORTH);

        JLabel title = new JLabel(user.name);
        title.setFont(new Font("Bank Gothic", Font.BOLD, 25));

        title.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField textField = new JTextField();
        textField.setMaximumSize(textField.getPreferredSize());
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        && !textField.getText().equals("")) {
                    String text = textField.getText();
                    textField.setText("");
                    textField.repaint();
                    // send the message!
                    chat.sendMessage(user, text);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // box.add(label);
        // box.add(textField);

        //add(box, BorderLayout.CENTER);
        add(panel,BorderLayout.CENTER);
        add(title, BorderLayout.NORTH);
        add(textField, BorderLayout.SOUTH);
        user.pane = this;
        System.out.println("Initialized JPanel for: " + user);
    }

    // adds the new message to the display
    public void addMessage(Message m) {
        /*
        border = new LineBorder(Color.BLACK,3,true);
        Border margin = new EmptyBorder(10,10,10,10);
        
        label.setBorder(new CompoundBorder(border,margin));
        */

        JLabel label = new JLabel(m.plaintext);

        Color c;
        Border border;
        // I sent this
        if (m.iSent) {
            c = new Color(0, 119, 255);
            // put it on the LEFT side
            // label.setColor
            label.setBackground(c);
            leftBox.add(label);
            border = new TextBubbleBorder(c, 3, 5, 0);
            int pad = label.getPreferredSize().height * 2-2;
            label.setForeground(Color.WHITE);
            rightBox.add(Box.createVerticalStrut(pad));
            leftBox.add(Box.createVerticalStrut(5));
        } else {
            // put it on the RIGHT side
            // I received this
            c = new Color(225,225,225);
            border = new TextBubbleBorder(c, 3, 5, 0);
            rightBox.add(label);
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            int pad = label.getPreferredSize().height * 2-2;
            leftBox.add(Box.createVerticalStrut(pad));
            rightBox.add(Box.createVerticalStrut(5));
        }

        label.setAlignmentY(Component.TOP_ALIGNMENT);

        label.setBackground(c);
        label.setBorder(border);
        label.setOpaque(true);
        revalidate();
        repaint();
    }
}
