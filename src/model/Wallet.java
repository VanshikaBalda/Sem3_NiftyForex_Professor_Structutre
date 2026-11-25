
package forex.model;

import java.time.LocalDateTime;

public record Wallet(
    int walletId,
    int userId,
    LocalDateTime createdAt
) {}

