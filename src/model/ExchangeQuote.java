package forex.model;
import java.math.BigDecimal;
public record ExchangeQuote(String from,String to,BigDecimal amount,BigDecimal converted,BigDecimal rate) {}
