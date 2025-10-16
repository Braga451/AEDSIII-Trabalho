package app;

import java.util.Locale;
import view.MainView;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(new Locale("pt", "BR"));
        // O Main apenas cria e inicia a View.
        // Toda a lógica de menu e interação estará na MainView.
        MainView view = new MainView();
        view.run();
    }
}