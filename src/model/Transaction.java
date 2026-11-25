package forex.model;
import java.math.BigDecimal; import java.time.LocalDateTime;
public record Transaction(int userId,String type,String from,String to,BigDecimal amount,BigDecimal result,LocalDateTime createdAt) {}
