import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckPersonalityType {
    private String userId;

    public CheckPersonalityType(String userId) {
        this.userId = userId;
        checkPersonalityType();
    }

    public static void main(String args) {
        // Assuming you have a method or variable to get the user ID
        String userId = getUserIDFromOtherCode(); // Replace with the actual method or variable

        if (userId != null) {
            CheckPersonalityType checker = new CheckPersonalityType(userId);
        } else {
            System.out.println("Unable to retrieve user ID from the other code.");
        }
    }

    private static String getUserIDFromOtherCode() {
        // Replace this with the actual method or variable to get the user ID from the other code
        return "user123";
    }

    private void checkPersonalityType() {
        // Connect to the user_detail database and retrieve the personality type for the user
        String userDetailUrl = "jdbc:mysql://localhost:3306/APP";
        String userDetailUser = "root";
        String userDetailPassword = "traceout1018";

        String userDetailSql = "SELECT personality_type FROM user_detail WHERE username = ?";

        try (
                Connection userDetailConnection = DriverManager.getConnection(userDetailUrl, userDetailUser, userDetailPassword);
                PreparedStatement userDetailPreparedStatement = userDetailConnection.prepareStatement(userDetailSql);
        ) {
            userDetailPreparedStatement.setString(1, userId);
            ResultSet userDetailResultSet = userDetailPreparedStatement.executeQuery();

            if (userDetailResultSet.next()) {
                String personalityType = userDetailResultSet.getString("personality_type");

                // Connect to the new_table database and perform personality type check
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

                        System.out.println("ID: " + id);
                        System.out.println("Personality Type: " + personalityType);
                        System.out.println("Movie: " + movie);
                        System.out.println("Song: " + song);
                        System.out.println("Book: " + book);
                        System.out.println("Podcast: " + podcast);
                    } else {
                        System.out.println("No matching rows found for the given personality type.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("No matching rows found for the given user ID in the user_detail database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
