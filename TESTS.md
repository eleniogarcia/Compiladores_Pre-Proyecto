# 🧪 Guía de Tests - Compilador Mini

## Archivos de Prueba Disponibles

### 1. `test.txt` - Test Básico con Funciones
**Descripción:** Ejemplo básico del README con función suma  
**Características:**
- Función `suma(int, int)` que retorna la suma
- Función `main()` con if-then-else
- Llamada a función dentro de condicional

**Código de retorno esperado:** 0

```bash
# Ejecutar
cp test.txt test_backup.txt
java -cp ".;java-cup-11b-runtime.jar" Main
```

---

### 2. `test_factorial.txt` - Factorial Recursivo
**Descripción:** Cálculo del factorial de 5 usando recursión  
**Características:**
- Recursión
- Condicional base (n == 0)
- Caso recursivo con multiplicación

**Código de retorno esperado:** 120 (5! = 120)

```bash
# Ejecutar
cp test_factorial.txt test.txt
java -cp ".;java-cup-11b-runtime.jar" Main
gcc -o program program.asm
./program
echo $?  # Debería mostrar 120 (en sistemas que soporten códigos > 255, sino 120 % 256)
```

---

### 3. `test_operaciones.txt` - Múltiples Funciones Aritméticas
**Descripción:** Test con 4 funciones de operaciones básicas  
**Características:**
- Función `suma(int, int)`
- Función `resta(int, int)`
- Función `multiplica(int, int)`
- Función `divide(int, int)` con verificación de división por 0
- Múltiples variables locales
- Múltiples llamadas a funciones

**Código de retorno esperado:** 15 (10 + 5)

```bash
# Ejecutar
cp test_operaciones.txt test.txt
java -cp ".;java-cup-11b-runtime.jar" Main
```

---

### 4. `test_void.txt` - Funciones Void
**Descripción:** Test de funciones sin valor de retorno  
**Características:**
- Función `void` sin parámetros: `inicializar()`
- Función `void` con parámetro: `procesar(int)`
- Variables locales en función void
- Llamadas a funciones void desde main

**Código de retorno esperado:** 0

```bash
# Ejecutar
cp test_void.txt test.txt
java -cp ".;java-cup-11b-runtime.jar" Main
```

---

### 5. `test_complejo.txt` - Test Completo
**Descripción:** Test más elaborado con composición de funciones  
**Características:**
- Función `cuadrado(int)` 
- Función `sumaCuadrados(int, int)` que llama a otra función
- Función `potencia(int, int)` recursiva
- Composición de funciones (llamadas anidadas)
- Condicional que compara resultados de funciones

**Código de retorno esperado:** 32 (2^5 = 32, que es mayor que 3² + 4² = 25)

```bash
# Ejecutar
cp test_complejo.txt test.txt
java -cp ".;java-cup-11b-runtime.jar" Main
```

---

## 🚀 Cómo Ejecutar los Tests

### Paso 1: Preparar el Test
```bash
# Elegir uno de los archivos de test
cp test_factorial.txt test.txt
```

### Paso 2: Compilar el Compilador (si no está compilado)
```bash
# Generar lexer y parser
java -jar jflex-full-1.9.1.jar lcalc.flex
java -jar java-cup-11b.jar -parser parser ycalc.cup

# Compilar Java
javac -cp ".;java-cup-11b-runtime.jar" *.java
```

### Paso 3: Ejecutar el Compilador
```bash
java -cp ".;java-cup-11b-runtime.jar" Main
```

### Paso 4: Ver el Código Generado
```bash
# Ver el código ensamblador generado
cat program.asm
```

### Paso 5: Ensamblar y Ejecutar (Opcional)
```bash
# En Linux/Mac
gcc -o program program.asm
./program
echo $?

# En Windows con MinGW
gcc -o program.exe program.asm
program.exe
echo %ERRORLEVEL%
```

---

## 📊 Salida Esperada del Compilador

Para cualquier test válido, deberías ver:

```
=== Etapa 1: Parsing ===
Programa Mini con funciones válido ✅

=== Etapa 2: Análisis Semántico ===
=== Fase 1: Registro de funciones ===
Registrada función: <tipo> <nombre>(N parámetros)
...

=== Fase 2: Validación semántica de funciones ===
Validando función: <tipo> <nombre>()
...
✅ Análisis semántico completado sin errores

=== Etapa 3: Ejecución simbólica ===
Registrada función: <tipo> <nombre>
...

=== Ejecutando main() ===
[Traza de ejecución]
...
Return: <valor>

=== Etapa 4: Generación de código (x86-64 Windows) ===
[Código generado en program.asm]
```

---

## ❌ Tests de Errores

### Test de Error 1: Falta main
```c
int suma(int a, int b) {
    return a + b;
}
```
**Error esperado:** `Error: No se encontró la función 'main'`

### Test de Error 2: Función int sin return
```c
int suma(int a, int b) {
    int resultado = a + b;
}

int main() {
    return 0;
}
```
**Error esperado:** `Error: función 'suma' de tipo 'int' debe tener al menos un return con valor`

### Test de Error 3: Función void con return de valor
```c
void imprime(int x) {
    return x;
}

int main() {
    return 0;
}
```
**Error esperado:** `Error: función 'void' no debe retornar un valor`

### Test de Error 4: Número incorrecto de argumentos
```c
int suma(int a, int b) {
    return a + b;
}

int main() {
    int x = suma(5);
    return 0;
}
```
**Error esperado:** `Error: función 'suma' espera 2 argumentos, se pasaron 1`

### Test de Error 5: Función no declarada
```c
int main() {
    int x = noExiste(5);
    return 0;
}
```
**Error esperado:** `Error: función 'noExiste' no declarada`

---

## 🎯 Verificar Resultados

### Método 1: Código de Retorno
```bash
gcc -o program program.asm
./program
echo $?  # Muestra el valor retornado por main()
```

### Método 2: Interpretación Simbólica
El compilador ya ejecuta el código simbólicamente y muestra el resultado:
```
Return: <valor>
```

### Método 3: Inspeccionar program.asm
Buscar el valor en `%rax` antes del último `ret` de `main`:
```asm
movq    $0, %rax    # Este es el valor de retorno
leave
ret
```

---

## 🐛 Debugging

### Ver el AST
Agrega prints en `Main.java` después del parsing:
```java
System.out.println("AST: " + root);
```

### Ver la Tabla de Símbolos
Ya se imprime automáticamente en la fase de análisis semántico.

### Ver la Traza de Ejecución
Ya se imprime automáticamente en la fase de interpretación simbólica.

---

## 📝 Crear Tu Propio Test

1. Crea un archivo `.txt` con tu código Mini
2. Asegúrate de que tenga la función `main()`
3. Copia el archivo a `test.txt`
4. Ejecuta el compilador

**Ejemplo:**
```c
int miTest(int x) {
    return x * 2;
}

int main() {
    int resultado = miTest(21);
    return resultado;  // Debería retornar 42
}
```

---

## 🔍 Análisis de Código Generado

Para entender el código ensamblador generado, busca:

1. **Prólogo de función:**
   ```asm
   nombre_funcion:
           pushq   %rbp
           movq    %rsp, %rbp
           subq    $N, %rsp
   ```

2. **Parámetros guardados:**
   ```asm
           movq    %rdi, -8(%rbp)    # Primer parámetro
           movq    %rsi, -16(%rbp)   # Segundo parámetro
   ```

3. **Variables locales:**
   ```asm
           movq    $valor, %rax
           movq    %rax, -24(%rbp)   # Variable local
   ```

4. **Llamada a función:**
   ```asm
           movq    valor, %rax
           movq    %rax, %rdi        # Primer argumento
           call    funcion
   ```

5. **Return:**
   ```asm
           movq    $valor, %rax      # Valor de retorno
           leave
           ret
   ```

---

## 📚 Referencias

- **Documentación completa:** Ver `DOCUMENTACION_ACTUALIZADA.md`
- **Sintaxis del lenguaje:** Ver `README.md`
- **Repositorio:** https://github.com/eleniogarcia/Compiladores_Pre-Proyecto

---

**✨ ¡Happy Testing! ✨**
