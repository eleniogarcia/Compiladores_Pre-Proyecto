import java.util.*;

interface ASTNode {
    void accept(ASTVisitor v);
}

/* ---------------- Nodos principales ---------------- */

class ProgramNode implements ASTNode {
    FunctionListNode functions;
    
    ProgramNode(FunctionListNode f) {
        this.functions = f;
    }
    
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

/* ---------------- Funciones ---------------- */

class FunctionListNode implements ASTNode {
    List<FunctionNode> functions = new ArrayList<>();
    
    void add(FunctionNode f) { functions.add(f); }
    
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class FunctionNode implements ASTNode {
    String returnType;  // "int" o "void"
    String name;
    ParamListNode params;
    BlockNode body;
    
    FunctionNode(String returnType, String name, ParamListNode params, BlockNode body) {
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class ParamListNode implements ASTNode {
    List<ParamNode> params = new ArrayList<>();
    
    void add(ParamNode p) { params.add(p); }
    
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class ParamNode implements ASTNode {
    String type;
    String name;
    
    ParamNode(String type, String name) {
        this.type = type;
        this.name = name;
    }
    
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

/* ---------------- Declaraciones de variables ---------------- */

class DeclListNode implements ASTNode {
    List<DeclNode> decls = new ArrayList<>();

    void add(DeclNode d) { decls.add(d); }

    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class DeclNode implements ASTNode {
    String name;
    String type;
    ExprNode init;

    DeclNode(String name, String type) {
        this.name = name;
        this.type = type;
        this.init = null;
    }

    DeclNode(String name, String type, ExprNode init) {
        this.name = name;
        this.type = type;
        this.init = init;
    }

    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

/* ---------------- Sentencias ---------------- */

class StmtListNode implements ASTNode {
    List<StmtNode> stmts = new ArrayList<>();
    void add(StmtNode s) { stmts.add(s); }
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

abstract class StmtNode implements ASTNode { }

class AssignNode extends StmtNode {
    String name;
    ExprNode expr;
    AssignNode(String name, ExprNode expr) { this.name = name; this.expr = expr; }
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class BlockNode implements ASTNode {
    DeclListNode decls;
    StmtListNode stmts;

    BlockNode(DeclListNode d, StmtListNode s) {
        this.decls = d;
        this.stmts = s;
    }

    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class IfNode extends StmtNode {
    ExprNode condition;
    BlockNode thenBlock;
    BlockNode elseBlock;

    IfNode(ExprNode cond, BlockNode thenBlk, BlockNode elseBlk) {
        this.condition = cond;
        this.thenBlock = thenBlk;
        this.elseBlock = elseBlk;
    }

    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class WhileNode extends StmtNode {
    ExprNode condition;
    BlockNode body;

    WhileNode(ExprNode cond, BlockNode body) {
        this.condition = cond;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class ReturnNode extends StmtNode {
    ExprNode expr;  // null para "return;" en funciones void

    ReturnNode(ExprNode e) { this.expr = e; }

    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

/* ---------------- Expresiones ---------------- */

abstract class ExprNode implements ASTNode { }

class BinOpNode extends ExprNode {
    String op;
    ExprNode left, right;
    BinOpNode(String op, ExprNode l, ExprNode r) { this.op = op; this.left = l; this.right = r; }
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class UnaryOpNode extends ExprNode {
    String op;
    ExprNode expr;

    UnaryOpNode(String op, ExprNode e) { this.op = op; this.expr = e; }

    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class NumNode extends ExprNode {
    int value;
    NumNode(int v) { this.value = v; }
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class IdNode extends ExprNode {
    String name;
    IdNode(String n) { this.name = n; }
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class CallNode extends ExprNode {
    String functionName;
    List<ExprNode> args;
    
    CallNode(String functionName, List<ExprNode> args) {
        this.functionName = functionName;
        this.args = args;
    }
    
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

/* ---------------- Visitor ---------------- */

interface ASTVisitor {
    void visit(ProgramNode node);
    void visit(FunctionListNode node);
    void visit(FunctionNode node);
    void visit(ParamListNode node);
    void visit(ParamNode node);
    void visit(BlockNode node);
    void visit(DeclListNode node);
    void visit(DeclNode node);
    void visit(StmtListNode node);
    void visit(AssignNode node);
    void visit(IfNode node);
    void visit(WhileNode node);
    void visit(ReturnNode node);
    void visit(BinOpNode node);
    void visit(UnaryOpNode node);
    void visit(NumNode node);
    void visit(IdNode node);
    void visit(CallNode node);
}

/* ---------------- Interpreter ---------------- */

class Interpreter implements ASTVisitor {
    private Map<String, FunctionNode> functions = new HashMap<>();
    private SymbolTable globalSymtab = new SymbolTable();
    private Stack<SymbolTable> callStack = new Stack<>();
    private SymbolTable currentSymtab;
    private Integer returnValue = null;
    
    public Interpreter() {
        currentSymtab = globalSymtab;
    }

    @Override
    public void visit(ProgramNode node) {
        // Paso 1: Registrar todas las funciones
        if (node.functions != null) {
            for (FunctionNode func : node.functions.functions) {
                functions.put(func.name, func);
                System.out.println("Registrada función: " + func.returnType + " " + func.name);
            }
        }
        
        // Paso 2: Ejecutar main
        FunctionNode mainFunc = functions.get("main");
        if (mainFunc == null) {
            throw new RuntimeException("No se encontró la función 'main'");
        }
        
        System.out.println("\n=== Ejecutando main() ===");
        executeFunction(mainFunc, new ArrayList<>());
        
        System.out.println("\n=== Tabla de símbolos global ===");
        globalSymtab.printTable();
    }

    private Integer executeFunction(FunctionNode func, List<Integer> argValues) {
        // Crear nuevo contexto para la función
        SymbolTable functionSymtab = new SymbolTable();
        callStack.push(currentSymtab);
        currentSymtab = functionSymtab;
        returnValue = null;
        
        // Asignar argumentos a parámetros
        if (func.params != null && func.params.params != null) {
            for (int i = 0; i < func.params.params.size(); i++) {
                ParamNode param = func.params.params.get(i);
                Integer argValue = argValues.get(i);
                currentSymtab.add(param.name, param.type);
                currentSymtab.assign(param.name, argValue);
            }
        }
        
        // Ejecutar el cuerpo de la función
        func.body.accept(this);
        
        // Restaurar contexto anterior
        currentSymtab = callStack.pop();
        
        return returnValue;
    }

    @Override
    public void visit(FunctionListNode node) {
        for (FunctionNode f : node.functions) {
            f.accept(this);
        }
    }

    @Override
    public void visit(FunctionNode node) {
        // Ya registrada en ProgramNode
    }

    @Override
    public void visit(ParamListNode node) { }

    @Override
    public void visit(ParamNode node) { }

    @Override
    public void visit(BlockNode node) {
        // Declarar e inicializar variables locales
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                currentSymtab.add(d.name, d.type);
                
                if (d.init != null) {
                    int value = eval(d.init);
                    currentSymtab.assign(d.name, value);
                }
            }
        }

        // Ejecutar sentencias
        if (node.stmts != null) {
            node.stmts.accept(this);
        }
    }

    @Override
    public void visit(DeclListNode node) { }

    @Override
    public void visit(DeclNode node) { }

    @Override
    public void visit(StmtListNode node) {
        for (StmtNode s : node.stmts) {
            if (returnValue != null) break;  // Si ya hay return, no ejecutar más
            s.accept(this);
        }
    }

    @Override
    public void visit(AssignNode node) {
        int value = eval(node.expr);
        currentSymtab.assign(node.name, value);
    }

    @Override
    public void visit(IfNode node) {
        int cond = eval(node.condition);
        if (cond != 0) {
            node.thenBlock.accept(this);
        } else if (node.elseBlock != null) {
            node.elseBlock.accept(this);
        }
    }

    @Override
    public void visit(WhileNode node) {
        while (eval(node.condition) != 0) {
            node.body.accept(this);
            if (returnValue != null) break;  // Si hay return en el loop, salir
        }
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.expr != null) {
            returnValue = eval(node.expr);
            System.out.println("Return: " + returnValue);
        } else {
            returnValue = 0;  // void return
            System.out.println("Return (void)");
        }
    }

    @Override
    public void visit(BinOpNode node) { }
    @Override
    public void visit(UnaryOpNode node) { }
    @Override
    public void visit(NumNode node) { }
    @Override
    public void visit(IdNode node) { }
    @Override
    public void visit(CallNode node) { }

    private int eval(ExprNode e) {
        if (e instanceof NumNode) {
            return ((NumNode) e).value;
        }
        
        if (e instanceof IdNode) {
            SymbolInfo s = currentSymtab.lookup(((IdNode) e).name);
            if (s == null || s.getValue() == null) {
                throw new RuntimeException("Variable no inicializada: " + ((IdNode) e).name);
            }
            return s.getValue();
        }
        
        if (e instanceof CallNode) {
            CallNode call = (CallNode) e;
            FunctionNode func = functions.get(call.functionName);
            if (func == null) {
                throw new RuntimeException("Función no definida: " + call.functionName);
            }
            
            // Evaluar argumentos
            List<Integer> argValues = new ArrayList<>();
            for (ExprNode arg : call.args) {
                argValues.add(eval(arg));
            }
            
            // Ejecutar función
            Integer result = executeFunction(func, argValues);
            return result != null ? result : 0;
        }
        
        if (e instanceof BinOpNode) {
            BinOpNode b = (BinOpNode) e;
            int l = eval(b.left);
            int r = eval(b.right);
            switch (b.op) {
                case "+": return l + r;
                case "-": return l - r;
                case "*": return l * r;
                case "/":
                    if (r == 0) throw new RuntimeException("División por cero");
                    return l / r;
                case "==": return l == r ? 1 : 0;
                case "<": return l < r ? 1 : 0;
                case ">": return l > r ? 1 : 0;
                case "&&": return (l != 0 && r != 0) ? 1 : 0;
                case "||": return (l != 0 || r != 0) ? 1 : 0;
            }
        }
        
        if (e instanceof UnaryOpNode) {
            UnaryOpNode u = (UnaryOpNode) e;
            int val = eval(u.expr);
            switch (u.op) {
                case "-": return -val;
                case "!": return val == 0 ? 1 : 0;
            }
        }
        
        throw new RuntimeException("Expresión no soportada: " + e);
    }
}

/* ---------------- X86_64Generator (simplificado para múltiples funciones) ---------------- */

class X86_64Generator implements ASTVisitor {
    private final StringBuilder text = new StringBuilder();
    private final Map<String, Integer> varOffsets = new LinkedHashMap<>();
    private int stackOffset = 0;
    private int labelCounter = 0;
    private String currentFunction = null;
    private boolean hasReturn = false;

    public String getAsm() {
        return text.toString();
    }

    private void emit(String s) {
        text.append("        ").append(s).append("\n");
    }

    private void emitLabel(String label) {
        text.append(label).append(":\n");
    }

    private String newLabel(String prefix) {
        return prefix + "_" + (labelCounter++);
    }

    @Override
    public void visit(ProgramNode node) {
        text.append(".text\n");
        text.append(".globl main\n\n");
        
        if (node.functions != null) {
            for (FunctionNode func : node.functions.functions) {
                func.accept(this);
            }
        }
    }

    @Override
    public void visit(FunctionListNode node) { }

    @Override
    public void visit(FunctionNode node) {
        currentFunction = node.name;
        varOffsets.clear();
        stackOffset = 0;
        hasReturn = false;
        
        // Prólogo de función
        emitLabel(node.name);
        emit("pushq   %rbp");
        emit("movq    %rsp, %rbp");
        
        // Guardar parámetros en la pila
        String[] paramRegs = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};
        if (node.params != null && node.params.params != null) {
            for (int i = 0; i < node.params.params.size() && i < 6; i++) {
                ParamNode param = node.params.params.get(i);
                stackOffset -= 8;
                varOffsets.put(param.name, stackOffset);
            }
        }
        
        // Primera pasada: contar variables locales para reservar espacio
        int localVarCount = countLocalVars(node.body);
        int totalStackSize = Math.abs(stackOffset) + (localVarCount * 8);
        
        // Alinear a 16 bytes (requerido por ABI x86-64)
        if ((totalStackSize % 16) != 0) {
            totalStackSize += 16 - (totalStackSize % 16);
        }
        
        if (totalStackSize > 0) {
            emit("subq    $" + totalStackSize + ", %rsp");
        }
        
        // Guardar parámetros desde registros
        if (node.params != null && node.params.params != null) {
            for (int i = 0; i < node.params.params.size() && i < 6; i++) {
                ParamNode param = node.params.params.get(i);
                int offset = varOffsets.get(param.name);
                emit("movq    " + paramRegs[i] + ", " + offset + "(%rbp)");
            }
        }
        
        // Visitar cuerpo
        node.body.accept(this);
        
        // Epílogo por defecto (solo si no hubo return explícito)
        if (!hasReturn) {
            if (node.returnType.equals("int")) {
                emit("movq    $0, %rax");
            }
            emit("leave");
            emit("ret");
        }
        text.append("\n");
    }
    
    private int countLocalVars(BlockNode block) {
        if (block.decls == null) return 0;
        return block.decls.decls.size();
    }

    @Override
    public void visit(ParamListNode node) { }

    @Override
    public void visit(ParamNode node) { }

    @Override
    public void visit(BlockNode node) {
        if (node.decls != null) node.decls.accept(this);
        if (node.stmts != null) node.stmts.accept(this);
    }

    @Override
    public void visit(DeclListNode node) {
        for (DeclNode d : node.decls) d.accept(this);
    }

    @Override
    public void visit(DeclNode node) {
        stackOffset -= 8;
        varOffsets.put(node.name, stackOffset);

        if (node.init != null) {
            generateExpr(node.init);
            emit("movq    %rax, " + stackOffset + "(%rbp)");
        }
    }

    @Override
    public void visit(StmtListNode node) {
        for (StmtNode s : node.stmts) s.accept(this);
    }

    @Override
    public void visit(AssignNode node) {
        generateExpr(node.expr);
        int offset = varOffsets.get(node.name);
        emit("movq    %rax, " + offset + "(%rbp)");
    }

    @Override
    public void visit(IfNode node) {
        String elseLabel = newLabel("L_else");
        String endLabel = newLabel("L_end");

        generateExpr(node.condition);
        emit("cmpq    $0, %rax");
        emit("je      " + elseLabel);

        node.thenBlock.accept(this);
        emit("jmp     " + endLabel);

        emitLabel(elseLabel);
        if (node.elseBlock != null) {
            node.elseBlock.accept(this);
        }

        emitLabel(endLabel);
    }

    @Override
    public void visit(WhileNode node) {
        String startLabel = newLabel("L_while");
        String endLabel = newLabel("L_end");

        emitLabel(startLabel);
        generateExpr(node.condition);
        emit("cmpq    $0, %rax");
        emit("je      " + endLabel);

        node.body.accept(this);
        emit("jmp     " + startLabel);

        emitLabel(endLabel);
    }

    @Override
    public void visit(ReturnNode node) {
        hasReturn = true;
        if (node.expr != null) {
            generateExpr(node.expr);
        }
        emit("leave");
        emit("ret");
    }

    @Override
    public void visit(BinOpNode node) { }
    @Override
    public void visit(UnaryOpNode node) { }
    @Override
    public void visit(NumNode node) { }
    @Override
    public void visit(IdNode node) { }
    @Override
    public void visit(CallNode node) { }

    private void generateExpr(ExprNode e) {
        if (e instanceof NumNode) {
            emit("movq    $" + ((NumNode) e).value + ", %rax");
        } else if (e instanceof IdNode) {
            Integer offset = varOffsets.get(((IdNode) e).name);
            if (offset != null) {
                emit("movq    " + offset + "(%rbp), %rax");
            }
        } else if (e instanceof CallNode) {
            CallNode call = (CallNode) e;
            String[] argRegs = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};
            
            // Alinear stack a 16 bytes antes de call (si es necesario)
            emit("# Preparando llamada a " + call.functionName);
            
            // Evaluar y colocar argumentos en registros (de izquierda a derecha)
            for (int i = 0; i < call.args.size() && i < 6; i++) {
                generateExpr(call.args.get(i));
                if (i == 0) {
                    emit("movq    %rax, %rdi");
                } else {
                    // Guardar temporalmente en la pila y luego mover al registro
                    emit("pushq   %rax");
                }
            }
            
            // Mover argumentos apilados a sus registros correspondientes (de derecha a izquierda)
            for (int i = call.args.size() - 1; i > 0 && i < 6; i--) {
                emit("popq    " + argRegs[i]);
            }
            
            // Alinear stack a 16 bytes (requerido por ABI)
            emit("andq    $-16, %rsp");
            emit("call    " + call.functionName);
            // Resultado en %rax
        } else if (e instanceof BinOpNode) {
            BinOpNode b = (BinOpNode) e;

            generateExpr(b.left);
            emit("pushq   %rax");

            generateExpr(b.right);
            emit("popq    %rcx");

            switch (b.op) {
                case "+":
                    emit("addq    %rcx, %rax");
                    break;
                case "-":
                    emit("subq    %rax, %rcx");
                    emit("movq    %rcx, %rax");
                    break;
                case "*":
                    emit("imulq   %rcx, %rax");
                    break;
                case "/":
                    emit("movq    %rax, %rbx");
                    emit("movq    %rcx, %rax");
                    emit("cqto");
                    emit("idivq   %rbx");
                    break;
                case "==":
                    emit("cmpq    %rax, %rcx");
                    emit("sete    %al");
                    emit("movzbq  %al, %rax");
                    break;
                case "<":
                    emit("cmpq    %rax, %rcx");
                    emit("setl    %al");
                    emit("movzbq  %al, %rax");
                    break;
                case ">":
                    emit("cmpq    %rax, %rcx");
                    emit("setg    %al");
                    emit("movzbq  %al, %rax");
                    break;
            }
        } else if (e instanceof UnaryOpNode) {
            UnaryOpNode u = (UnaryOpNode) e;
            generateExpr(u.expr);

            switch (u.op) {
                case "-":
                    emit("negq    %rax");
                    break;
                case "!":
                    emit("cmpq    $0, %rax");
                    emit("sete    %al");
                    emit("movzbq  %al, %rax");
                    break;
            }
        }
    }
}
