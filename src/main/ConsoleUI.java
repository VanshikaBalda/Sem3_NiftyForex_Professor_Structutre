
package forex.main;

import forex.model.ExchangeQuote;
import forex.service.AlertService;
import forex.service.AuthService;
import forex.service.ExchangeRateService;
import forex.service.ForexService;
import forex.service.HistoryService;
import forex.service.PortfolioService;
import forex.service.ReportService;
import forex.service.WalletService;
import forex.service.WatchlistService;
import forex.util.InputHelper;

import java.math.BigDecimal;

public class ConsoleUI {

    private final InputHelper in = new InputHelper();
    private final AuthService auth = new AuthService();
    private final WalletService wallet = new WalletService();
    private final ForexService forex = new ForexService();
    private final ExchangeRateService rates = new ExchangeRateService();
    private final WatchlistService watch = new WatchlistService();
    private final AlertService alerts = new AlertService();
    private final PortfolioService portfolio = new PortfolioService();
    private final HistoryService history = new HistoryService();
    private final ReportService reports = new ReportService();

    public void run() {

        System.out.println(
            "\n====================================" +
            "\n       NIFTYFOREX CONSOLE" +
            "\n===================================="
        );

        while (true) {

            System.out.println(
                "\n1. Register" +
                "\n2. Login" +
                "\n3. View exchange rates" +
                "\n0. Exit"
            );

            int c = in.choice("Choice: ");

            try {
                if (c == 1) {
                    register();
                }
                else if (c == 2) {

                    AuthService.Session s = auth.login(
                        in.text("Email: "),
                        in.text("Password: ")
                    );

                    if (s == null) {
                        System.out.println("Invalid email or password.");
                    } else {
                        userMenu(s);
                    }
                }
                else if (c == 3) {
                    showRates();
                }
                else if (c == 0) {
                    System.out.println("Goodbye!");
                    return;
                }
                else {
                    System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void register() {

        auth.register(
            in.text("Full name: "),
            in.text("Email: "),
            in.text("Password (min 6 chars): ")
        );

        System.out.println(
            "Registration successful. You can now log in."
        );
    }

    private void userMenu(AuthService.Session s) {

        while (true) {

            System.out.println(
                "\nWelcome, " + s.name() +
                "\n1. Wallet balance" +
                "\n2. Deposit money" +
                "\n3. Withdraw money" +
                "\n4. Convert currency (simulated)" +
                "\n5. Exchange rates" +
                "\n6. Add to watchlist" +
                "\n7. View watchlist" +
                "\n8. Create price alert" +
                "\n9. View alerts" +
                "\n10. Portfolio" +
                "\n11. Transaction history" +
                "\n12. Account report" +
                "\n0. Logout"
            );

            int c = in.choice("Choice: ");

            try {

                switch (c) {

                    case 1 -> showBalance(s.id());

                    case 2 -> deposit(s.id());

                    case 3 -> withdraw(s.id());

                    case 4 -> convert(s.id());

                    case 5 -> showRates();

                    case 6 -> watch.add(
                        s.id(),
                        in.text("Currency code (e.g. EUR): ")
                    );

                    case 7 ->
                        watch.list(s.id()).forEach(System.out::println);

                    case 8 -> alerts.add(
                        s.id(),
                        in.text("Currency: "),
                        in.amount("Target rate (USD value): "),
                        in.text("Direction (ABOVE/BELOW): ")
                    );

                    case 9 ->
                        alerts.list(s.id()).forEach(System.out::println);

                    case 10 ->
                        portfolio.list(s.id()).forEach(System.out::println);

                    case 11 ->
                        history.recent(s.id()).forEach(System.out::println);

                    case 12 ->
                        reports.print(s.id());

                    case 0 -> {
                        return;
                    }

                    default ->
                        System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Display all four currency balances
    private void showBalance(int uid) {

        System.out.println("\n--- WALLET BALANCES ---");

        String[] currencies = {"INR", "USD", "EUR", "GBP"};

        for (String currency : currencies) {

            BigDecimal amount = wallet.balance(uid, currency);

            System.out.println(currency + ": " + amount);
        }
    }

    // Deposit money into selected currency
    private void deposit(int uid) {

        String currency = chooseCurrency();

        BigDecimal amount = in.amount(
            "Deposit amount (" + currency + "): "
        );

        wallet.deposit(uid, currency, amount);

        System.out.println("Deposit successful.");
    }

    // Withdraw money from selected currency
    private void withdraw(int uid) {

        String currency = chooseCurrency();

        BigDecimal amount = in.amount(
            "Withdrawal amount (" + currency + "): "
        );

        wallet.withdraw(uid, currency, amount);

        System.out.println("Withdrawal successful.");
    }

    // Currency selection
    private String chooseCurrency() {

        System.out.println(
            "\nSelect currency:" +
            "\n1. INR" +
            "\n2. USD" +
            "\n3. EUR" +
            "\n4. GBP"
        );

        int choice = in.choice("Currency choice: ");

        return switch (choice) {
            case 1 -> "INR";
            case 2 -> "USD";
            case 3 -> "EUR";
            case 4 -> "GBP";
            default -> throw new IllegalArgumentException(
                "Invalid currency selection."
            );
        };
    }

    // Currency conversion
    private void convert(int uid) {

        String from = in.text("From currency (e.g. USD): ");
        String to = in.text("To currency (e.g. EUR): ");

        BigDecimal amount = in.amount("Amount: ");

        ExchangeQuote q = forex.exchange(uid, from, to, amount);

        System.out.println(
            "Simulated result: " +
            q.amount() + " " + q.from() +
            " = " + q.converted() + " " + q.to() +
            " | rate " + q.rate()
        );
    }

    // Display exchange rates
    private void showRates() {

        System.out.println(
            "\nRates are stored as units of currency per 1 USD:"
        );

        rates.rates().forEach(
            (k, v) -> System.out.println("1 USD = " + v + " " + k)
        );
    }
}
