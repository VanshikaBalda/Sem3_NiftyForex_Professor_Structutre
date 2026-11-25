
package forex.service;

import forex.dao.TransactionDAO;
import forex.dao.WalletDAO;

import java.math.BigDecimal;
import java.util.Set;

public class WalletService {

    private final WalletDAO dao = new WalletDAO();
    private final TransactionDAO tx = new TransactionDAO();

    private static final Set<String> CURRENCIES =
            Set.of("INR", "USD", "EUR", "GBP");

    // Get balance for a particular currency
    public BigDecimal balance(int uid, String currency) {
        currency = validCurrency(currency);
        return dao.balance(uid, currency);
    }

    // Deposit money
    public void deposit(int uid, String currency, BigDecimal amount) {

        valid(amount);
        currency = validCurrency(currency);

        dao.change(uid, currency, amount);

        tx.add(uid, "DEPOSIT", currency, currency,
                amount, amount, "Wallet deposit (simulation)");
    }

    // Withdraw money
    public void withdraw(int uid, String currency, BigDecimal amount) {

        valid(amount);
        currency = validCurrency(currency);

        if (balance(uid, currency).compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient wallet balance.");
        }

        dao.change(uid, currency, amount.negate());

        tx.add(uid, "WITHDRAW", currency, currency,
                amount, amount, "Wallet withdrawal (simulation)");
    }

    // Validate amount
    private void valid(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }
    }

    // Validate and normalize currency
    private String validCurrency(String currency) {

        if (currency == null) {
            throw new IllegalArgumentException(
                    "Unsupported currency.");
        }

        currency = currency.trim().toUpperCase();

        if (!CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException(
                    "Unsupported currency.");
        }

        return currency;
    }
}
