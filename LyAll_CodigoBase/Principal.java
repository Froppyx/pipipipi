package BatizLyA.LyAII_CodigoBase;

import java.awt.*;
import java.io.IOException;

public class Principal {
    public static void main(String[] args) throws IOException {
        // Solo inicializa la interfaz
        InterfazCoquette.mostrar();
    }

    public static void cambiarColorFondo(Component comp, Color color) {
        if (comp != null) {
            comp.setBackground(color);
            if (comp instanceof Container) {
                for (Component c : ((Container) comp).getComponents()) {
                    cambiarColorFondo(c, color);
                }
            }
        }
    }

    public static void cambiarFuente(Component comp, Font fuente) {
        if (comp != null) {
            comp.setFont(fuente);
            if (comp instanceof Container) {
                for (Component c : ((Container) comp).getComponents()) {
                    cambiarFuente(c, fuente);
                }
            }
        }
    }

}
