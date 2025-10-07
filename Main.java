import java.io.*;

public class Main {
    public static void main(String[] argv) {
        try {
            System.out.println("=== Etapa 1: Parsing ===");
            parser p = new parser(new Lexer(new FileReader("test.txt")));
            ProgramNode root = (ProgramNode) p.parse().value;
            System.out.println("Parsing completado sin errores ✅\n");

            // Etapa 2: Análisis semántico
            System.out.println("=== Etapa 2: Análisis Semántico ===");
            SymbolTableBuilder stb = new SymbolTableBuilder();
            root.accept(stb);
            System.out.println("Análisis semántico completado ✅\n");

            // Etapa 3: Interpretación simbólica (opcional)
            System.out.println("=== Etapa 3: Ejecución simbólica ===");
            Interpreter interp = new Interpreter();
            root.accept(interp);
            System.out.println("Interpretación finalizada ✅\n");

            // Etapa 4: Generación de código ensamblador (x86-64 para Windows)
            System.out.println("=== Etapa 4: Generación de código (x86-64 Windows) ===");
            X86_64Generator gen = new X86_64Generator();
            root.accept(gen);

            String asm = gen.getAsm();
            try (PrintWriter out = new PrintWriter("program.asm")) {
                out.print(asm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
