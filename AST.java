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
    DeclNode(String name, String type) { this.name = name; this.type = type; }
    @Override
    public void accept(ASTVisitor v) { v.visit(this); }
}

class StmtListNode implements ASTNode {
    List<StmtNode> stmts = new ArrayList<>();
    void add(StmtNode s) { stmts.add(s); }
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
    void visit(DeclListNode node);
    void visit(DeclNode node);
    void visit(StmtListNode node);
    void visit(AssignNode node);
    void visit(BinOpNode node);
    void visit(NumNode node);
    void visit(IdNode node);
}

class Interpreter implements ASTVisitor {
    private SymbolTable symtab = new SymbolTable();

    public SymbolTable getTable() { return symtab; }

    @Override
    public void visit(ProgramNode node) {
        if (node.decls != null) node.decls.accept(this);
        if (node.stmts != null) node.stmts.accept(this);
        symtab.printTable(); // al final muestro resultados
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
        int value = eval(node.expr);
        symtab.assign(node.name, value);
    }

    @Override
    public void visit(BinOpNode node) {
        // nada acá, se evalúa con eval()
    }

    @Override
    public void visit(NumNode node) {
        // nada acá, se evalúa con eval()
    }

    @Override
    public void visit(IdNode node) {
        // nada acá, se evalúa con eval()
    }

    /* ---- Evaluador de expresiones ---- */
    private int eval(ExprNode e) {
        if (e instanceof NumNode) {
            return ((NumNode) e).value;
        }
        if (e instanceof IdNode) {
            SymbolInfo s = symtab.lookup(((IdNode) e).name);
            if (s == null || s.getValue() == null) {
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
            }
        }
        throw new RuntimeException("Expresión no soportada: " + e);
    }
}

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
        // Generar código para la expresión
        generateExpr(node.expr);
        emit("STORE " + node.name);
    }

    @Override
    public void visit(BinOpNode node) {
        // handled in generateExpr
    }

    @Override
    public void visit(NumNode node) {
        // handled in generateExpr
    }

    @Override
    public void visit(IdNode node) {
        // handled in generateExpr
    }

    /* ---- Generador de expresiones ---- */
    private void generateExpr(ExprNode e) {
        if (e instanceof NumNode) {
            emit("LOADI " + ((NumNode) e).value);
        } else if (e instanceof IdNode) {
            emit("LOAD " + ((IdNode) e).name);
        } else if (e instanceof BinOpNode) {
            BinOpNode b = (BinOpNode) e;
            generateExpr(b.left);
            emit("PUSH");             // guardar resultado parcial
            generateExpr(b.right);
            switch (b.op) {
                case "+": emit("ADD"); break;
                case "-": emit("SUB"); break;
                case "*": emit("MUL"); break;
                case "/": emit("DIV"); break;
            }
        }
    }
}
