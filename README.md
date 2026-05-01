This repository contains a Java utility class designed to generate different types of unique identifiers. I created this to handle various ID requirements, from simple numeric codes to standard UUIDs used in professional software development.

### What it does
*   **Custom Short IDs:** Generates a random string using uppercase, lowercase, and numbers. You can specify exactly how long you want it to be.
*   **Standard UUIDs:** Uses the Java `UUID` library to create a 128-bit universally unique identifier (great for database primary keys).
*   **Numeric IDs:** Creates a random number based on a specific number of digits.

---

### How to use it

1.  Include the `IdGenerator.java` file in your project package.
2.  Call the static methods directly from the class (no need to create an object!).

#### Examples:
```java
// 1. Generate a 10-character alphanumeric ID
String myId = IdGenerator.generateShortId(10); 

// 2. Get a standard UUID string
String uuid = IdGenerator.generateUUID();

// 3. Get a 6-digit random number
long pin = IdGenerator.generateNumericId(6);
```

---

### Project Structure
*   `IdGenerator.java`: The main class containing all the generation logic.
*   `CHARS`: A constant string holding all possible characters to avoid hardcoding inside the loop.
*   Uses `java.util.Random` for generating the index positions.

---

### Notes for Lab Submission
*   **Dependencies:** Uses standard Java SE libraries (`util` and `lang`), so no external JAR files are needed.
*   **Logic:** The `generateShortId` method uses a `for` loop and `StringBuilder` for better performance when concatenating strings.
*   **IDE:** Developed and tested using Java 11+.

---

### 日本語訳 (Japanese Translation)

## ID生成ユーティリティ・プロジェクト

このレポジトリには、さまざまな種類のユニークな識別子を生成するためのJavaクラスが含まれています。課題やプロジェクトで、短いコードや標準的なUUIDが必要な時に使えるように作成しました。

### 特徴
* **カスタム短縮ID:** 英数字（大文字・小文字・数字）を組み合わせた任意の長さの文字列を生成します。
* **標準UUID:** Javaの `UUID` ライブラリを使用して、128ビットの識別子を作成します。
* **数値ID:** 指定した桁数のランダムな数値を生成します。

### 使い方
`IdGenerator.java` をプロジェクトに追加し、静的メソッドを直接呼び出すだけです。

```java
String myId = IdGenerator.generateShortId(8); // 8文字のIDを生成
```

### 提出時の注意
* `java.util.Random` と `StringBuilder` を使用して効率的に動作するようにしています。
* 外部ライブラリは不要で、標準的なJava環境で実行可能です。  
