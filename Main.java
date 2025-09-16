import java.io.FileReader;

public class Main {
    public static void main(String[] argv) {
        try {
            parser p = new parser(new Lexer(new FileReader("test.txt")));
            ProgramNode root = (ProgramNode) p.parse().value;

            // Paso 1: análisis semántico
            SymbolTableBuilder stb = new SymbolTableBuilder();
            root.accept(stb);

            // Paso 2: ejecución
            Interpreter interp = new Interpreter();
            root.accept(interp);

            // 3. assembler
            CodeGenerator gen = new CodeGenerator();
            root.accept(gen);

            System.out.println("=== Pseudo-Assembly ===");
            for (String instr : gen.getCode()) {
                System.out.println(instr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
