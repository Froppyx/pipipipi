package BatizLyA.LyAII_CodigoBase;

import javax.swing.JOptionPane;
import BatizLyA.LyAII_CodigoBase.ArbolSintactico.*;
import java.util.Vector;
import org.apache.commons.lang3.ArrayUtils;

public class Parser {
    //Declaración de variables----------------
    Programax p = null;
    String[] tipo = null;
    String[] variable;
    String byteString;
    private Vector tablaSimbolos = new Vector();
    private final Scanner s;
    final int ifx=1, thenx=2, elsex=3, beginx=4, endx=5, printx=6, semi=7,
            sum=8, igual=9, igualdad=10, intx=11, floatx=12, id=13,
            Resta = 14, Multi = 15, Div = 16, doublex = 17, longx = 18;
    private int tknCode, tokenEsperado;
    private String token, tokenActual, log;

    //Sección de bytecode
    private int cntBC = 0; // Contador de lineas para el código bytecode
    private String bc; // String temporal de bytecode
    private int jmp1, jmp2, jmp3;
    private int aux1, aux2, aux3;
    private String pilaBC[] = new String[100];
    private String memoriaBC[] = new String[10];
    private String pilaIns[] = new String [50];
    private int retornos[]= new int[10];
    private int cntIns = 0;
    //---------------------------------------------

/*     public static void main(String[] args){
        //var1 int ; var2 int; if var1 == var2 then print var1 + var2 else begin	if var1 + var2 then var1 := var2 + var1	else var2 := var1 + var2 end
        new Parser("var1 int ; var2 int ; var1 := var2 + var1 ; print var1 + var2 ;");
    } */

    public Parser(String codigo) {
        s = new Scanner(codigo);
        token = s.getToken(true);
        tknCode = stringToCode(token);
        p = P();
    }

    //INICIO DE ANÁLISIS SINTÁCTICO
    public void advance() {
        token = s.getToken(true);
        tokenActual = s.getToken(false);
        tknCode = stringToCode(token);
    }

    //---------------------------------------------------------------

    // Modificado para que llame al metodo codeToSymbol y no muestre el número del token esperado
    public void eat(int t, String simboloEsperado) {
        tokenEsperado = t;
        if(tknCode == t) {
            setLog("Token: " + token + "\n" + "Tipo:  "+ s.getTipoToken());
            advance();
        }
        else{
            error(token, simboloEsperado);
        }
    }


    //----------------------------------------------------------------

    public Programax P() {
        Declarax d = D();
        createTable();
        Statx s = S();

        return new Programax(tablaSimbolos,s);
    }

    public Declarax D() {
      if(tknCode == id) {
        //----------------------------------------------------------
        int nextCode = stringToCode(s.getToken(false));
        if(nextCode == intx || nextCode == floatx || nextCode == doublex || nextCode == longx) {
        //----------------------------------------------------------
          String s = token;
          eat(id, "id");
          Typex t = T();
          eat(semi, ";");
          D();
          tablaSimbolos.addElement(new Declarax(s, t));
          return new Declarax(s, t);
        }
        else{
            error(token, "(int / float / double / long)");
            return null;
        }
      } else if (tknCode != id) {
          return null;
      } else {
          error(token, "(id)");
          return null;
      }

    }

    public Typex T() {
        if(tknCode == intx) {
            eat(intx, "int");
            return new Typex("int");
        }
        else if(tknCode == floatx) {
            eat(floatx, "float");
            return new Typex("float");
        }
        //--------------------------------------------
        else if(tknCode == doublex) {
            eat(doublex, "double");
            return new Typex("double");
        }
        else if(tknCode == longx) {
            eat(longx, "long");
            return new Typex("long");
        }
        else{
            error(token, "(int / float / double / long)");
            return null;
        }
        //---------------------------------------------------
    }

    public Statx S() { //return statement
        switch(tknCode) {
            case ifx:
                Expx e1;
                Statx s1, s2;
                eat(ifx, "if");
                e1= E();
                eat(thenx, "then");
                s1=S();
                eat(elsex, "else");
                s2=S();
                return new Ifx(e1, s1, s2);


            case beginx:
                eat(beginx, "begin");    S();    L();
                return null;

            case id:
                Idx i;
                Expx e;
                eat(id, "id");   i=new Idx(tokenActual);  declarationCheck(tokenActual); byteCode("igual", tokenActual);  eat(igual, ":=");   e=E();
                return new Asignax(i, e);

            case printx:
                Expx ex;
                eat(printx, "print");    ex=E();
                return new Printx(ex);

            default: error(token, "(if | begin | id | print)");
                return null;
        }
    }

    public void L() {
        switch(tknCode) {
            case endx:
                eat(endx, "end");
            break;

            case semi:
                eat(semi, ";");
                S();
                L();
            break;
            default: error(token, "(end | ;)");
        }
    }

    public Expx E() {
       Idx i1, i2;
       String comp1, comp2;

       if(tknCode == id) {
           comp1 = token;
           declarationCheck(token);
           eat(id, "id");
           i1 = new Idx(token);
           switch(stringToCode(token)) {

               case sum:
                   comp2 = tokenActual;
                   eat(sum, "+");   eat(id, "id");
                   i2 = new Idx(comp2); //(tokenActual)
                   declarationCheck(comp2);
                   compatibilityCheck(comp1,comp2);
                   byteCode("suma", comp1, comp2);
                   System.out.println("Operación: " + comp1 + "+" + comp2);
                   return new Sumax(i1, i2);
                //---------------------------
               case Resta:
                     comp2 = tokenActual;
                     eat(Resta, "-");   eat(id, "id");
                     i2 = new Idx(comp2);
                     declarationCheck(comp2);
                     compatibilityCheck(comp1,comp2);
                     byteCode("resta", comp1, comp2);
                     return new Restax(i1, i2);

               case Multi:
                        comp2 = tokenActual;
                        eat(Multi, "*");   eat(id, "id");
                        i2 = new Idx(comp2);
                        declarationCheck(comp2);
                        compatibilityCheck(comp1,comp2);
                        byteCode("multiplicacion", comp1, comp2);
                        return new Multix(i1, i2);

               case Div:
                     comp2 = tokenActual;
                     eat(Div, "/");   eat(id, "id");
                     i2 = new Idx(comp2);
                     declarationCheck(comp2);
                     compatibilityCheck(comp1,comp2);
                     byteCode("division", comp1, comp2);
                     return new Divx(i1, i2);
               //---------------------------
               case igualdad:
                   comp2 = tokenActual;
                   eat(igualdad, "==");   eat(id, "id");
                   i2 = new Idx(comp2);
                   declarationCheck(comp2);
                   compatibilityCheck(comp1,comp2);
                   byteCode("igualdad", comp1, comp2);
                   return new Comparax(i1, i2);

               default:
                   error(token, "(+ | - | * | / | ==)");
                   return null;
           }
       }
       else{
           error(token, "(id)");
           return null;
       }
    } //FIN DEL ANÁLISIS SINTÁCTICO



    public void error(String token, String t) {
        switch(JOptionPane.showConfirmDialog(null,
                "Error sintáctico:\n"
                        + "El token:("+ token + ") no concuerda con la gramática del lenguaje,\n"
                        + "se espera: " + t + ".\n"
                        + "¿Desea detener la ejecución?",
                "Ha ocurrido un error",
                JOptionPane.YES_NO_OPTION)) {
            case JOptionPane.NO_OPTION:
                double e = 1.1;
                break;
            case JOptionPane.YES_OPTION:
                System.exit(0);
                break;
        }
    }

    public int stringToCode(String t) {
        int codigo = 0;
        switch(t) {
            case "if": codigo=1; break;
            case "then": codigo=2; break;
            case "else": codigo=3; break;
            case "begin": codigo=4; break;
            case "end": codigo=5; break;
            case "print": codigo=6; break;
            case ";": codigo=7; break;
            case "+": codigo=8; break;
            //---------------------------
            case "-": codigo=14; break;
            case "*": codigo=15; break;
            case "/": codigo=16; break;
            case "double": codigo=17; break;
            case "long": codigo=18; break;
            //---------------------------
            case ":=": codigo=9; break;
            case "==": codigo=10; break;
            case "int": codigo=11; break;
            case "float": codigo=12; break;
            default: codigo=13; break;
        }
        return codigo;
    }

    //----------------------------------------------------------------
    // Nuevo metodo para evitar que la correcion diga "Token esperado de tipo: n"
    private String codeToSymbol(int code) {
        switch(code) {
            case 1: return "if";
            case 2: return "then";
            case 3: return "else";
            case 4: return "begin";
            case 5: return "end";
            case 6: return "print";
            case 7: return ";";
            case 8: return "+";
            case 14: return "-";
            case 15: return "*";
            case 16: return "/";
            case 9: return ":=";
            case 10: return "==";
            case 11: return "int";
            case 12: return "float";
            case 17: return "double";
            case 18: return "long";
            default: return "id";
        }
    }
    //----------------------------------------------------------------

    //Métodos para recoger la información de los tokens para luego mostrarla
    public void setLog(String l) {
        if(log == null) {
            log = l + "\n \n";
        }
        else{
            log=log + l + "\n \n";
        }
    }

    public String getLog() {
        return log;
    }
    //-----------------------------------------------

    //Recorrido de la parte izquierda del árbol y creación de la tabla de símbolos
    public void createTable() {
        //String[] aux1 = new String[tablaSimbolos.size()];
        //String[] aux2 = new String[tablaSimbolos.size()];
        variable = new String[tablaSimbolos.size()];
        tipo = new String[tablaSimbolos.size()];

        //Imprime tabla de símbolos
        System.out.println("-----------------");
        System.out.println("TABLA DE SÍMBOLOS");
        System.out.println("-----------------");
        for(int i=0; i<tablaSimbolos.size(); i++) {
            Declarax dx;
            Typex tx;
            dx = (Declarax)tablaSimbolos.get(i);
            variable[i] = dx.s1;
            tipo[i] = dx.s2.getTypex();
            System.out.println(variable[i] + ": "+ tipo[i]); //Imprime tabla de símbolos por consola.
        }

        ArrayUtils.reverse(variable);
        ArrayUtils.reverse(tipo);

        System.out.println("-----------------\n");
    }


    //Verifica las declaraciones de las variables consultando la tabla de símbolos
    public void declarationCheck(String s) {
        boolean valido = false;
        for (int i=0; i<tablaSimbolos.size(); i++) {
            if(s.equals(variable[i])) {
                valido = true;
                break;
            }
        }
        if(!valido) {
            System.out.println("La variable "+ s +  " no está declarada.\nSe detuvo la ejecución.");
             javax.swing.JOptionPane.showMessageDialog(null, "La variable [" + s + "] no está declarada", "Error",
                   javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    //Chequeo de tipos consultando la tabla de símbolos
    public void compatibilityCheck(String s1, String s2) {
        Declarax elementoCompara1;
        Declarax elementoCompara2;
        System.out.println("CHECANDO COMPATIBILIDAD ENTRE TIPOS ("+s1+", "+s2+"). ");
        boolean termino = false;
        for(int i=0; i<tablaSimbolos.size() ; i++) {
          elementoCompara1 = (Declarax) tablaSimbolos.elementAt(i);
          if(s1.equals(elementoCompara1.s1)) {
            System.out.println("Se encontró el primer elemento en la tabla de símbolos...");
            for(int j=0; j<tablaSimbolos.size() ; j++) {
              elementoCompara2 = (Declarax) tablaSimbolos.elementAt(j);
              if(s2.equals(elementoCompara2.s1)) {
                System.out.println("Se encontró el segundo elemento en la tabla de símbolos...");
                  if (tipo[i].equals(tipo[j]) ||
                          (tipo[i].equals("float") && tipo[j].equals("double")) ||
                          (tipo[i].equals("double") && tipo[j].equals("float")) ||
                          (tipo[i].equals("int") && tipo[j].equals("long")) ||
                          (tipo[i].equals("long") && tipo[j].equals("int")) ||
                          (tipo[i].equals("double") && tipo[j].equals("long")) ||
                          (tipo[i].equals("long") && tipo[j].equals("double"))) {
                      termino = true;
                  break;
                }else{
                  termino = true;
                    javax.swing.JOptionPane.showMessageDialog(null, "Incompatibilidad de tipos: "+ elementoCompara1.s1 +" ("
                      + elementoCompara1.s2.getTypex() + "), "+elementoCompara2.s1 +" (" + elementoCompara2.s2.getTypex()
                      +").", "Error",
                      javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                break;
              }
            }
          }
          if(termino) {
            break;
          }
        }
    }

    public void byteCode(String tipo, String s1,String s2){
        int pos1=-1, pos2=-1;

        for(int i=0; i<variable.length; i++) {
            if(s1.equals(variable[i])) {
                pos1 = i;
            }
            if(s2.equals(variable[i])) {
                pos2 = i;
            }
        }

        switch(tipo) {
          case "igualdad":
            ipbc(cntIns + ": iload_"+pos1);
            ipbc(cntIns + ": iload_"+pos2);
            ipbc(cntIns + ": ifne " + (cntIns+4));
            jmp1 = cntBC;
          break;

          case "suma":
            ipbc(cntIns + ": iload_"+pos1);
            ipbc(cntIns + ": iload_"+pos2);
            ipbc(cntIns + ": iadd");
            jmp2 = cntBC;
          break;
        }
    }

    public void byteCode(String tipo, String s1) {
        int pos1 = -1;
        for(int i=0; i<variable.length; i++) {
            if(s1.equals(variable[i])) {
                pos1 = i;
            }
        }
        switch(tipo) {
            case "igual":
                pilaBC[cntBC+3] = cntIns+4 + ": istore_" + pos1;
                cntIns++;
                jmp2 = cntBC;
            break;
        }
    }

    public void ipbc(String ins) {
        while(pilaBC[cntBC] != null) {
            cntBC++;
        }
        cntIns++;
        pilaBC[cntBC] = ins;
        cntBC++;
    }

    public String getBytecode() {
        String JBC = "";
        for(int i=0; i<pilaBC.length; i++) {
            if(pilaBC[i] != null){
                JBC = JBC + pilaBC[i] + "\n";
            }
        }
        return JBC;
    }
}

