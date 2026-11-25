
package forex.dao;

import forex.exception.DatabaseException;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;

public class ExchangeRateDAO {

    public Map<String, BigDecimal> all() {

        Map<String, BigDecimal> rates = new LinkedHashMap<>();

        String sql =
            "SELECT quote_currency, rate " +
            "FROM exchange_rates " +
            "WHERE base_currency = 'USD' " +
            "ORDER BY quote_currency";

        try (
            Connection c = DatabaseConnection.getConnection();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql)
        ) {

            while (r.next()) {
                rates.put(
                    r.getString("quote_currency").toUpperCase(),
                    r.getBigDecimal("rate")
                );
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Rates unavailable: " + e.getMessage(), e
            );
        }

        return rates;
    }

    public BigDecimal rate(String currency) {

        String sql =
            "SELECT rate FROM exchange_rates " +
            "WHERE base_currency = 'USD' " +
            "AND quote_currency = ?";

        try (
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement p = c.prepareStatement(sql)
        ) {

            p.setString(1, currency.trim().toUpperCase());

            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return r.getBigDecimal("rate");
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Rate lookup failed: " + e.getMessage(), e
            );
        }

        return null;
    }
}
