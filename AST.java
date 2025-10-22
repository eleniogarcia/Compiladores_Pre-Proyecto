import java.util.*;

interface ASTNode {
    void accept(ASTVisitor v);
}

/* ---------------- Nodos principales ---------------- */

class ProgramNode implements ASTNode {
    DeclListNode decls;
    StmtListNode stmts;
    ProgramNode(DeclListNode d, StmtListNode s) { this.decls = d; this.stmts = s; }
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class DeclListNode implements ASTNode {
    List<DeclNode> decls = new ArrayList<>();

    void add(DeclNode d) { decls.add(d); }

    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class DeclNode implements ASTNode {
    String name;
    String type;
    ExprNode init;  // inicialización opcional

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

class StmtListNode implements ASTNode {
    List<StmtNode> stmts = new ArrayList<>();
    void add(StmtNode s) { stmts.add(s); }
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
    ExprNode expr;

    ReturnNode(ExprNode e) { this.expr = e; }

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

/* ---------------- Sentencias ---------------- */

abstract class StmtNode implements ASTNode { }

class AssignNode extends StmtNode {
    String name;
    ExprNode expr;
    AssignNode(String name, ExprNode expr) { this.name = name; this.expr = expr; }
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

/* ---------------- Visitor ---------------- */

interface ASTVisitor {
    void visit(ProgramNode node);
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
}



/* ---------------- Interpreter ---------------- */

class Interpreter implements ASTVisitor {
    private SymbolTable symtab = new SymbolTable();

    public SymbolTable getTable() { return symtab; }

    @Override
    public void visit(ProgramNode node) {
        // PASO 1: Declarar TODAS las variables e inicializarlas INMEDIATAMENTE
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                // 1. Agregar la variable a la SymbolTable
                symtab.add(d.name, d.type);

                // 2. Si tiene inicializador, evaluarlo y asignarlo AHORA
                if (d.init != null) {
                    int value = eval(d.init);
                    symtab.assign(d.name, value);
                }
            }
        }

        // PASO 2: Ejecutar las sentencias normales
        if (node.stmts != null) {
            node.stmts.accept(this);
        }

        symtab.printTable();
    }

    @Override
    public void visit(BlockNode node) {
        // Misma lógica: declarar e inicializar inmediatamente
        if (node.decls != null) {
            for (DeclNode d : node.decls.decls) {
                symtab.add(d.name, d.type);
                
                // Si tiene inicializador, evaluarlo y asignarlo AHORA
                if (d.init != null) {
                    int value = eval(d.init);
                    symtab.assign(d.name, value);
                }
            }
        }

        if (node.stmts != null) {
            node.stmts.accept(this);
        }
    }

    @Override
    public void visit(DeclListNode node) { /* Ignorado, la lógica está en ProgramNode/BlockNode */ }

    @Override
    public void visit(DeclNode node) { /* Ignorado, la lógica está en ProgramNode/BlockNode */ }


    @Override
    public void visit(StmtListNode node) {
        for (StmtNode s : node.stmts) s.accept(this);
    }

    @Override
    public void visit(AssignNode node) {
        int value = eval(node.expr);
        // Note: symtab.assign will set the value and set isInitialized=true
        symtab.assign(node.name, value);
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
        }
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.expr != null) {
            eval(node.expr);
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

    // El método eval() no se cambia ya que contiene la lógica para lanzar la excepción.
    private int eval(ExprNode e) {
        if (e instanceof NumNode) {
            return ((NumNode) e).value;
        }
        if (e instanceof IdNode) {
            SymbolInfo s = symtab.lookup(((IdNode) e).name);
            if (s == null || s.getValue() == null) {
                // Esta es la línea donde ocurre el error (AST.java:300)
                throw new RuntimeException("Variable no inicializada: " + ((IdNode) e).name);
            }
            return s.getValue();
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

/* ---------------- CodeGenerator ---------------- */

class CodeGenerator implements ASTVisitor {
    private List<String> code = new ArrayList<>();

    public List<String> getCode() { return code; }

    private void emit(String instr) {
        code.add(instr);
    }

    @Override
    public void visit(ProgramNode node) {
        if (node.decls != null) node.decls.accept(this);
        if (node.stmts != null) node.stmts.accept(this);
    }

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
        emit("ALLOC " + node.name);
    }

    @Override
    public void visit(StmtListNode node) {
        for (StmtNode s : node.stmts) s.accept(this);
    }

    @Override
    public void visit(AssignNode node) {
        generateExpr(node.expr);
        emit("STORE " + node.name);
    }

    @Override
    public void visit(IfNode node) {
        String elseLabel = "L_else_" + System.nanoTime();
        String endLabel = "L_end_" + System.nanoTime();

        generateExpr(node.condition);
        emit("JZ " + elseLabel);
        node.thenBlock.accept(this);
        emit("JMP " + endLabel);
        emit("LABEL " + elseLabel);
        if (node.elseBlock != null) {
            node.elseBlock.accept(this);
        }
        emit("LABEL " + endLabel);
    }

    @Override
    public void visit(WhileNode node) {
        String startLabel = "L_while_" + System.nanoTime();
        String endLabel = "L_end_" + System.nanoTime();

        emit("LABEL " + startLabel);
        generateExpr(node.condition);
        emit("JZ " + endLabel);
        node.body.accept(this);
        emit("JMP " + startLabel);
        emit("LABEL " + endLabel);
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.expr != null) {
            generateExpr(node.expr);
        }
        emit("RETURN");
    }

    @Override
    public void visit(BinOpNode node) { }

    @Override
    public void visit(UnaryOpNode node) { }

    @Override
    public void visit(NumNode node) { }

    @Override
    public void visit(IdNode node) { }

    private void generateExpr(ExprNode e) {
        if (e instanceof NumNode) {
            emit("LOAD " + ((NumNode) e).value);
        } else if (e instanceof IdNode) {
            emit("LOAD_VAR " + ((IdNode) e).name);
        } else if (e instanceof BinOpNode) {
            BinOpNode b = (BinOpNode) e;
            generateExpr(b.left);
            emit("PUSH");
            generateExpr(b.right);
            emit("OP " + b.op);
        } else if (e instanceof UnaryOpNode) {
            UnaryOpNode u = (UnaryOpNode) e;
            generateExpr(u.expr);
            emit("UNARY " + u.op);
        }
    }
}

/* ---------------- X86_64Generator ---------------- */

class X86_64Generator implements ASTVisitor {
    private final List<String> text = new ArrayList<>();
    private final Map<String, Integer> varOffsets = new LinkedHashMap<>();
    private int stackOffset = 0;
    private int labelCounter = 0;

    public String getAsm() {
        StringBuilder sb = new StringBuilder();

        sb.append("main:\n");
        sb.append("        pushq   %rbp\n");
        sb.append("        movq    %rsp, %rbp\n");

        int totalVars = varOffsets.size();
        if (totalVars > 0)
            sb.append("        subq    $" + (totalVars * 8) + ", %rsp\n");

        for (String t : text)
            sb.append("        ").append(t).append("\n");

        sb.append("        movq    $0, %rax\n");
        sb.append("        leave\n");
        sb.append("        ret\n");

        return sb.toString();
    }

    private void emit(String s) { text.add(s); }

    private String newLabel(String prefix) {
        return prefix + "_" + (labelCounter++);
    }

    @Override
    public void visit(ProgramNode node) {
        if (node.decls != null) node.decls.accept(this);
        if (node.stmts != null) node.stmts.accept(this);
    }

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

        text.add(elseLabel + ":");
        if (node.elseBlock != null) {
            node.elseBlock.accept(this);
        }

        text.add(endLabel + ":");
    }

    @Override
    public void visit(WhileNode node) {
        String startLabel = newLabel("L_while");
        String endLabel = newLabel("L_end");

        text.add(startLabel + ":");
        generateExpr(node.condition);
        emit("cmpq    $0, %rax");
        emit("je      " + endLabel);

        node.body.accept(this);
        emit("jmp     " + startLabel);

        text.add(endLabel + ":");
    }

    @Override
    public void visit(ReturnNode node) {
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

    private void generateExpr(ExprNode e) {
        if (e instanceof NumNode) {
            emit("movq    $" + ((NumNode) e).value + ", %rax");
        } else if (e instanceof IdNode) {
            int offset = varOffsets.get(((IdNode) e).name);
            emit("movq    " + offset + "(%rbp), %rax");
        } else if (e instanceof BinOpNode) {
            BinOpNode b = (BinOpNode) e;

            generateExpr(b.left);
            emit("pushq   %rax");

            generateExpr(b.right);
            emit("popq    %rcx");

            switch (b.op) {
                case "+": emit("addq    %rcx, %rax"); break;
                case "-":
                    emit("movq    %rcx, %rbx");
                    emit("subq    %rax, %rbx");
                    emit("movq    %rbx, %rax");
                    break;
                case "*": emit("imulq   %rcx, %rax"); break;
                case "/":
                    emit("movq    %rax, %rbx");
                    emit("movq    %rcx, %rax");
                    emit("cqto");
                    emit("idivq   %rbx");
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