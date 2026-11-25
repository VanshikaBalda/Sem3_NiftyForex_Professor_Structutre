package forex.model;
import java.math.BigDecimal;
public record PriceAlert(int userId,String currency,BigDecimal target,String direction) {}
