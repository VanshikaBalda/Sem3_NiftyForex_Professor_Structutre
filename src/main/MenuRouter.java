package forex.main;
public class MenuRouter {
 private final ConsoleUI ui; public MenuRouter(ConsoleUI ui){this.ui=ui;} public void start(){ui.run();}
}
