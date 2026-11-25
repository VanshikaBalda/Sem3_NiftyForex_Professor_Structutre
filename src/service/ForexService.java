
package forex.service;

import forex.dao.WalletDAO;
import forex.dao.PortfolioDAO;
import forex.model.ExchangeQuote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class ForexService {

    private final ExchangeRateService rates =
            new ExchangeRateService();

    private final WalletDAO wallet =
            new WalletDAO();

    private final PortfolioDAO portfolio =
            new PortfolioDAO();

    private static final Set<String> CURRENCIES =
            Set.of("INR", "USD", "EUR", "GBP");

    // Calculate conversion quote
    public ExchangeQuote quote(
            int uid,
            String from,
            String to,
            BigDecimal amount) {

        from = normalizeCurrency(from);
        to = normalizeCurrency(to);

        validate(from, to, amount);

        BigDecimal converted =
                rates.convert(amount, from, to);

        BigDecimal rate =
                converted.divide(amount, 8, RoundingMode.HALF_UP);

        return new ExchangeQuote(
                from,
                to,
                amount,
                converted,
                rate
        );
    }

    // Perform actual wallet conversion
    public ExchangeQuote exchange(
            int uid,
            String from,
            String to,
            BigDecimal amount) {

        ExchangeQuote q = quote(uid, from, to, amount);

        // Deduct source currency and credit destination currency
        wallet.transfer(
                uid,
                q.from(),
                q.to(),
                q.amount(),
                q.converted(),
                q.rate()
        );

        // Update portfolio
        portfolio.add(uid, q.to(), q.converted());

        return q;
    }

    // Normalize currency input
    private String normalizeCurrency(String currency) {

        if (currency == null) {
            throw new IllegalArgumentException(
                    "Currency cannot be empty.");
        }

        return currency.trim().toUpperCase();
    }

    // Validate conversion request
    private void validate(
            String from,
            String to,
            BigDecimal amount) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }

        if (!CURRENCIES.contains(from) ||
                !CURRENCIES.contains(to)) {

            throw new IllegalArgumentException(
                    "Unsupported currency.");
        }

        if (from.equals(to)) {
            throw new IllegalArgumentException(
                    "Source and destination currencies must be different.");
        }
    }
}
