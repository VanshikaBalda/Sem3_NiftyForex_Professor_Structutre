package forex.model;

import java.math.BigDecimal;

public record CurrencyBalance(
    int balanceId,
    int walletId,
    String currencyCode,
    BigDecimal balance
) {}