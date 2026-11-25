
package forex.service;

import forex.dao.ExchangeRateDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class ExchangeRateService {

    private final ExchangeRateDAO dao = new ExchangeRateDAO();

    // Retrieve all exchange rates
    public Map<String, BigDecimal> rates() {
        return dao.all();
    }

    // Retrieve rate for a currency
    public BigDecimal getRate(String cur) {

        if (cur == null || cur.isBlank()) {
            throw new IllegalArgumentException(
                    "Currency code cannot be empty.");
        }

        BigDecimal r = dao.rate(cur.trim().toUpperCase());

        if (r == null || r.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Unsupported currency: " + cur);
        }

        return r;
    }

    // Convert one currency into another
    public BigDecimal convert(
            BigDecimal amount,
            String from,
            String to) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }

        BigDecimal fromRate = getRate(from);
        BigDecimal toRate = getRate(to);

        return amount.multiply(toRate)
                     .divide(fromRate, 4, RoundingMode.HALF_UP);
    }
}
