package forex.util;
import java.math.BigDecimal; import java.util.Scanner;
public class InputHelper {
 private final Scanner sc=new Scanner(System.in);
 public String text(String prompt){System.out.print(prompt);return sc.nextLine().trim();}
 public int choice(String prompt){while(true){try{return Integer.parseInt(text(prompt));}catch(NumberFormatException e){System.out.println("Enter a valid number.");}}}
 public BigDecimal amount(String prompt){while(true){try{return new BigDecimal(text(prompt));}catch(NumberFormatException e){System.out.println("Enter a valid amount, e.g. 25.50");}}}
}
