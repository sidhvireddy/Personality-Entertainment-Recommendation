import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/APP";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "traceout1018";
    public static String loggedInUsername;

    public static void main(String[] args) {
        JFrame frame = new JFrame("User Registration/Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JButton registerButton = new JButton("Sign Up");
        JButton loginButton = new JButton("Log In");

        frame.add(registerButton);
        frame.add(loginButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegistrationDialog();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginDialog();
            }
        });

        frame.setVisible(true);
    }

    private static void showRegistrationDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField userIdField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField personalityField = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderField);


        int result = JOptionPane.showConfirmDialog(null, panel, "Sign Up", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String userId = userIdField.getText();
            String gender = genderField.getText();
            String personality = personalityField.getText();

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String insertQuery = "INSERT INTO user_detail (username, user_id, gender, password) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, userId);
                    preparedStatement.setString(3, gender);

                    preparedStatement.setString(4, password);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "User registered successfully!");
                    // Instead of directly calling main method, consider creating a separate method in PersonalityTest
                    // and call that method after registration.
                    PersonalityTest.main(username);
                  // Example method, update as needed
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showLoginDialog() {
        JTextField usernameField = new JTextField();
         JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Log In", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            loggedInUsername = username;
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String selectQuery = "SELECT * FROM user_detail WHERE username=? AND password=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        checkPersonalityType(username);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void checkPersonalityType(String username) {
        // Connect to the user_detail database and retrieve the personality type for the user
        String userDetailUrl = "jdbc:mysql://localhost:3306/APP";
        String userDetailUser = "root";
        String userDetailPassword = "traceout1018";

        String userDetailSql = "SELECT personality_type FROM user_detail WHERE username = ?";

        try (
                Connection userDetailConnection = DriverManager.getConnection(userDetailUrl, userDetailUser, userDetailPassword);
                PreparedStatement userDetailPreparedStatement = userDetailConnection.prepareStatement(userDetailSql);
        ) {
            userDetailPreparedStatement.setString(1, username);
            ResultSet userDetailResultSet = userDetailPreparedStatement.executeQuery();

            if (userDetailResultSet.next()) {
                String personalityType = userDetailResultSet.getString("personality_type");

                // Connect to the new_table database and perform the personality type check
                String newTableUrl = "jdbc:mysql://localhost:3306/APP";
                String newTableUser = "root";
                String newTablePassword = "traceout1018";

                String newTableSql = "SELECT * FROM new_table WHERE personality_type = ?";

                try (
                        Connection newTableConnection = DriverManager.getConnection(newTableUrl, newTableUser, newTablePassword);
                        PreparedStatement newTablePreparedStatement = newTableConnection.prepareStatement(newTableSql);
                ) {
                    newTablePreparedStatement.setString(1, personalityType);
                    ResultSet newTableResultSet = newTablePreparedStatement.executeQuery();

                    if (newTableResultSet.next()) {
                        int id = newTableResultSet.getInt(1);
                        String movie = newTableResultSet.getString(3);
                        String song = newTableResultSet.getString(4);
                        String book = newTableResultSet.getString(5);
                        String podcast = newTableResultSet.getString(6);

                        // Display the information in a JOptionPane
                        String message = "ID: " + id + "\n"
                                + "Personality Type: " + personalityType + "\n"
                                + "Movie: " + movie + "\n"
                                + "Song: " + song + "\n"
                                + "Book: " + book + "\n"
                                + "Podcast: " + podcast;

                        JOptionPane.showMessageDialog(null, message, "Personality Recommendations", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "No matching rows found for the given personality type.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "No matching rows found for the given username in the user_detail database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
