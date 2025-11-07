# 🔄 Guía de Git para el Proyecto

## Estado Actual del Repositorio

```bash
git status
```

Deberías ver archivos modificados y nuevos archivos sin trackear.

---

## 📝 Paso 1: Revisar Cambios

### Ver archivos modificados:
```bash
git diff AST.java
git diff ycalc.cup
git diff Main.java
git diff SymbolTable.java
```

### Ver nuevos archivos:
```bash
git status --untracked-files=all
```

---

## ➕ Paso 2: Agregar Archivos

### Opción A: Agregar todo (Recomendado)
```bash
# Agregar archivos modificados y nuevos
git add -A

# O específicamente:
git add AST.java
git add ycalc.cup
git add SymbolTable.java
git add Main.java
git add test*.txt
git add *.md
git add build.bat
git add .gitignore
```

### Opción B: Agregar selectivamente
```bash
# Solo archivos fuente
git add AST.java ycalc.cup SymbolTable.java Main.java

# Solo documentación
git add *.md

# Solo tests
git add test*.txt

# Scripts y configuración
git add build.bat .gitignore
```

---

## 💾 Paso 3: Hacer Commit

```bash
git commit -m "feat: Agregar soporte completo para funciones

- Implementar FunctionNode, ParamNode, CallNode en AST
- Extender gramática en ycalc.cup para funciones y llamadas
- Agregar SymbolTableBuilder con validación semántica
- Actualizar X86_64Generator para múltiples funciones con ABI
- Reescribir Interpreter para soporte de llamadas
- Agregar verificación de tipos de retorno y parámetros
- Implementar convención de llamada System V AMD64
- Crear 5 archivos de test (factorial, operaciones, void, etc.)
- Agregar documentación completa (README, TESTS, CAMBIOS)
- Incluir script build.bat para Windows

Breaking Changes:
- ProgramNode ahora requiere FunctionListNode en lugar de BlockNode
- Todos los programas deben definir función main()

Closes #XX"
```

---

## 📤 Paso 4: Push a GitHub

```bash
# Ver el estado
git status

# Push a la rama actual
git push origin main

# O si estás en otra rama
git push origin nombre-de-tu-rama
```

---

## 🌿 Alternativa: Crear una Rama Nueva

Si prefieres crear una rama separada para estas funciones:

```bash
# Crear y cambiar a nueva rama
git checkout -b feature/funciones

# Agregar cambios
git add -A

# Commit
git commit -m "feat: Agregar soporte completo para funciones"

# Push de la nueva rama
git push origin feature/funciones

# Luego puedes crear un Pull Request en GitHub
```

---

## 🏷️ Paso 5: Crear Tag de Versión (Opcional)

```bash
# Crear tag
git tag -a v2.0 -m "Versión 2.0: Soporte completo para funciones"

# Push del tag
git push origin v2.0

# Ver todos los tags
git tag -l
```

---

## 📊 Verificar Historial

```bash
# Ver commits recientes
git log --oneline --graph --decorate -10

# Ver cambios en un archivo específico
git log --follow -p AST.java

# Ver estadísticas de cambios
git diff --stat HEAD~1
```

---

## ⚠️ Archivos que NO se deben subir

El `.gitignore` ya excluye:
- `*.class` - Archivos compilados
- `Lexer.java`, `parser.java`, `sym.java` - Generados
- `program.asm`, `program` - Salida del compilador
- `out/`, `bin/` - Directorios de compilación
- `.idea/`, `.vscode/` - Configuración de IDEs

**Estos archivos se regeneran al compilar, no es necesario versionarlos.**

---

## 🔍 Revisar Antes de Commit

### Checklist:
- [ ] ¿Los archivos `.class` están excluidos?
- [ ] ¿Los archivos generados (Lexer.java, parser.java) están excluidos?
- [ ] ¿El programa compila sin errores?
- [ ] ¿Los tests pasan correctamente?
- [ ] ¿La documentación está actualizada?
- [ ] ¿El mensaje de commit es descriptivo?

```bash
# Verificar que se compila
javac -cp ".;java-cup-11b-runtime.jar" *.java

# Verificar que funciona
java -cp ".;java-cup-11b-runtime.jar" Main
```

---

## 📝 Formato de Mensajes de Commit

Seguir el formato Conventional Commits:

```
<tipo>: <descripción breve>

<descripción detallada>

<footer>
```

**Tipos:**
- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `chore:` Cambios menores (build, etc.)

**Ejemplo:**
```bash
git commit -m "docs: Actualizar README con instrucciones de funciones"
```

---

## 🔙 Deshacer Cambios (Si es necesario)

### Antes de commit:
```bash
# Descartar cambios en un archivo
git checkout -- AST.java

# Sacar archivo del staging
git reset HEAD AST.java

# Descartar todos los cambios (CUIDADO!)
git reset --hard HEAD
```

### Después de commit (local):
```bash
# Deshacer último commit (mantener cambios)
git reset --soft HEAD~1

# Deshacer último commit (descartar cambios)
git reset --hard HEAD~1
```

---

## 🎯 Comandos Útiles

```bash
# Ver diferencias detalladas
git diff --stat
git diff --name-only

# Ver qué se va a commitear
git diff --cached

# Agregar interactivamente
git add -p

# Ver log bonito
git log --graph --oneline --all --decorate

# Buscar en commits
git log --grep="funciones"

# Ver quién modificó qué línea
git blame AST.java
```

---

## 🌐 Después del Push

### En GitHub:

1. **Crear Release:**
   - Ir a "Releases" → "Create new release"
   - Tag: `v2.0`
   - Título: `v2.0 - Soporte para Funciones`
   - Descripción: Copiar de `CAMBIOS.md`

2. **Actualizar README:**
   - Verificar que se vea bien en GitHub
   - Agregar badges si es necesario

3. **Crear Wiki (Opcional):**
   - Documentación adicional
   - Tutoriales
   - FAQ

---

## 📧 Compartir Cambios

```bash
# Generar archivo de patch
git format-patch -1 HEAD

# Crear archivo ZIP del repositorio
git archive --format=zip --output=proyecto.zip HEAD

# Crear diff de todos los cambios
git diff HEAD~1 > cambios.diff
```

---

## ✅ Resumen Rápido

```bash
# 1. Ver estado
git status

# 2. Agregar todo
git add -A

# 3. Commit
git commit -m "feat: Agregar soporte completo para funciones"

# 4. Push
git push origin main

# 5. Tag (opcional)
git tag -a v2.0 -m "Versión 2.0"
git push origin v2.0
```

---

**🎉 ¡Listo para subir a GitHub! 🎉**

Para más información: https://git-scm.com/docs
