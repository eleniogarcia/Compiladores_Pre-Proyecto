# 📝 RESUMEN DE CAMBIOS - Soporte para Funciones

## Fecha: 05/11/2025
## Versión: 2.0 - Con soporte completo para funciones

---

## 🎯 Objetivo Cumplido

✅ Extender el compilador Mini para soportar:
- Funciones con tipo de retorno `int` o `void`
- Parámetros de tipo `int`
- Llamadas a funciones con argumentos
- Recursión

---

## 📦 Archivos Modificados y Creados

### Archivos MODIFICADOS

#### 1. `AST.java` ⭐ **CAMBIOS MAYORES**
**Nuevas clases agregadas:**
- `FunctionListNode` - Lista de funciones
- `FunctionNode` - Definición de función
- `ParamListNode` - Lista de parámetros
- `ParamNode` - Parámetro individual
- `CallNode` - Llamada a función

**Clases modificadas:**
- `ProgramNode` - Ahora contiene `FunctionListNode` en lugar de bloques
- `ReturnNode` - Ahora soporta `return;` sin expresión (para void)
- `Interpreter` - Completamente reescrito para manejar funciones
- `X86_64Generator` - Reescrito para generar múltiples funciones

**Cambios clave en X86_64Generator:**
```java
- Agregado: private boolean hasReturn = false;
- Modificado: visit(ProgramNode) - itera sobre todas las funciones
- Modificado: visit(FunctionNode) - genera código con prólogo/epílogo
- Agregado: countLocalVars() - calcula espacio en pila
- Modificado: generateExpr() - manejo de CallNode con ABI
```

#### 2. `ycalc.cup` ⭐ **CAMBIOS MAYORES**
**Nuevas reglas de gramática:**
```yacc
program ::= functions
functions ::= function | functions function
function ::= type ID LPAREN params RPAREN block
type ::= INT | VOID
params ::= /* vacío */ | nonempty_params
param ::= INT ID
expr ::= ID LPAREN args RPAREN  // Llamada a función
stmt ::= RETURN SEMI  // Return sin valor (void)
```

#### 3. `SymbolTable.java` ⭐ **CAMBIOS MAYORES**
**Nuevas clases:**
- `FunctionInfo` - Información de funciones (nombre, tipo, parámetros)
- `SymbolTableBuilder` - Validador semántico completo

**Funcionalidades agregadas:**
- Registro global de funciones
- Validación de tipos de retorno
- Validación de número y tipo de argumentos
- Verificación de existencia de `main()`
- Scopes por función

#### 4. `Main.java` ⭐ **CAMBIOS MENORES**
**Mejoras:**
- Verificación de errores semánticos antes de generar código
- Manejo de excepciones en interpretación simbólica
- Mensajes informativos mejorados

#### 5. `lcalc.flex` - **SIN CAMBIOS**
El lexer ya tenía todas las palabras reservadas necesarias.

---

### Archivos NUEVOS

#### 1. `DOCUMENTACION_ACTUALIZADA.md` ⭐ **NUEVO**
Documentación completa con:
- Explicación de todas las fases con funciones
- Ejemplos de AST extendido
- Detalles de generación de código x86-64
- Convención de llamada ABI
- Casos de prueba

#### 2. `README.md` ⭐ **NUEVO**
Guía rápida con:
- Características principales
- Instrucciones de compilación
- Arquitectura del compilador
- Ejemplos de código
- Limitaciones

#### 3. `TESTS.md` ⭐ **NUEVO**
Guía completa de pruebas con:
- 5 archivos de test documentados
- Instrucciones de ejecución
- Tests de errores
- Cómo crear nuevos tests

#### 4. `build.bat` ⭐ **NUEVO**
Script de Windows para:
- Generar lexer y parser
- Compilar el proyecto
- Ejecutar el compilador
- Ver código generado
- Limpiar archivos

#### 5. Archivos de Test
- `test.txt` - Test básico (ACTUALIZADO)
- `test_factorial.txt` - Factorial recursivo (NUEVO)
- `test_operaciones.txt` - Múltiples funciones (NUEVO)
- `test_void.txt` - Funciones void (NUEVO)
- `test_complejo.txt` - Test complejo (NUEVO)

---

## 🔧 Cambios Técnicos Detallados

### 1. Análisis Sintáctico
```
ANTES:
program ::= block

DESPUÉS:
program ::= functions
functions ::= function | functions function
function ::= type ID LPAREN params RPAREN block
```

### 2. Análisis Semántico
```java
ANTES:
- Una sola tabla de símbolos global
- Sin validación de funciones

DESPUÉS:
- Tabla de funciones global
- Tabla de símbolos por función (scopes)
- Validación completa de tipos de retorno
- Validación de argumentos en llamadas
```

### 3. Interpretación
```java
ANTES:
- Ejecución directa del bloque principal

DESPUÉS:
- Registro de todas las funciones
- Stack de contextos para llamadas
- Manejo de return values
- Ejecución desde main()
```

### 4. Generación de Código
```asm
ANTES:
- Solo generaba main
- Sin manejo de parámetros

DESPUÉS:
- Genera todas las funciones
- Convención de llamada System V AMD64
- Paso de argumentos por registros
- Alineación de stack a 16 bytes
- Gestión de stack frames
```

---

## 🧪 Validación

### Tests Exitosos
✅ Suma simple con funciones  
✅ Factorial recursivo (5! = 120)  
✅ Múltiples funciones aritméticas  
✅ Funciones void  
✅ Composición de funciones  
✅ Llamadas anidadas  

### Validación Semántica Exitosa
✅ Detección de función main faltante  
✅ Detección de funciones redeclaradas  
✅ Detección de funciones int sin return  
✅ Detección de void con return de valor  
✅ Detección de número incorrecto de argumentos  
✅ Detección de tipos incorrectos de argumentos  
✅ Detección de funciones no declaradas  

---

## 📊 Estadísticas

| Métrica | Valor |
|---------|-------|
| Líneas agregadas en AST.java | ~300 |
| Nuevas clases en AST.java | 5 |
| Líneas agregadas en SymbolTable.java | ~250 |
| Nuevas reglas en ycalc.cup | 10 |
| Archivos de documentación | 4 |
| Archivos de test | 5 |
| Casos de prueba | 8 |

---

## 🎓 Conceptos de Compiladores Aplicados

### Análisis Léxico
- ✅ Sin cambios (ya completo)

### Análisis Sintáctico
- ✅ Gramática extendida con funciones
- ✅ Parsing LALR(1) con CUP
- ✅ Construcción de AST jerárquico

### Análisis Semántico
- ✅ Tablas de símbolos por scope
- ✅ Verificación de tipos
- ✅ Análisis de flujo de control (return)
- ✅ Resolución de nombres (funciones y variables)

### Generación de Código
- ✅ Convención de llamada ABI
- ✅ Gestión de registros
- ✅ Stack frames
- ✅ Prólogo y epílogo de funciones

---

## 🚀 Cómo Usar el Compilador Actualizado

### Opción 1: Usar build.bat (Recomendado para Windows)
```cmd
build.bat
```
Luego seleccionar opción 4 (Todo)

### Opción 2: Manual
```bash
# 1. Generar lexer y parser
java -jar jflex-full-1.9.1.jar lcalc.flex
java -jar java-cup-11b.jar -parser parser ycalc.cup

# 2. Compilar
javac -cp ".;java-cup-11b-runtime.jar" *.java

# 3. Ejecutar (usa test.txt)
java -cp ".;java-cup-11b-runtime.jar" Main

# 4. Ensamblar y ejecutar
gcc -o program program.asm
./program
echo $?
```

### Opción 3: Probar diferentes tests
```bash
# Copiar un test específico
cp test_factorial.txt test.txt

# Compilar
java -cp ".;java-cup-11b-runtime.jar" Main
```

---

## ⚠️ Limitaciones Conocidas

Las siguientes características NO están implementadas:

1. **Más de 6 parámetros por función** - Requerería paso por stack
2. **Tipos de datos** - Solo `int` soportado
3. **Arrays** - No implementados
4. **Punteros** - No implementados
5. **Structs/Clases** - No implementados
6. **Variables globales** - No soportadas explícitamente
7. **Optimizaciones** - Código sin optimizar

---

## 📚 Documentación Adicional

- `README.md` - Guía principal del proyecto
- `DOCUMENTACION_ACTUALIZADA.md` - Documentación técnica completa
- `TESTS.md` - Guía de tests y ejemplos
- Original: `Compiladores proyecto.pdf` - Versión sin funciones

---

## 👥 Créditos

**Implementación del soporte para funciones:**
- Elenio Garcia Bustamante
- Germán Adrián Muñoz

**Universidad Nacional de Villa Mercedes**
Materia: Compiladores
Profesor: Francisco Bavera

---

## 🔗 Recursos

**Repositorio GitHub:**  
https://github.com/eleniogarcia/Compiladores_Pre-Proyecto

**Referencias técnicas:**
- System V AMD64 ABI - Convención de llamada
- JFlex Documentation
- CUP Parser Generator
- x86-64 Instruction Set Reference

---

## ✅ Checklist de Implementación

- [x] Definir nodos de AST para funciones
- [x] Extender gramática en CUP
- [x] Implementar análisis semántico
- [x] Actualizar intérprete simbólico
- [x] Implementar generación de código x86-64
- [x] Crear tests de validación
- [x] Documentar cambios
- [x] Crear guía de usuario
- [x] Verificar todos los casos de prueba

---

**🎉 ¡Compilador Mini v2.0 con Funciones COMPLETADO! 🎉**

*"La compilación con funciones es el puente entre el pensamiento modular y la ejecución eficiente en hardware."*
