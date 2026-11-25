
package forex.dao;

import forex.exception.DatabaseException;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionDAO {

    // Save a transaction
    public void add(
            int userId,
            String type,
            String from,
            String to,
            BigDecimal amount,
            BigDecimal result,
            String note) {

        if (amount == null || amount.signum() <= 0 ||
                result == null || result.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid transaction amount.");
        }

        if (type == null || from == null || to == null ||
                type.isBlank() || from.isBlank() || to.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid transaction details.");
        }

        String walletSQL =
                "SELECT wallet_id FROM wallets WHERE user_id = ?";

        String transactionSQL =
                "INSERT INTO transactions " +
                "(wallet_id, reference, type, status, " +
                "source_currency, source_amount, " +
                "destination_currency, destination_amount, " +
                "fee, exchange_rate, total_debit) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection()) {

            int walletId;

            // Find wallet
            try (PreparedStatement p = c.prepareStatement(walletSQL)) {

                p.setInt(1, userId);

                try (ResultSet r = p.executeQuery()) {
                    if (!r.next()) {
                        throw new SQLException("Wallet not found");
                    }

                    walletId = r.getInt("wallet_id");
                }
            }

            BigDecimal fee = BigDecimal.ZERO;
            BigDecimal exchangeRate = BigDecimal.ONE;

            BigDecimal totalDebit =
                    "WITHDRAW".equalsIgnoreCase(type)
                    ? amount
                    : BigDecimal.ZERO;

            // Insert transaction
            try (PreparedStatement p =
                         c.prepareStatement(transactionSQL)) {

                p.setInt(1, walletId);
                p.setString(2, UUID.randomUUID().toString());
                p.setString(3, type.toUpperCase());
                p.setString(4, "SUCCESS");
                p.setString(5, from.toUpperCase());
                p.setBigDecimal(6, amount);
                p.setString(7, to.toUpperCase());
                p.setBigDecimal(8, result);
                p.setBigDecimal(9, fee);
                p.setBigDecimal(10, exchangeRate);
                p.setBigDecimal(11, totalDebit);

                p.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Transaction save failed: " + e.getMessage(), e);
        }
    }

    // Retrieve recent transactions
    public List<String> recent(int userId) {

        List<String> out = new ArrayList<>();

        String sql =
                "SELECT t.created_at, t.type, " +
                "t.source_currency, t.source_amount, " +
                "t.destination_currency, t.destination_amount, " +
                "t.status " +
                "FROM transactions t " +
                "JOIN wallets w ON t.wallet_id = w.wallet_id " +
                "WHERE w.user_id = ? " +
                "ORDER BY t.transaction_id DESC LIMIT 30";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, userId);

            try (ResultSet r = p.executeQuery()) {

                while (r.next()) {

                    out.add(
                            r.getTimestamp("created_at") +
                            " | " + r.getString("type") +
                            " | " + r.getBigDecimal("source_amount") +
                            " " + r.getString("source_currency") +
                            " -> " + r.getBigDecimal("destination_amount") +
                            " " + r.getString("destination_currency") +
                            " | " + r.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "History load failed: " + e.getMessage(), e);
        }

        return out;
    }
}
