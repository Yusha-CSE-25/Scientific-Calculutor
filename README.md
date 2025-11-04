# 🧮 Scientific Calculator (Java Console)

A simple **Scientific Calculator** built in Java that supports:

* Basic arithmetic: `+ - * / ^`
* Trigonometric functions: `sin, cos, tan, asin, acos, atan`
* Logarithmic & exponential: `log, ln, exp`
* Square root, absolute, factorial
* Full parentheses support
* Custom expression parsing using the **Shunting Yard algorithm**

---

## ⚙️ Requirements

* **Java 17 or later** (JDK)
* Works in **any IDE** (NetBeans, IntelliJ, VS Code) or command line.

---

## 🚀 How to Compile & Run

### 🖥️ Option 1 — Command Line

```bash
# 1️⃣ Go into the project directory
cd ScientificCalculator/src/com/mycompany/scientificcalculator

# 2️⃣ Compile the Java file
javac ScientificCalculator.java

# 3️⃣ Run it
java com.mycompany.scientificcalculator.ScientificCalculator
```

### 💡 Option 2 — Using an IDE

1. Open the folder in NetBeans, IntelliJ, or VS Code.
2. Run the file `ScientificCalculator.java`.
3. In the console, type expressions like:

   ```
   > 5 + 3 * 2
   11.0

   > sin(90) + log(100)
   3.0
   ```

---

## 🧪 Supported Examples

| Expression  | Output |
| ----------- | ------ |
| `2 + 3 * 4` | `14.0` |
| `sqrt(16)`  | `4.0`  |
| `sin(90)`   | `1.0`  |
| `log(1000)` | `3.0`  |
| `3! + 4^2`  | `25.0` |

---

## 🧰 Folder Structure

```
ScientificCalculator/
└── src/
    └── com/
        └── mycompany/
            └── scientificcalculator/
                └── ScientificCalculator.java
```

---

## 📜 License

This project is licensed under the **MIT License**.
You’re free to use, modify, and distribute it with My credit.

Created By Yusha
