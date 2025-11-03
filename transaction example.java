import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLTransactionExample {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Disable auto-commit to manage transactions manually
            connection.setAutoCommit(false);

            // Simulate transferring money from account 1 to account 2
            int fromAccountId = 1;
            int toAccountId = 2;
            double amount = 100.00;

            // 1. Deduct amount from source account
            String deductSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            PreparedStatement deductStatement = connection.prepareStatement(deductSql);
            deductStatement.setDouble(1, amount);
            deductStatement.setInt(2, fromAccountId);
            int rowsAffectedDeduct = deductStatement.executeUpdate();

            if (rowsAffectedDeduct == 0) {
                throw new SQLException("Failed to deduct from source account. Account not found or insufficient funds.");
            }

            // Introduce a potential error here to demonstrate rollback
            // if (true) { throw new SQLException("Simulating an error during transfer."); }

            // 2. Add amount to destination account
            String addSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            PreparedStatement addStatement = connection.prepareStatement(addSql);
            addStatement.setDouble(1, amount);
            addStatement.setInt(2, toAccountId);
            int rowsAffectedAdd = addStatement.executeUpdate();

            if (rowsAffectedAdd == 0) {
                throw new SQLException("Failed to add to destination account. Account not found.");
            }

            // If all operations succeed, commit the transaction
            connection.commit();
            System.out.println("Transaction successful: $" + amount + " transferred from account " + fromAccountId + " to account " + toAccountId);

        } catch (SQLException e) {
            // If any error occurs, rollback the transaction
            if (connection != null) {
                try {
                    connection.rollback();
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            e.printStackTrace();
        } finally {
            // Close the connection in the finally block
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Revert to default auto-commit behavior
                    connection.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }
}