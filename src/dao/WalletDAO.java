
package forex.dao;

import forex.exception.DatabaseException;

import java.sql.*;
import java.math.BigDecimal;
import java.util.UUID;

public class WalletDAO {

    // Create wallet and initialize all four currencies
    public void create(int userId) {

        String walletSQL =
            "INSERT INTO wallets (user_id) VALUES (?)";

        String balanceSQL =
            "INSERT INTO wallet_balances " +
            "(wallet_id, currency_code, balance) VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection()) {

            c.setAutoCommit(false);

            try {
                int walletId;

                // Create wallet
                try (PreparedStatement p = c.prepareStatement(
                        walletSQL, Statement.RETURN_GENERATED_KEYS)) {

                    p.setInt(1, userId);
                    p.executeUpdate();

                    try (ResultSet keys = p.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Wallet ID not generated");
                        }

                        walletId = keys.getInt(1);
                    }
                }

                // Initialize currency balances
                String[] currencies = {"INR", "USD", "EUR", "GBP"};

                try (PreparedStatement p = c.prepareStatement(balanceSQL)) {

                    for (String currency : currencies) {
                        p.setInt(1, walletId);
                        p.setString(2, currency);
                        p.setBigDecimal(3, BigDecimal.ZERO);
                        p.addBatch();
                    }

                    p.executeBatch();
                }

                c.commit();

            } catch (SQLException e) {
                c.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Wallet creation failed: " + e.getMessage(), e);
        }
    }

    // Retrieve balance for a particular currency
    public BigDecimal balance(int userId, String currency) {

        String sql =
            "SELECT wb.balance FROM wallet_balances wb " +
            "JOIN wallets w ON wb.wallet_id = w.wallet_id " +
            "WHERE w.user_id = ? AND wb.currency_code = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, userId);
            p.setString(2, currency.toUpperCase());

            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return r.getBigDecimal("balance");
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Balance lookup failed: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    // Update balance for a particular currency
    public void change(int userId, String currency, BigDecimal amount) {

        String sql =
            "UPDATE wallet_balances wb " +
            "JOIN wallets w ON wb.wallet_id = w.wallet_id " +
            "SET wb.balance = wb.balance + ? " +
            "WHERE w.user_id = ? AND wb.currency_code = ?";

        try (Connection c = DatabaseConnection.getConnection()) {

            c.setAutoCommit(false);

            try (PreparedStatement p = c.prepareStatement(sql)) {

                p.setBigDecimal(1, amount);
                p.setInt(2, userId);
                p.setString(3, currency.toUpperCase());

                if (p.executeUpdate() == 0) {
                    throw new SQLException(
                        "Wallet currency balance not found");
                }

                c.commit();

            } catch (SQLException e) {
                c.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Wallet update failed: " + e.getMessage(), e);
        }
    }

    // Transfer between two currencies and record the exchange
    public void transfer(
            int userId,
            String from,
            String to,
            BigDecimal amount,
            BigDecimal converted,
            BigDecimal exchangeRate) {

        // Validate transfer values
        if (amount == null || amount.signum() <= 0 ||
            converted == null || converted.signum() <= 0 ||
            exchangeRate == null || exchangeRate.signum() <= 0) {

            throw new IllegalArgumentException(
                "Invalid transfer values.");
        }

        if (from == null || to == null ||
            from.isBlank() || to.isBlank() ||
            from.equalsIgnoreCase(to)) {

            throw new IllegalArgumentException(
                "Invalid currency conversion.");
        }

        from = from.toUpperCase();
        to = to.toUpperCase();

        String walletSQL =
            "SELECT wallet_id FROM wallets WHERE user_id = ?";

        String debitSQL =
            "UPDATE wallet_balances " +
            "SET balance = balance - ? " +
            "WHERE wallet_id = ? AND currency_code = ? " +
            "AND balance >= ?";

        String creditSQL =
            "UPDATE wallet_balances " +
            "SET balance = balance + ? " +
            "WHERE wallet_id = ? AND currency_code = ?";

        String transactionSQL =
            "INSERT INTO transactions " +
            "(wallet_id, reference, type, status, " +
            "source_currency, source_amount, " +
            "destination_currency, destination_amount, " +
            "fee, exchange_rate, total_debit) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection()) {

            c.setAutoCommit(false);

            try {
                int walletId;

                // Find wallet
                try (PreparedStatement p =
                         c.prepareStatement(walletSQL)) {

                    p.setInt(1, userId);

                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) {
                            throw new SQLException("Wallet not found");
                        }

                        walletId = r.getInt("wallet_id");
                    }
                }

                // Deduct source currency
                try (PreparedStatement p =
                         c.prepareStatement(debitSQL)) {

                    p.setBigDecimal(1, amount);
                    p.setInt(2, walletId);
                    p.setString(3, from);
                    p.setBigDecimal(4, amount);

                    if (p.executeUpdate() == 0) {
                        throw new SQLException(
                            "Insufficient source balance or currency not found");
                    }
                }

                // Credit destination currency
                try (PreparedStatement p =
                         c.prepareStatement(creditSQL)) {

                    p.setBigDecimal(1, converted);
                    p.setInt(2, walletId);
                    p.setString(3, to);

                    if (p.executeUpdate() == 0) {
                        throw new SQLException(
                            "Destination currency balance not found");
                    }
                }

                // Record exchange transaction
                try (PreparedStatement p =
                         c.prepareStatement(transactionSQL)) {

                    p.setInt(1, walletId);
                    p.setString(2, UUID.randomUUID().toString());
                    p.setString(3, "EXCHANGE");
                    p.setString(4, "SUCCESS");
                    p.setString(5, from);
                    p.setBigDecimal(6, amount);
                    p.setString(7, to);
                    p.setBigDecimal(8, converted);
                    p.setBigDecimal(9, BigDecimal.ZERO);
                    p.setBigDecimal(10, exchangeRate);
                    p.setBigDecimal(11, amount);

                    p.executeUpdate();
                }

                c.commit();

            } catch (SQLException e) {
                c.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Currency transfer failed: " + e.getMessage(), e);
        }
    }
}
