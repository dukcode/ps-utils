# 🧩 Java PS Utils

**Java PS Utils** is a lightweight utility library designed to streamline **Competitive Programming (Algorithm)** testing using **JUnit 5**.

It abstracts standard I/O redirection (`System.in`, `System.out`) and provides a simple interface for **Parameterized Tests** and **Timeout Assertions**, making your solution verification process clean and efficient.

## ✨ Features

- 🛡 **Zero Boilerplate**: Use simple `@PSTest`, `@PSTestCase`, and `@PSTestCases` annotations. No inheritance, no setup methods.
- ⏱ **Timeout Assertions**: Easily detect infinite loops or inefficient solutions with strict timeouts.
- 📝 **I/O Redirection**: Automatically handles `System.in` and captures `System.out` for assertion.
- 🧪 **Flexible Test Organization**: Define each test case as a separate method, or group multiple cases in one method with `@PSTestCases`.
- 🎯 **Better IDE Integration**: Each test case appears separately in test runners.
- ☕ **Java 8 Compatible**: Works with Java 8 and above.

## 📦 Installation

This library is distributed via **JitPack**.

### Gradle (Groovy)

**Step 1.** Add the JitPack repository to your root build.gradle file:

```groovy
repositories {
  mavenCentral()
  maven { url '[https://jitpack.io](https://jitpack.io)' }
}
```


**Step 2.** Add the dependency:

```groovy
dependencies {
  implementation 'com.github.dukcode:ps-utils:1.1.0'
}
```

### Gradle (Kotlin DSL)

```kotlin
repositories {
  mavenCentral()
  maven("[https://jitpack.io](https://jitpack.io)")
}

dependencies {
implementation("com.github.dukcode:ps-utils:1.1.0")
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>[https://jitpack.io](https://jitpack.io)</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.dukcode</groupId>
    <artifactId>ps-utils</artifactId>
    <version>1.1.0</version>
</dependency>
```

## 🚀 Usage

There are two ways to use this library: **Annotation-based** (recommended) and **Classic**.

### 🌟 Annotation-based Approach (Recommended)

Use `@PSTest` and `@PSTestCase` annotations for minimal boilerplate.

**1. Annotate Your Test Class**

Use `@PSTest` to specify the solution class and timeout.

**2. Define Test Cases**

Use `@PSTestCase` to define input and expected output for each test case.

#### 📝 Example Code

Here is a full example of testing a solution named `Fence`:

```java
import org.dukcode.psutils.PSTest;
import org.dukcode.psutils.PSTestCase;

@PSTest(timeout = 1.0, solution = Fence.class)
class FenceTest {

  @PSTestCase(
      input = """
          3
          7
          7 1 5 9 6 7 3
          """,
      output = "20"
  )
  void testCase1() {}

  @PSTestCase(
      input = """
          7
          1 4 4 4 4 1 1
          """,
      output = "16"
  )
  void testCase2() {}
}
```

**Benefits:**
- ✨ No inheritance required
- 🎯 Each test case is a separate method with clear naming
- 📊 Better IDE integration and test reporting
- 🔧 Less boilerplate code

#### 🔢 Multiple Test Cases in One Method

You can also use `@PSTestCases` to define multiple test cases in a single method:

```java
import org.dukcode.psutils.PSTest;
import org.dukcode.psutils.PSTestCase;
import org.dukcode.psutils.PSTestCases;

@PSTest(timeout = 1.0, solution = Fence.class)
class FenceTest {

  @PSTestCases({
      @PSTestCase(input = "3\n7\n7 1 5 9 6 7 3", output = "20"),
      @PSTestCase(input = "7\n1 4 4 4 4 1 1", output = "16"),
      @PSTestCase(input = "4\n1 4 2 3", output = "8")
  })
  void testAllCases() {}
}
```

Each test case will be executed separately and displayed as:
- `[1] input: 3\n7\n7 1 5 9 6 7 3`
- `[2] input: 7\n1 4 4 4 4 1 1`
- `[3] input: 4\n1 4 2 3`

---

### 🔧 Classic Approach (Inheritance-based)

For those who prefer the traditional approach, you can extend `ProblemSolvingTest`.

**1. Create a Test Class**

Extend `ProblemSolvingTest` and pass the timeout limit (in seconds) to the super constructor.

**2. Implement Abstract Methods**

- `setData()`: Provide test cases as a Stream<Arguments>. Each argument contains the **Input String** and the **Expected Output String**.
- `runMain()`: Call your solution's main method.

#### 📝 Example Code

```java
import org.dukcode.psutils.ProblemSolvingTest;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

class FenceTest extends ProblemSolvingTest {

  public FenceTest() {
    super(1);  // 1 second timeout
  }

  public static Stream<Arguments> setData() {
    return Stream.of(
      Arguments.of(
        """
        3
        7
        7 1 5 9 6 7 3
        """,
        "20"
      ),
      Arguments.of(
        """
        7
        1 4 4 4 4 1 1
        """,
        "16"
      )
    );
  }

  @Override
  protected void runMain() throws Exception {
    Fence.main(new String[]{});
  }
}
```

## 🛠 Utilities Reference

`Console`

A wrapper around java.util.Scanner to simplify reading input in your solution code. It manages the Scanner lifecycle automatically.

```java
import org.dukcode.psutils.Console;

public class MySolution {
  public static void main(String[] args) {
    String line = Console.readLine();
    // ... solve problem
  }
}
```

`Assertions`

Provides strict timeout assertions used internally by the test framework. You generally don't need to use this directly if you use `@PSTest` annotation or extend `ProblemSolvingTest`.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](https://www.google.com/search?q=LICENSE) file for details.
