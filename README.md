# 🧩 Java PS Utils

**Java PS Utils** is a lightweight utility library designed to streamline **Competitive Programming (Algorithm)** testing using **JUnit 5**.

It abstracts standard I/O redirection (`System.in`, `System.out`) and provides a simple interface for **Parameterized Tests** and **Timeout Assertions**, making your solution verification process clean and efficient.

## ✨ Features

- 🛡 **Zero Boilerplate**: Focus on your algorithm logic, not test setup.
- ⏱ **Timeout Assertions**: Easily detect infinite loops or inefficient solutions with strict timeouts.
- 📝 **I/O Redirection**: Automatically handles `System.in` and captures `System.out` for assertion.
- 🧪 **Parameterized Testing**: Test multiple input/output cases in a single test class.
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
  implementation 'com.github.dukcode:ps-utils:1.0.0'
}
```

### Gradle (Kotlin DSL)

```kotlin
repositories {
  mavenCentral()
  maven("[https://jitpack.io](https://jitpack.io)")
}

dependencies {
implementation("com.github.dukcode:ps-utils:1.0.0")
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
    <version>1.0.0</version>
</dependency>
```

## 🚀 Usage

**1. Create a Test Class**

Extend `ProblemSolvingTest` and pass the timeout limit (in seconds) to the super constructor.

**2. Implement Abstract Methods**

- `setData()`: Provide test cases as a Stream<Arguments>. Each argument contains the **Input String** and the **Expected Output String**.
- `runMain()`: Call your solution's main method.

### 📝 Example Code

Here is a full example of testing a solution named Fence.

```java
import util.ProblemSolvingTest;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

class FenceTest extends ProblemSolvingTest {

  // Set timeout to 1 second
  public FenceTest() {
    super(1);
  }

  public static Stream<Arguments> setData() {
    return Stream.of(
      Arguments.of(
        // Input Case 1
        """
        3
        7
        7 1 5 9 6 7 3
        """,
        // Expected Output 1
        """
        20
        """
      ),
      Arguments.of(
        // Input Case 2
        """
        7
        1 4 4 4 4 1 1
        """,
        // Expected Output 2
        """
        16
        """
      )
    );
  }

  @Override
  protected void runMain() throws Exception {
    // Run the main method of your solution class
    Fence.main(new String[]{});
  }
}
```

## 🛠 Utilities Reference

`Console`

A wrapper around java.util.Scanner to simplify reading input in your solution code. It manages the Scanner lifecycle automatically when used with ProblemSolvingTest.

```java
import util.Console;

public class MySolution {
  public static void main(String[] args) {
    String line = Console.readLine();
    // ... solve problem
  }
}
```

`Assertions`

Provides strict timeout assertions used internally by the test framework. You generally don't need to use this directly if you extend `ProblemSolvingTest`.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](https://www.google.com/search?q=LICENSE) file for details.
