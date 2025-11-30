package org.dukcode.psutils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

/**
 * {@link PSTestCases}에 대한 테스트 템플릿 호출 컨텍스트를 제공함.
 *
 * <p>이 프로바이더는 {@link PSTestCases} 애노테이션에 정의된 각 {@link PSTestCase}에 대해
 * 별도의 테스트 호출을 생성함. JUnit 5의 TestTemplate 메커니즘을 사용하여
 * 하나의 테스트 메서드가 여러 번 실행되도록 함.
 */
public class PSTestCasesProvider implements TestTemplateInvocationContextProvider {

  /**
   * 이 프로바이더가 주어진 테스트 템플릿을 지원하는지 확인함.
   * @PSTestCases 애노테이션이 있는 메서드만 지원함.
   *
   * @param context 확장 컨텍스트
   * @return @PSTestCases가 있으면 true, 없으면 false
   */
  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    // 테스트 메서드에 @PSTestCases 애노테이션이 있는지 확인
    return context.getTestMethod()
        .map(method -> method.isAnnotationPresent(PSTestCases.class))
        .orElse(false);
  }

  /**
   * 각 테스트 케이스에 대한 호출 컨텍스트를 제공함.
   * @PSTestCases에 정의된 각 케이스에 대해 별도의 컨텍스트를 생성함.
   *
   * @param context 확장 컨텍스트
   * @return 각 테스트 케이스에 대한 호출 컨텍스트 스트림
   */
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext context) {

    // 테스트 메서드와 @PSTestCases 애노테이션 가져오기
    Method testMethod = context.getRequiredTestMethod();
    PSTestCases testCases = testMethod.getAnnotation(PSTestCases.class);

    if (testCases == null) {
      // @PSTestCases가 없으면 빈 스트림 반환
      return Stream.empty();
    }

    // @PSTest 설정을 읽기 위해 테스트 클래스 가져오기
    Class<?> testClass = context.getRequiredTestClass();
    PSTest psTest = testClass.getAnnotation(PSTest.class);

    // 클래스에 @PSTest가 있는지 검증
    if (psTest == null) {
      throw new IllegalStateException(
          "@PSTestCases must be used within a class annotated with @PSTest");
    }

    // 각 테스트 케이스에 대해 별도의 호출 컨텍스트 생성
    PSTestCase[] cases = testCases.value();
    return Arrays.stream(cases)
        .map(testCase -> new PSTestInvocationContext(psTest, testCase));
  }

  /**
   * 단일 테스트 케이스에 대한 호출 컨텍스트.
   * 각 테스트 케이스의 표시 이름과 실행에 필요한 Extension을 제공함.
   */
  private static class PSTestInvocationContext implements TestTemplateInvocationContext {

    // 클래스 레벨의 @PSTest 애노테이션
    private final PSTest psTest;
    // 현재 실행할 테스트 케이스
    private final PSTestCase testCase;

    /**
     * 생성자.
     *
     * @param psTest 클래스 레벨의 @PSTest 애노테이션
     * @param testCase 실행할 테스트 케이스
     */
    PSTestInvocationContext(PSTest psTest, PSTestCase testCase) {
      this.psTest = psTest;
      this.testCase = testCase;
    }

    /**
     * 테스트 케이스의 표시 이름을 반환함.
     * IDE와 테스트 리포트에서 각 케이스를 구별하기 위해 사용됨.
     *
     * @param invocationIndex 호출 인덱스 (1부터 시작)
     * @return 테스트 케이스의 표시 이름
     */
    @Override
    public String getDisplayName(int invocationIndex) {
      // 입력 문자열 가져오기
      String input = testCase.input();
      // 30자로 제한하고 개행 문자를 이스케이프 처리
      String preview = input.length() > 30
          ? input.substring(0, 30).replaceAll("\n", "\\\\n") + "..."
          : input.replaceAll("\n", "\\\\n");
      // "[1] input: ..." 형식으로 표시
      return String.format("[%d] input: %s", invocationIndex, preview);
    }

    /**
     * 이 테스트 케이스 실행에 필요한 추가 Extension을 반환함.
     * PSTestCaseExecutor를 사용하여 실제 테스트를 실행함.
     *
     * @return Extension 리스트
     */
    @Override
    public java.util.List<Extension> getAdditionalExtensions() {
      // PSTestCaseExecutor를 Extension으로 등록하여 테스트 실행
      return Arrays.asList(new PSTestCaseExecutor(psTest, testCase));
    }
  }
}
