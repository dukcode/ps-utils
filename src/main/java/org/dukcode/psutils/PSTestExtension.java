package org.dukcode.psutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

/**
 * JUnit 5 Extension for problem solving tests.
 *
 * <p>This extension handles:
 * <ul>
 *   <li>I/O redirection (System.in and System.out)</li>
 *   <li>Timeout assertions</li>
 *   <li>Output verification</li>
 * </ul>
 */
public class PSTestExtension implements BeforeEachCallback, AfterEachCallback,
    InvocationInterceptor {

  // 다른 Extension과의 충돌을 피하기 위한 네임스페이스
  private static final Namespace NAMESPACE = Namespace.create(PSTestExtension.class);
  // 원본 System.out을 저장하기 위한 키
  private static final String ORIGINAL_OUT_KEY = "originalOut";
  // 출력 캡처 스트림을 저장하기 위한 키
  private static final String CAPTOR_KEY = "captor";

  /**
   * 각 테스트 메서드 실행 전에 호출됨.
   * System.out을 캡처하기 위한 출력 리다이렉션을 설정함.
   *
   * @param context 현재 테스트의 확장 컨텍스트
   */
  @Override
  public void beforeEach(ExtensionContext context) {
    // 이 테스트를 위한 확장 스토어 가져오기
    Store store = getStore(context);
    // 리다이렉션 전에 원본 System.out 저장
    PrintStream originalOut = System.out;
    // 모든 출력을 캡처할 ByteArrayOutputStream 생성
    OutputStream captor = new ByteArrayOutputStream();

    // 나중에 복원 및 검증을 위해 둘 다 저장
    store.put(ORIGINAL_OUT_KEY, originalOut);
    store.put(CAPTOR_KEY, captor);

    // System.out을 캡처 스트림으로 리다이렉션
    System.setOut(new PrintStream(captor));
  }

  /**
   * 테스트 메서드 호출을 가로채서 문제 풀이 테스트 로직을 실행함.
   * 개별 테스트 메서드에 있는 @PSTestCase 애노테이션을 처리함.
   *
   * @param invocation 테스트 메서드 호출
   * @param invocationContext 리플렉션 호출 컨텍스트
   * @param extensionContext 확장 컨텍스트
   * @throws Throwable 테스트 실행이 실패한 경우
   */
  @Override
  public void interceptTestMethod(Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) throws Throwable {

    // 호출되는 테스트 메서드 가져오기
    Method testMethod = invocationContext.getExecutable();
    // @PSTestCase 애노테이션이 있는지 확인
    PSTestCase testCase = testMethod.getAnnotation(PSTestCase.class);

    if (testCase == null) {
      // @PSTestCase 애노테이션이 없으면 일반 테스트로 실행 (@PSTestCases일 수 있음)
      invocation.proceed();
      return;
    }

    // @PSTest 설정을 읽기 위해 테스트 클래스 가져오기
    Class<?> testClass = extensionContext.getRequiredTestClass();
    PSTest psTest = testClass.getAnnotation(PSTest.class);

    // 클래스에 @PSTest가 있는지 검증
    if (psTest == null) {
      throw new IllegalStateException(
          "@PSTestCase must be used within a class annotated with @PSTest");
    }

    // 애노테이션에서 테스트 설정 추출
    double timeout = psTest.timeout();
    Class<?> solutionClass = psTest.solution();
    String input = testCase.input();
    String expectedOutput = testCase.output();

    // 타임아웃 검증과 함께 테스트 실행
    Assertions.assertTimeout(timeout, () -> {
      runSolution(solutionClass, input);
      verifyOutput(extensionContext, expectedOutput);
    });

    // 이미 테스트를 실행했으므로 원본 빈 테스트 메서드 본문은 건너뜀
    invocation.skip();
  }

  /**
   * 각 테스트 메서드 실행 후에 호출됨.
   * System.out을 원래대로 복원하고 캡처된 출력을 콘솔에 출력함.
   *
   * @param context 현재 테스트의 확장 컨텍스트
   */
  @Override
  public void afterEach(ExtensionContext context) {
    // 저장된 스트림들 가져오기
    Store store = getStore(context);
    PrintStream originalOut = store.get(ORIGINAL_OUT_KEY, PrintStream.class);
    OutputStream captor = store.get(CAPTOR_KEY, OutputStream.class);

    if (originalOut != null) {
      // System.out을 원래대로 복원
      System.setOut(originalOut);
      if (captor != null) {
        // 캡처된 출력을 콘솔에 출력 (디버깅 용도)
        System.out.println(cleanEachLine(captor.toString()));
      }
    }
  }

  /**
   * 솔루션의 main 메서드를 실행함.
   *
   * @param solutionClass 실행할 솔루션 클래스
   * @param input System.in에 주입할 입력 문자열
   * @throws Exception 솔루션 실행 중 예외 발생 시
   */
  private void runSolution(Class<?> solutionClass, String input) throws Exception {
    try {
      // 입력을 System.in에 로드
      loadInput(input);
      // 리플렉션으로 main 메서드 가져오기
      Method mainMethod = solutionClass.getMethod("main", String[].class);
      // main 메서드 실행 (static 메서드이므로 null 전달)
      mainMethod.invoke(null, (Object) new String[]{});
    } finally {
      // 테스트 간 격리를 위해 항상 Scanner 닫기
      Console.close();
    }
  }

  /**
   * 입력 문자열을 System.in으로 리다이렉션함.
   *
   * @param input 입력 문자열
   */
  private void loadInput(String input) {
    // 문자열을 바이트 배열로 변환
    byte[] buf = input.getBytes();
    // ByteArrayInputStream으로 System.in 교체
    System.setIn(new ByteArrayInputStream(buf));
  }

  /**
   * 캡처된 출력과 예상 출력을 비교하여 검증함.
   *
   * @param context 확장 컨텍스트
   * @param expectedOutput 예상 출력 문자열
   */
  private void verifyOutput(ExtensionContext context, String expectedOutput) {
    // 캡처된 출력 스트림 가져오기
    Store store = getStore(context);
    OutputStream captor = store.get(CAPTOR_KEY, OutputStream.class);

    if (captor == null) {
      throw new IllegalStateException("Output captor not found");
    }

    // 실제 출력과 예상 출력을 정규화
    String actualOutput = cleanEachLine(captor.toString());
    String expected = cleanEachLine(expectedOutput);

    // AssertJ로 검증 (더 읽기 좋은 에러 메시지 제공)
    org.assertj.core.api.Assertions.assertThat(actualOutput.trim())
        .isEqualTo(expected.trim());
  }

  /**
   * 문자열의 각 줄 끝 공백을 제거함 (Java 8 호환).
   *
   * @param source 정규화할 문자열
   * @return 각 줄의 후행 공백이 제거된 문자열
   */
  private String cleanEachLine(String source) {
    // 개행 문자로 분리하여 각 줄의 후행 공백 제거
    List<String> lines = Arrays.stream(source.split("\n"))
        .map(s -> s.replaceAll("\\s+$", "")) // stripTrailing() 대체 (Java 8 호환)
        .collect(Collectors.toList());       // toList() 대체 (Java 8 호환)

    // StringJoiner로 다시 합치기
    StringJoiner lineJoiner = new StringJoiner("\n");
    for (String line : lines) {
      lineJoiner.add(line);
    }

    return lineJoiner.toString();
  }

  /**
   * 현재 컨텍스트의 Extension Store를 가져옴.
   *
   * @param context 확장 컨텍스트
   * @return Extension Store
   */
  private Store getStore(ExtensionContext context) {
    return context.getStore(NAMESPACE);
  }
}
