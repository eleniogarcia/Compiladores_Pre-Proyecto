 import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, SymbolInfo> table = new HashMap<>();

    public void add(String name, String type) {
        if (table.containsKey(name)) {
            System.err.println("Error: variable '" + name + "' ya declarada.");
        } else {
            table.put(name, new SymbolInfo(name, type));
            System.out.println("Se agregó '" + name + "' de tipo " + type + " a la tabla de símbolos.");
        }
    }

    public SymbolInfo lookup(String name) {
        return table.get(name);
    }

    public void assign(String name, Integer value) {
        SymbolInfo s = table.get(name);
        if (s == null) {
            System.err.println("Error: variable '" + name + "' no declarada.");
        } else {
            s.setValue(value);
            System.out.println("Asignación: " + name + " = " + value);
        }
    }

    public void printTable() {
        System.out.println("Tabla de símbolos:");
        for (SymbolInfo s : table.values()) {
            System.out.println(s);
        }
    }
}

class SymbolInfo {
    private final String name;
    private final String type;
    private Integer value;

    public SymbolInfo(String name, String type) {
        this.name = name;
        this.type = type;
        this.value = null;
    }

    public void setValue(Integer v) { this.value = v; }
    public Integer getValue() { return value; }
    public String getName() { return name; }
    public String getType() { return type; }

    @Override
    public String toString() {
        return name + " : " + type + " = " + (value != null ? value : "undef");
    }
}

 class SymbolTableBuilder implements ASTVisitor {
     private SymbolTable symtab = new SymbolTable();

     public SymbolTable getTable() { return symtab; }

     @Override
     public void visit(ProgramNode node) {
         if (node.decls != null) node.decls.accept(this);
         if (node.stmts != null) node.stmts.accept(this);
         //symtab.printTable();
     }

     @Override
     public void visit(DeclListNode node) {
         for (DeclNode d : node.decls) d.accept(this);
     }

     @Override
     public void visit(DeclNode node) {
         symtab.add(node.name, node.type);
     }

     @Override
     public void visit(StmtListNode node) {
         for (StmtNode s : node.stmts) s.accept(this);
     }

     @Override
     public void visit(AssignNode node) {
         SymbolInfo var = symtab.lookup(node.name);
         if (var == null) {
             System.err.println("Error: variable '" + node.name + "' no declarada.");
         } else {
             String exprType = getExprType(node.expr);
             if (!exprType.equals(var.getType())) {
                 System.err.println("Error de tipos: no se puede asignar " +
                         exprType + " a variable " + var.getType());
             }
         }
     }

     @Override
     public void visit(BinOpNode node) {
         // igual que con assign, usamos getExprType
         getExprType(node);
     }

     @Override
     public void visit(NumNode node) {
         // siempre int
     }

     @Override
     public void visit(IdNode node) {
         if (symtab.lookup(node.name) == null) {
             System.err.println("Error: variable '" + node.name + "' no declarada.");
         }

     }

     /* ---- método auxiliar para expresiones ---- */
     private String getExprType(ExprNode e) {
         if (e instanceof NumNode) return "int";
         if (e instanceof IdNode) {
             IdNode id = (IdNode) e;
             SymbolInfo s = symtab.lookup(id.name);
             return (s != null) ? s.getType() : "error";
         }
         if (e instanceof BinOpNode) {
             BinOpNode b = (BinOpNode) e;
             String lt = getExprType(b.left);
             String rt = getExprType(b.right);
             if (!lt.equals("int") || !rt.equals("int")) {
                 System.err.println("Error de tipos en operación '" + b.op + "'");
                 return "error";
             }
             return "int";
         }
         return "error";
     }
 }




