import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class PersonalityTest {
    private static int intjScore = 0;
    private static int infjScore = 0;
    private static int esfpScore = 0;
    private static int entpScore = 0;
    private static int questionIndex = 0;

    private static final String[] questions = {
            "Do you prefer spending time alone or with a small group of close friends?",
            "Are you more focused on future possibilities than the present moment?",
            "Do you enjoy analyzing complex ideas and theories?",
            "Are you empathetic and understanding towards others' feelings?",
            "Do you often seek new and exciting experiences?",
            "Are you adaptable and spontaneous?",
            "Do you find it easy to express your thoughts and emotions?",
            "Are you good at understanding and manipulating systems or technologies?",
            "Do you enjoy debating and exploring different viewpoints?",
            "Are you often seen as calm and reserved?",
            "Do you enjoy helping others and making a positive impact on their lives?",
            "Are you often the center of attention at social gatherings?",
            "Do you enjoy planning and organizing events or activities?",
            "Are you open to new experiences and challenges?",
            "Do you often question established rules and traditions?",
            "Are you good at brainstorming creative solutions to problems?"
    };

    public static void main(String user) {
        JFrame frame = new JFrame("Personality Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel questionPanel = new JPanel(new FlowLayout());
        JLabel questionLabel = new JLabel();
        questionPanel.add(questionLabel);
        frame.add(questionPanel, BorderLayout.NORTH);

        JPanel answerPanel = new JPanel(new GridLayout(1, 2));
        JRadioButton yesButton = new JRadioButton("Yes");
        JRadioButton noButton = new JRadioButton("No");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(yesButton);
        buttonGroup.add(noButton);
        answerPanel.add(yesButton);
        answerPanel.add(noButton);
        frame.add(answerPanel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Next");
        frame.add(nextButton, BorderLayout.SOUTH);

        questionLabel.setText(questions[questionIndex]);

        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAnswer("yes");
            }
        });

        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAnswer("no");
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                questionIndex++;
                if (questionIndex < questions.length) {
                    questionLabel.setText(questions[questionIndex]);
                    buttonGroup.clearSelection();
                } else {
                    showResult(user);
                }
            }
        });

        frame.setSize(400, 150);
        frame.setVisible(true);
    }

    private static void handleAnswer(String answer) {
        if (answer.equals("yes")) {
            esfpScore += (questions[questionIndex].hashCode() % 2 == 0) ? 1 : 0;
            entpScore += (questions[questionIndex].hashCode() % 2 == 0) ? 1 : 0;
        } else if (answer.equals("no")) {
            intjScore += (questions[questionIndex].hashCode() % 2 == 0) ? 1 : 0;
            infjScore += (questions[questionIndex].hashCode() % 2 == 0) ? 1 : 0;
        }
    }

    private static void showResult(String user) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/APP";
        String username = "root";
        String password = "traceout1018";


        String personalityType;
        if (intjScore >= infjScore && intjScore >= esfpScore && intjScore >= entpScore) {
            personalityType = "INTJ";
        } else if (infjScore >= intjScore && infjScore >= esfpScore && infjScore >= entpScore) {
            personalityType = "INFJ";
        } else if (esfpScore >= intjScore && esfpScore >= infjScore && esfpScore >= entpScore) {
            personalityType = "ESFP";
        } else {
            personalityType = "ENTP";
        }

        JOptionPane.showMessageDialog(null, "Your personality type is: " + personalityType);

        String valueToInsert = personalityType; // The value you want to insert into the table

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String updateQuery = "UPDATE user_detail SET personality_type = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, personalityType);
                preparedStatement.setString(2, user); // Assuming you store the logged-in username in a variable

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Your personality type is: " + personalityType + "\nPersonality type updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "No row found with the given username.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}