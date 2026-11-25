package forex.model;
import java.math.BigDecimal;
public record PortfolioHolding(int userId,String currency,BigDecimal quantity) {}
