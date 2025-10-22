import java.util.HashMap;

class SymbolTable {
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
            s.setInitialized(true);
            System.out.println("Asignación: " + name + " = " + value);
        }
    }

    public void setInitialized(String name) {
        SymbolInfo s = table.get(name);
        if (s != null) {
            s.setInitialized(true);
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
    private boolean isInitialized;

    public SymbolInfo(String name, String type) {
        this.name = name;
        this.type = type;
        this.value = null;
        this.isInitialized = false;
    }

    public void setValue(Integer v) { this.value = v; }
    public Integer getValue() { return value; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isInitialized() { return isInitialized; }
    public void setInitialized(boolean init) { this.isInitialized = init; }

    @Override
    public String toString() {
        return name + " : " + type + " = " + (value != null ? value : "undef");
    }
}

// ------------------------------------------------------------------------------------------------
// --- CLASE SymbolTableBuilder (CORREGIDA) ---
// ------------------------------------------------------------------------------------------------

class SymbolTableBuilder implements ASTVisitor {
    private SymbolTable symtab = new SymbolTable();

    public SymbolTable getTable() { return symtab; }

    @Override
    public void visit(ProgramNode node) {
        // PASO 1: Declarar TODAS las variables
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                symtab.add(d.name, d.type);
            }
        }

        // PASO 2.1: Marcar como 'initialized' todas las variables con inicializador en su declaración
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                if (d.init != null) {
                    symtab.setInitialized(d.name);
                }
            }
        }

        // PASO 2.2: Validar las expresiones de esas inicializaciones
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                if (d.init != null) {
                    getExprType(d.init);
                }
            }
        }

        // PASO 2.3: Chequear las sentencias normales
        if (node.stmts != null) {
            node.stmts.accept(this);
        }
    }

    @Override
    public void visit(BlockNode node) {
        // Lógica corregida de tres pases, igual que en ProgramNode.
        if (node.decls != null) {
            // 1. Declarar
            for (DeclNode d : node.decls.decls) {
                symtab.add(d.name, d.type);
            }
            // 2. Marcar inicializaciones
            for (DeclNode d : node.decls.decls) {
                if (d.init != null) {
                    symtab.setInitialized(d.name);
                }
            }
            // 3. Chequear expresiones de inicialización
            for (DeclNode d : node.decls.decls) {
                if (d.init != null) {
                    getExprType(d.init);
                }
            }
        }
        // 4. Chequear sentencias del bloque
        if (node.stmts != null) node.stmts.accept(this);
    }

    @Override
    public void visit(DeclNode node) { /* Lógica movida a ProgramNode/BlockNode */ }

    @Override
    public void visit(DeclListNode node) { /* Lógica movida a ProgramNode/BlockNode */ }

    @Override
    public void visit(StmtListNode node) {
        for (StmtNode s : node.stmts) s.accept(this);
    }

    @Override
    public void visit(AssignNode node) {
        SymbolInfo var = symtab.lookup(node.name);
        if (var == null) {
            System.err.println("Error semántico: variable '" + node.name + "' no declarada.");
        } else {
            // Primero, valida la expresión del lado derecho.
            String exprType = getExprType(node.expr);
            if (!exprType.equals(var.getType())) {
                System.err.println("Error de tipos: no se puede asignar " +
                        exprType + " a variable " + var.getType());
            }

            // LÍNEA CLAVE AÑADIDA:
            // Después de una asignación exitosa, la variable queda inicializada.
            var.setInitialized(true);
        }
    }

    @Override
    public void visit(IfNode node) {
        String condType = getExprType(node.condition);
        if (!condType.equals("int")) {
            System.err.println("Error: condición del if debe ser de tipo int");
        }
        if (node.thenBlock != null) node.thenBlock.accept(this);
        if (node.elseBlock != null) node.elseBlock.accept(this);
    }

    @Override
    public void visit(WhileNode node) {
        String condType = getExprType(node.condition);
        if (!condType.equals("int")) {
            System.err.println("Error: condición del while debe ser de tipo int");
        }
        if (node.body != null) node.body.accept(this);
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.expr != null) {
            getExprType(node.expr);
        }
    }

    @Override
    public void visit(BinOpNode node) {
        getExprType(node);
    }

    @Override
    public void visit(UnaryOpNode node) {
        getExprType(node);
    }

    @Override
    public void visit(NumNode node) { }

    @Override
    public void visit(IdNode node) {
        SymbolInfo s = symtab.lookup(node.name);
        if (s == null) {
            System.err.println("Error semántico: variable '" + node.name + "' no declarada.");
        }
    }

    private String getExprType(ExprNode e) {
        if (e instanceof NumNode) return "int";

        if (e instanceof IdNode) {
            IdNode id = (IdNode) e;
            SymbolInfo s = symtab.lookup(id.name);

            if (s == null) {
                System.err.println("Error semántico: variable '" + id.name + "' no declarada.");
                return "error";
            }

            if (!s.isInitialized()) {
                System.err.println("Error semántico: variable '" + id.name + "' puede no haber sido inicializada.");
            }
            return s.getType();
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

        if (e instanceof UnaryOpNode) {
            UnaryOpNode u = (UnaryOpNode) e;
            String t = getExprType(u.expr);
            if (!t.equals("int")) {
                System.err.println("Error de tipos en operación unaria '" + u.op + "'");
                return "error";
            }
            return "int";
        }
        return "error";
    }
}