# 🚀 Compilador Mini 

Compilador completo para el lenguaje imperativo **Mini** que traduce código fuente a código x86-64 ejecutable, con soporte completo para funciones.

## ✨ Características Principales

- ✅ **Funciones con parámetros** (int)
- ✅ **Tipos de retorno**: `int` y `void`
- ✅ **Recursión** soportada
- ✅ **Análisis semántico completo** con validación de tipos
- ✅ **Generación de código x86-64** optimizada
- ✅ **Interpretación simbólica** para validación

## 📝 Ejemplo de Código Mini

```c
int suma(int a, int b) {
    return a + b;
}

int main() {
    int x = 4;
    int y = 2;
    
    if (x > 4) then {
        x = 5;
    } else {
        x = suma(x, y);
    }
    
    return 0;
}
```

## 🔧 Compilación y Ejecución

### Prerequisitos

- Java JDK 8 o superior
- JFlex (`jflex-full-1.9.1.jar`)
- CUP (`java-cup-11b.jar` y `java-cup-11b-runtime.jar`)
- GCC (para ensamblar el código generado)

### Pasos

```bash
# 1. Generar el analizador léxico
java -jar jflex-full-1.9.1.jar lcalc.flex

# 2. Generar el analizador sintáctico
java -jar java-cup-11b.jar -parser parser ycalc.cup

# 3. Compilar el proyecto Java
javac -cp ".;java-cup-11b-runtime.jar" *.java

# 4. Ejecutar el compilador (lee test.txt y genera program.asm)
java -cp ".;java-cup-11b-runtime.jar" Main

# 5. Ensamblador generado en 
program.asm
```

### En Windows PowerShell

```powershell
# Compilar
javac -cp ".;java-cup-11b-runtime.jar" *.java

# Ejecutar
java -cp ".;java-cup-11b-runtime.jar" Main
```

## 📊 Salida del Compilador

El compilador ejecuta 4 fases y muestra información de cada una:

```
=== Etapa 1: Parsing ===
Programa Mini con funciones válido ✅

=== Etapa 2: Análisis Semántico ===
=== Fase 1: Registro de funciones ===
Registrada función: int suma(2 parámetros)
Registrada función: int main(0 parámetros)

=== Fase 2: Validación semántica de funciones ===
Validando función: int suma()
Validando función: int main()
✅ Análisis semántico completado sin errores

=== Etapa 3: Ejecución simbólica ===
Registrada función: int suma
Registrada función: int main

=== Ejecutando main() ===
...
Return: 0

=== Etapa 4: Generación de código (x86-64 Windows) ===
[Genera program.asm]
```

## 🏗️ Arquitectura del Compilador

```
┌─────────────┐
│  test.txt   │  Código fuente Mini
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Lexer     │  JFlex → Tokens
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Parser    │  CUP → AST
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Semántico  │  Validación de tipos y funciones
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Intérprete  │  Ejecución simbólica (opcional)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Generador   │  Código x86-64 Assembly
│   x86-64    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ program.asm │  Código ensamblador
└─────────────┘
```

## 📁 Estructura del Proyecto

```
Compiladores_Pre-Proyecto/
├── Main.java              # Punto de entrada
├── AST.java               # Definición del AST + Visitors
├── SymbolTable.java       # Análisis semántico
├── lcalc.flex             # Especificación del lexer
├── ycalc.cup              # Especificación del parser (con funciones)
├── Lexer.java             # Generado por JFlex
├── parser.java            # Generado por CUP
├── sym.java               # Símbolos generados por CUP
├── test.txt               # Archivo de prueba
├── program.asm            # Código generado
└── *.jar                  # Librerías de JFlex y CUP
```

## 🎯 Características del Lenguaje Mini

### Tipos de Datos
- `int`: Enteros de 32 bits

### Palabras Reservadas
```c
void, int, if, then, else, while, return, main
```

### Operadores
```c
// Aritméticos
+ - * /

// Relacionales
== < >

// Lógicos
&& || !

// Asignación
=
```

### Estructuras de Control
```c
// If-then-else
if (condición) then {
    // código
} else {
    // código
}

// While
while (condición) {
    // código
}

// Return
return expresión;  // para funciones int
return;            // para funciones void
```

### Funciones
```c
// Función con retorno
int nombreFuncion(int param1, int param2) {
    // cuerpo
    return expresión;
}

// Función sin retorno
void nombreFuncion(int param) {
    // cuerpo
    return;  // opcional
}

// Llamada a función
resultado = nombreFuncion(arg1, arg2);
```

## 🧪 Casos de Prueba

### Test 1: Suma Simple
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

### Test 3: Función Void
```c
void imprime(int x) {
    return;
}

int main() {
    int a = 10;
    imprime(a);
    return 0;
}
```

## 🔍 Análisis Semántico

El compilador realiza las siguientes validaciones:

### Validaciones de Funciones
- ✅ Existencia de función `main()`
- ✅ No redeclaración de funciones
- ✅ Funciones `int` deben tener `return` con valor
- ✅ Funciones `void` no deben retornar valor
- ✅ Función llamada debe estar declarada
- ✅ Número correcto de argumentos
- ✅ Tipos de argumentos correctos

### Validaciones de Variables
- ✅ Declaración previa antes de uso
- ✅ No redeclaración en el mismo scope
- ✅ Variables leídas deben estar inicializadas
- ✅ Tipos compatibles en operaciones

## 🖥️ Generación de Código x86-64

### Convención de Llamada (System V AMD64 ABI)

| Argumento | Registro |
|-----------|----------|
| 1º        | %rdi     |
| 2º        | %rsi     |
| 3º        | %rdx     |
| 4º        | %rcx     |
| 5º        | %r8      |
| 6º        | %r9      |
| Retorno   | %rax     |

### Ejemplo de Código Generado

```asm
suma:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $16, %rsp
        movq    %rdi, -8(%rbp)      # Guardar parámetro a
        movq    %rsi, -16(%rbp)     # Guardar parámetro b
        
        movq    -8(%rbp), %rax      # Cargar a
        pushq   %rax
        movq    -16(%rbp), %rax     # Cargar b
        popq    %rcx
        addq    %rcx, %rax          # a + b
        
        leave
        ret

main:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $16, %rsp
        
        movq    $4, %rax
        movq    %rax, -8(%rbp)      # x = 4
        
        movq    $2, %rax
        movq    %rax, -16(%rbp)     # y = 2
        
        # Llamar suma(x, y)
        movq    -8(%rbp), %rax
        movq    %rax, %rdi
        pushq   %rax
        movq    -16(%rbp), %rax
        popq    %rsi
        andq    $-16, %rsp
        call    suma
        
        movq    %rax, -8(%rbp)      # x = resultado
        
        movq    $0, %rax
        leave
        ret
```

## ⚠️ Limitaciones Conocidas

- ❌ Solo tipo de datos `int` (no bool, float, string, char)
- ❌ No soporta arrays
- ❌ No soporta punteros
- ❌ No soporta structs o clases
- ❌ Máximo 6 parámetros por función (limitación de registros)
- ❌ No variables globales explícitas
- ❌ Sin optimizaciones de código

## 👥 Alumnos

- **Elenio Garcia Bustamante**
- **Germán Adrián Muñoz**

## 🎓 Universidad Nacional de Villa Mercedes

**Materia:** Compiladores  
**Profesor:** Francisco Bavera  
**Carrera:** Ingeniería en Sistemas de Información




