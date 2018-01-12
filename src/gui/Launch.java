package gui;

import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import io.IO;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;

public class Launch extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Launch frame = new Launch();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Launch() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 250, 250);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel title = new JLabel("AMGINE 5");
        title.setFont(new Font("Bank Gothic", Font.BOLD, 25));
        title.setBounds(53, 37, 144, 34);
        contentPane.add(title);

        JButton launch = new JButton("Launch");
        launch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                launch();
            }
        });

        launch.setBounds(62, 152, 125, 34);
        contentPane.add(launch);

        textField = new JTextField();
        textField.setBounds(44, 98, 162, 28);
        textField.setColumns(10);

        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    launch();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        textField.setText("Name");
        contentPane.add(textField);
    }

    public void launch() {
        String name = textField.getText();
        if (!name.equals("Name") && !name.equals("Name already exists!")) {
            if(name.contains(" ") || name.contains("\"")){
                System.out.println("Invalid name!");
                textField.setText("Invalid name!");
                return;
            }
            
            if (!IO.hasName(name)) {
                new ChatWindow(name);
                setVisible(false);
                dispose();
            } else {
                System.out.println("Name: " + name + " already exists!");
                textField.setText("Name already exists!");
            }
        }
    }
}
