# Compilador del Lenguaje Mini - VERSIÓN CON FUNCIONES
**Universidad Nacional de Villa Mercedes**  
**Carrera:** Ingeniería en Sistemas de Información  
**Materia:** Compiladores  
**Profesor:** Francisco Bavera  
**Alumno:** Elenio Garcia Bustamante, Germán Adrián Muñoz  
**Fecha de actualización:** 05/11/2025

---

## 📋 CAMBIOS PRINCIPALES - VERSIÓN CON FUNCIONES

### ✨ Nuevas Características

1. **Soporte para Definición de Funciones**
   - Funciones con tipo de retorno `int` o `void`
   - Parámetros de tipo `int`
   - Llamadas a funciones con argumentos

2. **Validación Semántica de Funciones**
   - Verificación de que `main()` existe
   - Verificación de tipos de retorno
   - Validación de número y tipo de argumentos
   - Funciones `int` deben tener `return` con valor
   - Funciones `void` no deben retornar valor

3. **Generación de Código x86-64 Mejorada**
   - Convención de llamada System V AMD64 ABI
   - Paso de argumentos por registros (%rdi, %rsi, %rdx, %rcx, %r8, %r9)
   - Alineación de stack a 16 bytes
   - Gestión de stack frames para cada función

---

## 1. Resumen del Proyecto

### 1.1 Objetivo
Implementamos un compilador completo para el lenguaje imperativo Mini **con soporte para funciones** que traduce código fuente a código ensamblador x86-64 ejecutable.

### 1.2 Lenguaje Mini - Características ACTUALIZADAS

**Tipos:**
- `int` (enteros 32 bits)
- `void` (para funciones sin retorno)

**Operadores:**
- Aritméticos: `+`, `-`, `*`, `/`
- Relacionales: `==`, `<`, `>`
- Lógicos: `&&`, `||`, `!`
- Unarios: `-` (negación), `!` (NOT)

**Estructuras de Control:**
- `if (condición) then { } else { }`
- `while (condición) { }`
- `return expresión;` o `return;`

**NUEVO - Funciones:**
```c
int nombreFuncion(int param1, int param2) {
    // cuerpo de la función
    return expresión;
}

void otraFuncion() {
    // cuerpo sin retorno
    return;  // opcional
}
```

### 1.3 Pipeline del Compilador

```
Código Fuente (test.txt)
    ↓
Análisis Léxico (JFlex) → Tokens
    ↓
Análisis Sintáctico (CUP) → AST con Funciones
    ↓
Análisis Semántico → Tabla de Símbolos + Validación de Funciones
    ↓
Interpretación Simbólica → Ejecución para Validación (con llamadas)
    ↓
Generación x86-64 → program.asm (múltiples funciones)
    ↓
Ensamblador (gcc/as) → Ejecutable
```

---

## 2. Estructura y Ejecución

### 2.1 Estructura de Archivos ACTUALIZADA

```
Compiladores_Pre-Proyecto/
├── Main.java                # Punto de entrada
├── AST.java                 # AST + Visitors + Generación de código
├── SymbolTable.java         # Análisis semántico con funciones
├── lcalc.flex               # Especificación lexer (sin cambios)
├── ycalc.cup                # Especificación parser (CON FUNCIONES)
├── Lexer.java               # Lexer generado
├── parser.java, sym.java    # Parser generado
├── test.txt                 # Programa de prueba con funciones
└── program.asm              # Código generado (múltiples funciones)
```

### 2.2 Comandos de Ejecución (sin cambios)

```bash
# 1. Generar lexer
java -jar jflex-full-1.9.1.jar lcalc.flex

# 2. Generar parser
java -jar java-cup-11b.jar -parser parser ycalc.cup

# 3. Compilar proyecto
javac -cp ".;java-cup-11b-runtime.jar" *.java

# 4. Ejecutar compilador
java -cp ".;java-cup-11b-runtime.jar" Main

# 5. Ensamblar y ejecutar (Linux/Mac)
gcc -o program program.asm
./program
echo $?  # Ver código de retorno
```

---

## 3. Nuevos Nodos del AST

### 3.1 Estructura del AST Extendida

```
ASTNode (interface)
├── ProgramNode (functions: FunctionListNode)  [MODIFICADO]
├── FunctionListNode (functions: List<FunctionNode>)  [NUEVO]
├── FunctionNode (returnType, name, params, body)  [NUEVO]
├── ParamListNode (params: List<ParamNode>)  [NUEVO]
├── ParamNode (type, name)  [NUEVO]
├── DeclListNode, DeclNode (sin cambios)
├── StmtListNode, StmtNode (sin cambios)
│   ├── AssignNode
│   ├── IfNode
│   ├── WhileNode
│   └── ReturnNode (expr puede ser null para void)  [MODIFICADO]
└── ExprNode
    ├── BinOpNode, UnaryOpNode, NumNode, IdNode
    └── CallNode (functionName, args: List<ExprNode>)  [NUEVO]
```

### 3.2 Ejemplo Visual de AST con Funciones

**Código Mini:**
```c
# Archivos compilados de Java
*.class

# Archivos generados por JFlex y CUP
Lexer.java
parser.java
sym.java

# Código ensamblador generado
program.asm
program
program.exe

# Directorios de salida
out/
bin/
target/

# Archivos de IDE
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# Archivos de sistema
.DS_Store
Thumbs.db
*~

# Archivos de respaldo
*.bak
*_backup.txt
```

**AST Resultante:**
```
ProgramNode
└── FunctionListNode
    ├── FunctionNode("int", "suma")
    │   ├── ParamListNode
    │   │   ├── ParamNode("int", "a")
    │   │   └── ParamNode("int", "b")
    │   └── BlockNode
    │       ├── DeclListNode (vacía)
    │       └── StmtListNode
    │           └── ReturnNode
    │               └── BinOpNode("+")
    │                   ├── IdNode("a")
    │                   └── IdNode("b")
    └── FunctionNode("int", "main")
        ├── ParamListNode (vacía)
        └── BlockNode
            ├── DeclListNode
            │   ├── DeclNode("x", "int", NumNode(4))
            │   └── DeclNode("y", "int", NumNode(2))
            └── StmtListNode
                ├── AssignNode("x")
                │   └── CallNode("suma")
                │       ├── IdNode("x")
                │       └── IdNode("y")
                └── ReturnNode(NumNode(0))
```

---

## 4. Análisis Sintáctico - Gramática ACTUALIZADA

### 4.1 Nuevas Reglas de Producción

```yacc
/* Programa = lista de funciones */
program ::= functions

/* Funciones */
functions ::= function
            | functions function

function ::= type ID LPAREN params RPAREN block

type ::= INT | VOID

/* Parámetros */
params ::= /* vacío */
         | nonempty_params

nonempty_params ::= param
                  | nonempty_params COMMA param

param ::= INT ID

/* Return puede no tener expresión (para void) */
stmt ::= RETURN expr SEMI
       | RETURN SEMI

/* Llamadas a función */
expr ::= ID LPAREN args RPAREN

args ::= /* vacío */
       | nonempty_args

nonempty_args ::= expr
                | nonempty_args COMMA expr
```

---

## 5. Análisis Semántico EXTENDIDO

### 5.1 Verificaciones para Funciones

| Verificación | Descripción | Ejemplo Error |
|-------------|-------------|---------------|
| **Función main existe** | Todo programa debe tener `int main()` | No hay función main |
| **Unicidad de funciones** | No redeclarar funciones | `int f() {}` dos veces |
| **Tipo de retorno** | Funciones `int` deben retornar valor | `int f() { }` sin return |
| **Funciones void** | No deben retornar valor | `void f() { return 5; }` |
| **Función declarada** | Al llamar, la función debe existir | `f()` sin definir f |
| **Número de argumentos** | Debe coincidir con parámetros | `suma(x)` cuando suma espera 2 |
| **Tipos de argumentos** | Deben coincidir con parámetros | `suma("hola", 5)` |
| **Scopes de funciones** | Variables locales a cada función | No mezclar variables entre funciones |

### 5.2 Tabla de Símbolos por Función

El análisis semántico ahora mantiene:
- **Tabla de funciones global:** Todas las funciones del programa
- **Tabla de símbolos por función:** Variables locales y parámetros

```java
class SymbolTableBuilder {
    private HashMap<String, FunctionInfo> functions;  // Funciones globales
    private SymbolTable currentScope;                 // Scope actual
    private String currentFunctionReturnType;         // Tipo de retorno esperado
    private boolean hasReturn;                        // Si ya hubo return
}
```

### 5.3 Ejemplo de Validación

**Código:**
```c
int suma(int a, int b) {
    return a + b;  // ✅ OK: return con int
}

void imprime(int x) {
    return x;  // ❌ ERROR: void no debe retornar valor
}

int main() {
    int x = suma(1, 2);        // ✅ OK
    int y = suma(1);           // ❌ ERROR: falta argumento
    int z = suma(1, "hola");   // ❌ ERROR: tipo incorrecto
    imprime(x);                // ✅ OK
    noExiste();                // ❌ ERROR: función no declarada
    return 0;                  // ✅ OK
}
```

---

## 6. Interpretación Simbólica CON FUNCIONES

### 6.1 Ejecución de Funciones

El intérprete ahora:
1. **Registra todas las funciones** al inicio
2. **Crea un stack de contextos** para cada llamada
3. **Maneja return values** correctamente
4. **Ejecuta desde main()**

```java
class Interpreter {
    private Map<String, FunctionNode> functions;     // Funciones disponibles
    private SymbolTable globalSymtab;                // Variables globales (ninguna en este caso)
    private Stack<SymbolTable> callStack;            // Stack de llamadas
    private SymbolTable currentSymtab;               // Contexto actual
    private Integer returnValue;                     // Valor de retorno
}
```

### 6.2 Ejemplo de Ejecución

**Código:**
```c
int suma(int a, int b) {
    return a + b;
}

int main() {
    int x = 4;
    int y = 2;
    x = suma(x, y);
    return 0;
}
```

**Traza de Ejecución:**
```
=== Registrando funciones ===
Registrada función: int suma
Registrada función: int main

=== Ejecutando main() ===
Tabla de símbolos de main:
  x: int = 4
  y: int = 2

Llamada a suma(4, 2)
  Tabla de símbolos de suma:
    a: int = 4
    b: int = 2
  Evaluando: a + b = 6
  Return: 6

Asignación: x = 6
Tabla de símbolos de main (actualizada):
  x: int = 6
  y: int = 2

Return: 0
```

---

## 7. Generación de Código x86-64 MEJORADA

### 7.1 Convención de Llamada System V AMD64 ABI

**Paso de Argumentos (primeros 6):**
| Orden | Registro | Propósito |
|-------|----------|-----------|
| 1º | %rdi | Primer argumento |
| 2º | %rsi | Segundo argumento |
| 3º | %rdx | Tercer argumento |
| 4º | %rcx | Cuarto argumento |
| 5º | %r8 | Quinto argumento |
| 6º | %r9 | Sexto argumento |

**Valor de Retorno:** `%rax`

**Alineación:** El stack debe estar alineado a 16 bytes antes de `call`

### 7.2 Estructura de una Función

```asm
nombre_funcion:
    # Prólogo
    pushq   %rbp              # Guardar frame pointer anterior
    movq    %rsp, %rbp        # Establecer nuevo frame pointer
    subq    $N, %rsp          # Reservar espacio para variables (alineado a 16)
    
    # Guardar parámetros desde registros a la pila
    movq    %rdi, -8(%rbp)    # Primer parámetro
    movq    %rsi, -16(%rbp)   # Segundo parámetro
    # ...
    
    # Cuerpo de la función
    # ...
    
    # Epílogo
    movq    $valor, %rax      # Valor de retorno (si es int)
    leave                     # Equivale a: movq %rbp, %rsp; popq %rbp
    ret                       # Retornar al caller
```

### 7.3 Llamada a Función

```asm
    # Preparar argumentos
    movq    valor1, %rax
    movq    %rax, %rdi        # Primer argumento
    movq    valor2, %rax
    movq    %rax, %rsi        # Segundo argumento
    
    # Alinear stack a 16 bytes
    andq    $-16, %rsp
    
    # Llamar función
    call    nombre_funcion
    
    # Resultado en %rax
```

### 7.4 Ejemplo Completo

**Código Mini:**
```c
int suma(int a, int b) {
    return a + b;
}

int main() {
    int x = 4;
    int y = 2;
    x = suma(x, y);
    return 0;
}
```

**Código Ensamblador Generado:**
```asm
.text
.globl main

suma:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $16, %rsp          # 2 parámetros × 8 bytes, alineado
        movq    %rdi, -8(%rbp)     # Guardar parámetro 'a'
        movq    %rsi, -16(%rbp)    # Guardar parámetro 'b'
        
        # return a + b;
        movq    -8(%rbp), %rax     # Cargar 'a'
        pushq   %rax
        movq    -16(%rbp), %rax    # Cargar 'b'
        popq    %rcx
        addq    %rcx, %rax         # rax = a + b
        
        leave
        ret

main:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $16, %rsp          # 2 variables × 8 bytes, alineado
        
        # int x = 4;
        movq    $4, %rax
        movq    %rax, -8(%rbp)
        
        # int y = 2;
        movq    $2, %rax
        movq    %rax, -16(%rbp)
        
        # x = suma(x, y);
        # Preparando llamada a suma
        movq    -8(%rbp), %rax     # Cargar x
        movq    %rax, %rdi         # Primer argumento
        pushq   %rax
        movq    -16(%rbp), %rax    # Cargar y
        popq    %rsi               # Segundo argumento
        andq    $-16, %rsp         # Alinear stack
        call    suma
        movq    %rax, -8(%rbp)     # x = resultado
        
        # return 0;
        movq    $0, %rax
        leave
        ret
```

### 7.5 Prueba de Compilación

**Compilar y ejecutar:**
```bash
# Compilar con GCC (Linux/Mac)
gcc -o program program.asm

# Ejecutar
./program

# Ver código de retorno
echo $?
# Output: 0
```

**Verificar ejecución:**
```bash
# Si agregamos: return x; en main, deberíamos ver 6
gcc -o program program.asm
./program
echo $?
# Output: 6
```

---

## 8. Características del Compilador Actualizado

### 8.1 Soporte Completo

| Característica | Estado | Descripción |
|----------------|--------|-------------|
| **Funciones int** | ✅ IMPLEMENTADO | Funciones que retornan enteros |
| **Funciones void** | ✅ IMPLEMENTADO | Funciones sin valor de retorno |
| **Parámetros** | ✅ IMPLEMENTADO | Hasta 6 parámetros int por función |
| **Llamadas recursivas** | ✅ IMPLEMENTADO | Soporte para recursión |
| **Scopes locales** | ✅ IMPLEMENTADO | Variables locales por función |
| **Validación semántica** | ✅ IMPLEMENTADO | Verificación completa de tipos |

### 8.2 Limitaciones Restantes

| Limitación | Impacto |
|------------|---------|
| **Tipos de datos** | Solo `int` (no bool, float, string, char) |
| **Arrays** | No soportados |
| **Punteros** | No soportados |
| **Structs/Clases** | No soportados |
| **Más de 6 parámetros** | Requiere paso por stack (no implementado) |
| **Variables globales** | No soportadas explícitamente |
| **Optimizaciones** | Código sin optimizar |

---

## 9. Casos de Prueba

### 9.1 Ejemplo 1: Factorial Recursivo

```c
int factorial(int n) {
    if (n == 0) then {
        return 1;
    } else {
        return n * factorial(n - 1);
    }
}

int main() {
    int resultado = factorial(5);
    return resultado;  // Debería retornar 120
}
```

### 9.2 Ejemplo 2: Funciones Void

```c
void imprime(int x) {
    return;  // void no retorna valor
}

int main() {
    int a = 10;
    imprime(a);
    return 0;
}
```

### 9.3 Ejemplo 3: Múltiples Funciones

```c
int suma(int a, int b) {
    return a + b;
}

int resta(int a, int b) {
    return a - b;
}

int multiplica(int a, int b) {
    return a * b;
}

int main() {
    int x = suma(5, 3);
    int y = resta(x, 2);
    int z = multiplica(y, 4);
    return z;  // Debería retornar 24
}
```

---

## 10. Conclusión

### 10.1 Logros Principales

✅ **Análisis Léxico** con JFlex - Tokenización robusta (sin cambios)  
✅ **Análisis Sintáctico LALR(1)** con CUP - Parsing con funciones  
✅ **Análisis Semántico** - Validación completa de funciones  
✅ **Interpretación Simbólica** - Ejecución con llamadas de función  
✅ **Generación x86-64** - Código ejecutable con múltiples funciones  

### 10.2 Relación con la Teoría

Este proyecto aplica conceptos avanzados de compiladores:
- **Scopes y tablas de símbolos jerárquicas**
- **Convención de llamada ABI**
- **Stack frames y gestión de memoria**
- **Análisis de flujo de control con return**
- **Recursión y contextos de ejecución**

### 10.3 Repositorio

**GitHub:** https://github.com/eleniogarcia/Compiladores_Pre-Proyecto

---

## Apéndice A: Referencia Rápida de Sintaxis Mini

```c
// Declaración de funciones
int nombreFuncion(int param1, int param2) {
    // Variables locales
    int x = 5;
    int y;
    
    // Asignaciones
    y = x + param1;
    
    // Condicionales
    if (y > 10) then {
        return y;
    } else {
        return param2;
    }
}

// Función void
void otraFuncion() {
    int temp = 0;
    return;  // Opcional
}

// Función main (obligatoria)
int main() {
    int resultado = nombreFuncion(1, 2);
    otraFuncion();
    return 0;
}
```

---

**"La compilación con funciones es el puente entre el pensamiento modular y la ejecución eficiente en hardware."**
