import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

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
// --- Información de funciones ---
// ------------------------------------------------------------------------------------------------

class FunctionInfo {
    String name;
    String returnType;
    List<String> paramTypes;
    
    FunctionInfo(String name, String returnType, List<String> paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }
}

// ------------------------------------------------------------------------------------------------
// --- CLASE SymbolTableBuilder PARA FUNCIONES ---
// ------------------------------------------------------------------------------------------------

class SymbolTableBuilder implements ASTVisitor {
    private HashMap<String, FunctionInfo> functions = new HashMap<>();
    private SymbolTable currentScope = new SymbolTable();
    private String currentFunctionReturnType = null;
    private boolean hasReturn = false;
    private int errorCount = 0;

    public int getErrorCount() { return errorCount; }

    @Override
    public void visit(ProgramNode node) {
        System.out.println("=== Fase 1: Registro de funciones ===");
        
        // Paso 1: Registrar todas las funciones
        if (node.functions != null) {
            for (FunctionNode func : node.functions.functions) {
                List<String> paramTypes = new ArrayList<>();
                if (func.params != null && func.params.params != null) {
                    for (ParamNode param : func.params.params) {
                        paramTypes.add(param.type);
                    }
                }
                
                if (functions.containsKey(func.name)) {
                    System.err.println("Error: función '" + func.name + "' ya declarada.");
                    errorCount++;
                } else {
                    functions.put(func.name, new FunctionInfo(func.name, func.returnType, paramTypes));
                    System.out.println("Registrada función: " + func.returnType + " " + func.name + 
                                     "(" + paramTypes.size() + " parámetros)");
                }
            }
        }
        
        // Verificar que existe main
        if (!functions.containsKey("main")) {
            System.err.println("Error: No se encontró la función 'main'");
            errorCount++;
        }
        
        System.out.println("\n=== Fase 2: Validación semántica de funciones ===");
        
        // Paso 2: Validar cada función
        if (node.functions != null) {
            for (FunctionNode func : node.functions.functions) {
                func.accept(this);
            }
        }
        
        if (errorCount == 0) {
            System.out.println("\n✅ Análisis semántico completado sin errores");
        } else {
            System.err.println("\n❌ Se encontraron " + errorCount + " errores semánticos");
        }
    }

    @Override
    public void visit(FunctionListNode node) {
        // Manejado en ProgramNode
    }

    @Override
    public void visit(FunctionNode node) {
        System.out.println("\nValidando función: " + node.returnType + " " + node.name + "()");
        
        // Crear nuevo scope para la función
        currentScope = new SymbolTable();
        currentFunctionReturnType = node.returnType;
        hasReturn = false;
        
        // Agregar parámetros al scope
        if (node.params != null && node.params.params != null) {
            for (ParamNode param : node.params.params) {
                currentScope.add(param.name, param.type);
                currentScope.setInitialized(param.name);  // Los parámetros vienen inicializados
            }
        }
        
        // Validar el cuerpo de la función
        node.body.accept(this);
        
        // Verificar que funciones int tengan return
        if (node.returnType.equals("int") && !hasReturn) {
            System.err.println("Error: función '" + node.name + "' de tipo 'int' debe tener al menos un return con valor");
            errorCount++;
        }
    }

    @Override
    public void visit(ParamListNode node) {
        // Manejado en FunctionNode
    }

    @Override
    public void visit(ParamNode node) {
        // Manejado en FunctionNode
    }

    @Override
    public void visit(BlockNode node) {
        // Paso 1: Declarar todas las variables locales
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                currentScope.add(d.name, d.type);
            }
        }
        
        // Paso 2: Marcar las que tienen inicialización
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                if (d.init != null) {
                    currentScope.setInitialized(d.name);
                }
            }
        }
        
        // Paso 3: Validar las expresiones de inicialización
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                if (d.init != null) {
                    String initType = getExprType(d.init);
                    if (!initType.equals("error") && !initType.equals(d.type)) {
                        System.err.println("Error: no se puede inicializar '" + d.name + 
                                         "' de tipo '" + d.type + "' con expresión de tipo '" + initType + "'");
                        errorCount++;
                    }
                }
            }
        }
        
        // Paso 4: Validar sentencias
        if (node.stmts != null) {
            node.stmts.accept(this);
        }
    }

    @Override
    public void visit(DeclNode node) {
        // Manejado en BlockNode
    }

    @Override
    public void visit(DeclListNode node) {
        // Manejado en BlockNode
    }

    @Override
    public void visit(StmtListNode node) {
        for (StmtNode s : node.stmts) {
            s.accept(this);
        }
    }

    @Override
    public void visit(AssignNode node) {
        SymbolInfo var = currentScope.lookup(node.name);
        if (var == null) {
            System.err.println("Error: variable '" + node.name + "' no declarada");
            errorCount++;
        } else {
            String exprType = getExprType(node.expr);
            if (!exprType.equals("error") && !exprType.equals(var.getType())) {
                System.err.println("Error: no se puede asignar expresión de tipo '" + exprType + 
                                 "' a variable '" + node.name + "' de tipo '" + var.getType() + "'");
                errorCount++;
            }
            var.setInitialized(true);
        }
    }

    @Override
    public void visit(IfNode node) {
        String condType = getExprType(node.condition);
        if (!condType.equals("error") && !condType.equals("int")) {
            System.err.println("Error: condición del 'if' debe ser de tipo 'int'");
            errorCount++;
        }
        
        if (node.thenBlock != null) node.thenBlock.accept(this);
        if (node.elseBlock != null) node.elseBlock.accept(this);
    }

    @Override
    public void visit(WhileNode node) {
        String condType = getExprType(node.condition);
        if (!condType.equals("error") && !condType.equals("int")) {
            System.err.println("Error: condición del 'while' debe ser de tipo 'int'");
            errorCount++;
        }
        
        if (node.body != null) node.body.accept(this);
    }

    @Override
    public void visit(ReturnNode node) {
        hasReturn = true;
        
        if (currentFunctionReturnType.equals("void")) {
            if (node.expr != null) {
                System.err.println("Error: función 'void' no debe retornar un valor");
                errorCount++;
            }
        } else if (currentFunctionReturnType.equals("int")) {
            if (node.expr == null) {
                System.err.println("Error: función 'int' debe retornar un valor");
                errorCount++;
            } else {
                String returnType = getExprType(node.expr);
                if (!returnType.equals("error") && !returnType.equals("int")) {
                    System.err.println("Error: return debe ser de tipo 'int', se encontró '" + returnType + "'");
                    errorCount++;
                }
            }
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
        SymbolInfo s = currentScope.lookup(node.name);
        if (s == null) {
            System.err.println("Error: variable '" + node.name + "' no declarada");
            errorCount++;
        }
    }

    @Override
    public void visit(CallNode node) {
        getExprType(node);
    }

    private String getExprType(ExprNode e) {
        if (e instanceof NumNode) {
            return "int";
        }

        if (e instanceof IdNode) {
            IdNode id = (IdNode) e;
            SymbolInfo s = currentScope.lookup(id.name);

            if (s == null) {
                System.err.println("Error: variable '" + id.name + "' no declarada");
                errorCount++;
                return "error";
            }

            if (!s.isInitialized()) {
                System.err.println("Error: variable '" + id.name + "' puede no haber sido inicializada");
                errorCount++;
            }
            return s.getType();
        }

        if (e instanceof CallNode) {
            CallNode call = (CallNode) e;
            FunctionInfo func = functions.get(call.functionName);
            
            if (func == null) {
                System.err.println("Error: función '" + call.functionName + "' no declarada");
                errorCount++;
                return "error";
            }
            
            // Verificar número de argumentos
            if (call.args.size() != func.paramTypes.size()) {
                System.err.println("Error: función '" + call.functionName + "' espera " + 
                                 func.paramTypes.size() + " argumentos, se pasaron " + call.args.size());
                errorCount++;
                return func.returnType;
            }
            
            // Verificar tipos de argumentos
            for (int i = 0; i < call.args.size(); i++) {
                String argType = getExprType(call.args.get(i));
                String expectedType = func.paramTypes.get(i);
                
                if (!argType.equals("error") && !argType.equals(expectedType)) {
                    System.err.println("Error: argumento " + (i+1) + " de función '" + call.functionName + 
                                     "' debe ser de tipo '" + expectedType + "', se pasó '" + argType + "'");
                    errorCount++;
                }
            }
            
            return func.returnType;
        }

        if (e instanceof BinOpNode) {
            BinOpNode b = (BinOpNode) e;
            String lt = getExprType(b.left);
            String rt = getExprType(b.right);
            
            if (!lt.equals("error") && !rt.equals("error")) {
                if (!lt.equals("int") || !rt.equals("int")) {
                    System.err.println("Error: operador '" + b.op + "' requiere operandos de tipo 'int'");
                    errorCount++;
                    return "error";
                }
            }
            return "int";
        }

        if (e instanceof UnaryOpNode) {
            UnaryOpNode u = (UnaryOpNode) e;
            String t = getExprType(u.expr);
            
            if (!t.equals("error") && !t.equals("int")) {
                System.err.println("Error: operador unario '" + u.op + "' requiere operando de tipo 'int'");
                errorCount++;
                return "error";
            }
            return "int";
        }
        
        return "error";
    }
}
