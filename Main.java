import java.io.*;

public class Main {
    public static void main(String[] argv) {
        try {
            System.out.println("=== Etapa 1: Parsing ===");
            parser p = new parser(new Lexer(new FileReader("test.txt")));
            ProgramNode root = (ProgramNode) p.parse().value;
            System.out.println("Parsing completado sin errores\n");

            // Etapa 2: Análisis semántico
            System.out.println("=== Etapa 2: Análisis Semántico ===");
            SymbolTableBuilder stb = new SymbolTableBuilder();
            root.accept(stb);
            System.out.println("Análisis semántico completado\n");

            // Verificar si hay errores semánticos
            if (stb.getErrorCount() > 0) {
                System.err.println("\n❌ COMPILACIÓN ABORTADA: Se encontraron " + stb.getErrorCount() + " errores semánticos.");
                System.err.println("Por favor corrija los errores antes de generar código.");
                System.exit(1);
            }

            // Etapa 3: Interpretación simbólica (opcional)
            System.out.println("=== Etapa 3: Ejecución simbólica ===");
            try {
                Interpreter interp = new Interpreter();
                root.accept(interp);
                System.out.println("Interpretación finalizada\n");
            } catch (RuntimeException e) {
                System.err.println("\n⚠️  Error durante la ejecución simbólica: " + e.getMessage());
                System.err.println("Continuando con la generación de código...\n");
            }

            // Etapa 4: Generación de código ensamblador (x86-64 para Windows)
            System.out.println("=== Etapa 4: Generación de código (x86-64 Windows) ===");
            X86_64Generator gen = new X86_64Generator();
            root.accept(gen);

            String asm = gen.getAsm();
            try (PrintWriter out = new PrintWriter("program.asm")) {
                out.print(asm);
            }
            
            System.out.println("\n✅ Código ensamblador generado exitosamente en 'program.asm'");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
