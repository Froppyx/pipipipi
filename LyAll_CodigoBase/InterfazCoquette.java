package Batiziño;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public class InterfazCoquette {

    public static void mostrar() {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Color whiteBackground = Color.WHITE;
        Color pinkBackground = new Color(255, 188, 210);
        Color pinkDark = new Color(255, 105, 180);
        Color blackText = Color.BLACK;

        //PERSONALIZACIÓN DE COMPONENTES EN GENERAL
        UIManager.put("FileChooser.background", new ColorUIResource(whiteBackground));
        UIManager.put("FileChooser.listViewBackground", new ColorUIResource(pinkBackground));
        UIManager.put("FileChooser.detailsViewBackground", new ColorUIResource(pinkBackground));
        UIManager.put("Panel.background", new ColorUIResource(whiteBackground));
        UIManager.put("Label.background", new ColorUIResource(whiteBackground));

        UIManager.put("textHighlight", new ColorUIResource(pinkBackground.brighter()));
        UIManager.put("textHighlightText", new ColorUIResource(blackText));

        //PERSONALIZACIÓN DE BOTONES
        UIManager.put("Button.background", new ColorUIResource(pinkDark));
        UIManager.put("Button.foreground", new ColorUIResource(Color.BLACK));
        UIManager.put("Button.select", new ColorUIResource(pinkDark));
        UIManager.put("Button.focus", new ColorUIResource(pinkDark.brighter()));
        UIManager.put("Button.highlight", new ColorUIResource(pinkDark));
        UIManager.put("Button.shadow", new ColorUIResource(pinkDark.darker()));
        UIManager.put("Button.darkShadow", new ColorUIResource(pinkDark.darker().darker()));
        UIManager.put("Button.border", BorderFactory.createLineBorder(pinkDark.darker(), 3));

        //PERSONALIZACIÓN DE LISTA DE ARCVHIVOS
        UIManager.put("List.border", BorderFactory.createLineBorder(pinkDark.darker(), 2));
        UIManager.put("List.selectionBackground", new ColorUIResource(pinkBackground));
        UIManager.put("List.selectionForeground", new ColorUIResource(Color.BLACK));
        UIManager.put("List.background", new ColorUIResource(pinkBackground));

        //PERSONALIZACIÓN DE LISTA DESPLEGABLE DE CARPETAS
        UIManager.put("ComboBox.background", new ColorUIResource(whiteBackground));
        UIManager.put("ComboBox.buttonBackground", new ColorUIResource(pinkDark));
        UIManager.put("ComboBox.foreground", new ColorUIResource(Color.BLACK));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(pinkBackground));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(Color.BLACK));
        UIManager.put("ComboBox.listBackground", new ColorUIResource(whiteBackground));
        UIManager.put("ComboBox.listForeground", new ColorUIResource(Color.BLACK));
        UIManager.put("ComboBox.border", BorderFactory.createLineBorder(pinkDark.darker(), 2));

        //PERSONALIZACIÓN DE TEXTO Y CAMPO DE TEXTO
        UIManager.put("TextField.background", new ColorUIResource(whiteBackground));
        UIManager.put("TextField.foreground", new ColorUIResource(Color.BLACK));
        UIManager.put("TextField.caretForeground", new ColorUIResource(Color.BLACK));
        UIManager.put("TextField.selectionBackground", new ColorUIResource(pinkBackground));
        UIManager.put("TextField.selectionForeground", new ColorUIResource(Color.BLACK));
        UIManager.put("TextField.border", BorderFactory.createLineBorder(pinkDark.darker(), 1));

        UIManager.put("OptionPane.background", new ColorUIResource(pinkBackground));
        UIManager.put("Panel.background", new ColorUIResource(whiteBackground));
        UIManager.put("Label.foreground", new ColorUIResource(Color.BLACK));
        UIManager.put("MenuItem.background", new ColorUIResource(whiteBackground));
        UIManager.put("MenuItem.foreground", new ColorUIResource(Color.BLACK));
        UIManager.put("MenuItem.selectionBackground", new ColorUIResource(pinkBackground));
        UIManager.put("MenuItem.selectionForeground", new ColorUIResource(Color.BLACK));

        Font customFont = new Font("Century Gothic", Font.PLAIN, 14);
        UIManager.put("FileChooser.font", new FontUIResource(customFont));
        UIManager.put("Label.font", new FontUIResource(customFont));
        UIManager.put("Button.font", new FontUIResource(customFont));
        UIManager.put("TextField.font", new FontUIResource(customFont));
        UIManager.put("ComboBox.font", new FontUIResource(customFont));
        UIManager.put("List.font", new FontUIResource(customFont));
        UIManager.put("Panel.font", new FontUIResource(customFont));



        // Crear y mostrar el JFileChooser
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Selecciona un archivo");

        fc.setBackground(whiteBackground);

        int resultado = fc.showOpenDialog(null);
        // Verificar si se seleccionó un archivo
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fc.getSelectedFile();
            StringBuilder codigo = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    codigo.append(linea).append(" ");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al leer el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Crear una instancia del analizador sintáctico
            Parser parser = new Parser(codigo.toString());

            // Por medio de un JOptionPane, se muestra el resultado del análisis
            StringBuilder resultadoAnalisis = new StringBuilder();
            resultadoAnalisis.append("TABLA DE SÍMBOLOS\n-----------------\n");
            if (parser.variable != null && parser.tipo != null) {
                for (int i = 0; i < parser.variable.length; i++) {
                    resultadoAnalisis.append(parser.variable[i]).append(": ").append(parser.tipo[i]).append("\n");
                }
            }
            // Mostrar los errores léxicos y sintácticos
            resultadoAnalisis.append("\nBYTECODE\n-----------------\n");
            resultadoAnalisis.append(parser.getBytecode());

            JOptionPane.showMessageDialog(null, resultadoAnalisis.toString(), "Resultados del análisis", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(null, "No se seleccionó ningún archivo.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

