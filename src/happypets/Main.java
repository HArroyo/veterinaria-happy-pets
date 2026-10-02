package happypets;

import javax.swing.SwingUtilities;

import happypets.ui.LoginFrame;
import happypets.ui.Ui;

/**
 * Clase principal de inicio de la plataforma Veterinaria Happy Pets.
 * Inicia la interfaz con la pantalla de Login moderna y credenciales por defecto (admin / admin).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   Iniciando Plataforma Veterinaria Happy Pets   ");
        System.out.println("   Versión: 1.0.0 Oficial                        ");
        System.out.println("   Credenciales por defecto: admin / admin       ");
        System.out.println("=================================================");

        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
