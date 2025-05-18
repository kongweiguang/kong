package io.github.kongweiguang.core.lang;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Opt类的单元测试
 * 基于OptDemo中的示例转换而来
 */
public class OptTest {

    private static final Map<String, Double> exchangeRates = new HashMap<>();

    static {
        // 初始化汇率数据(相对于CNY)
        exchangeRates.put("USD", 0.14);  // 1 CNY = 0.14 USD
        exchangeRates.put("EUR", 0.13);  // 1 CNY = 0.13 EUR
        exchangeRates.put("JPY", 21.0);  // 1 CNY = 21.0 JPY
    }

    /**
     * 测试Opt的基本用法
     */
    @Test
    public void testBasicUsage() {
        // 创建不同状态的Opt
        Opt<String, String> present = Opt.of("Hello");
        Opt<String, String> empty = Opt.empty();
        Opt<String, String> error = Opt.error("发生错误");

        // 测试状态检查
        assertTrue(present.isPresent());
        assertTrue(empty.isEmpty());
        assertTrue(error.isError());

        // 测试获取值
        assertEquals("Hello", present.get());
        assertThrows(NoSuchElementException.class, empty::get);
        
        // 测试获取错误
        assertEquals("发生错误", error.getError());
        assertThrows(NoSuchElementException.class, present::getError);

        // 测试OrElse方法
        assertEquals("Hello", present.orElse("默认值"));
        assertEquals("默认值", empty.orElse("默认值"));
        assertEquals("默认值", error.orElse("默认值"));

        // 测试链式调用
        String result = Opt.ofNullable("  Hello World  ")
                .map(String::trim)
                .filter(s -> s.length() > 5)
                .map(String::toUpperCase)
                .orElse("默认值");
        assertEquals("HELLO WORLD", result);
        
        // 测试链式调用 - 不满足过滤条件的情况
        String shortResult = Opt.ofNullable("  Hi  ")
                .map(String::trim)
                .filter(s -> s.length() > 5) // 不满足条件
                .map(String::toUpperCase)
                .orElse("默认值");
        assertEquals("默认值", shortResult);
    }

    /**
     * 测试三态匹配功能
     */
    @Test
    public void testTriStateMatching() {
        // 测试正常值的处理
        StringBuilder output = new StringBuilder();
        Opt<String, String> result1 = processInput("Hello")
                .match(
                        value -> output.append("处理值: ").append(value),
                        () -> output.append("输入为空"),
                        error -> output.append("处理错误: ").append(error)
                );
        assertTrue(result1.isPresent());
        assertEquals("处理值: Hello processed", output.toString());
        
        // 测试空值的处理
        output.setLength(0); // 清空
        Opt<String, String> result2 = processInput(null)
                .match(
                        value -> output.append("处理值: ").append(value),
                        () -> output.append("输入为空"),
                        error -> output.append("处理错误: ").append(error)
                );
        assertTrue(result2.isEmpty());
        assertEquals("输入为空", output.toString());
        
        // 测试错误的处理
        output.setLength(0); // 清空
        Opt<String, String> result3 = processInput("invalid")
                .match(
                        value -> output.append("处理值: ").append(value),
                        () -> output.append("输入为空"),
                        error -> output.append("处理错误: ").append(error)
                );
        assertTrue(result3.isError());
        assertEquals("处理错误: 无效输入", output.toString());
    }

    /**
     * 测试错误处理功能
     */
    @Test
    public void testErrorHandling() {
        // 测试成功的转换
        Opt<Double, String> conversionResult = convertCurrency(100.0, "CNY", "USD");
        assertTrue(conversionResult.isPresent());
        assertEquals(14.0, conversionResult.get(), 0.001);

        // 测试matchResult处理所有情况
        String resultMessage = conversionResult.matchResult(
                amount -> String.format("转换成功: 100 CNY = %.2f USD", amount),
                () -> "转换失败: 未提供输入",
                error -> "转换错误: " + error
        );
        assertEquals("转换成功: 100 CNY = 14.00 USD", resultMessage);

        // 测试错误的转换
        Opt<Double, String> errorResult = convertCurrency(100.0, "CNY", "XYZ");
        assertTrue(errorResult.isError());
        assertEquals("不支持的目标货币: XYZ", errorResult.getError());

        // 测试orElseThrowError
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                errorResult.orElseThrowError(
                        IllegalArgumentException::new,
                        () -> new IllegalStateException("空结果")
                )
        );
        assertEquals("不支持的目标货币: XYZ", exception.getMessage());
    }

    /**
     * 测试tryGet功能
     */
    @Test
    public void testTryGet() {
        // 测试读取不存在的文件
        Opt<String, IOException> nonExistentFile = Opt.ofTry(() ->
                Files.readString(Paths.get("nonexistent.toml"))
        );
        
        assertTrue(nonExistentFile.isError());
        assertInstanceOf(NoSuchFileException.class, nonExistentFile.getError());
        
        // 测试ofTry成功的情况
        Opt<String, RuntimeException> successfulTry = Opt.ofTry(() -> "success");
        assertTrue(successfulTry.isPresent());
        assertEquals("success", successfulTry.get());
    }

    /**
     * 测试实际业务场景
     */
    @Test
    public void testRealWorldExample() {
        // 测试成功登录并获取有效配置
        Opt<User, String> loginResult = login("admin", "password123");
        assertTrue(loginResult.isPresent());
        
        Opt<UserPreferences, String> preferences = loginResult
                .flatMap(OptTest::loadUserPreferences)
                .flatMap(OptTest::validatePreferences);
        
        assertTrue(preferences.isPresent());
        assertEquals("dark", preferences.get().theme());
        assertEquals("zh_CN", preferences.get().language());
        assertEquals("Asia/Shanghai", preferences.get().timezone());
        
        // 测试登录失败
        Opt<User, String> failedLogin = login("admin", "wrongpassword");
        assertTrue(failedLogin.isError());
        assertEquals("用户名或密码错误", failedLogin.getError());
        
        // 测试无效的偏好设置
        Opt<User, String> user2Login = login("user2", "password123");
        assertTrue(user2Login.isPresent());
        
        Opt<UserPreferences, String> invalidPrefs = user2Login
                .flatMap(OptTest::loadUserPreferences);
        
        assertTrue(invalidPrefs.isError());
        assertEquals("无效的主题: invalid_theme", invalidPrefs.getError());
    }

    // 辅助方法 - 处理输入
    private static Opt<String, String> processInput(String input) {
        if (input == null) {
            return Opt.empty();
        }
        if (input.equalsIgnoreCase("invalid")) {
            return Opt.error("无效输入");
        }
        return Opt.of(input + " processed");
    }

    // 辅助方法 - 货币转换
    private static Opt<Double, String> convertCurrency(double amount, String fromCurrency, String toCurrency) {
        if (!fromCurrency.equals("CNY")) {
            return Opt.error("目前只支持从CNY转换");
        }

        if (!exchangeRates.containsKey(toCurrency)) {
            return Opt.error("不支持的目标货币: " + toCurrency);
        }

        double rate = exchangeRates.get(toCurrency);
        return Opt.of(amount * rate);
    }

    // 辅助方法 - 用户登录
    private static Opt<User, String> login(String username, String password) {
        if (username == null || password == null) {
            return Opt.empty();
        }

        if ("admin".equals(username) && "password123".equals(password)) {
            return Opt.of(new User(1, "admin", "管理员"));
        } else if ("user2".equals(username) && "password123".equals(password)) {
            return Opt.of(new User(2, "user2", "普通用户"));
        } else {
            return Opt.error("用户名或密码错误");
        }
    }

    // 辅助方法 - 加载用户偏好设置
    private static Opt<UserPreferences, String> loadUserPreferences(User user) {
        if (user.id() == 1) {
            return Opt.of(new UserPreferences("dark", "zh_CN", "Asia/Shanghai"));
        } else if (user.id() == 2) {
            return Opt.error("无效的主题: invalid_theme"); // 模拟验证失败
        } else {
            return Opt.error("无法加载用户ID为 " + user.id() + " 的偏好设置");
        }
    }

    // 辅助方法 - 验证用户偏好设置
    private static Opt<UserPreferences, String> validatePreferences(UserPreferences prefs) {
        if (prefs.theme() == null || prefs.language() == null || prefs.timezone() == null) {
            return Opt.error("偏好设置不完整");
        }

        if (!isValidTheme(prefs.theme())) {
            return Opt.error("无效的主题: " + prefs.theme());
        }

        if (!isValidLanguage(prefs.language())) {
            return Opt.error("无效的语言: " + prefs.language());
        }

        if (!isValidTimezone(prefs.timezone())) {
            return Opt.error("无效的时区: " + prefs.timezone());
        }

        return Opt.of(prefs);
    }

    // 验证主题
    private static boolean isValidTheme(String theme) {
        return "light".equals(theme) || "dark".equals(theme) || "system".equals(theme);
    }

    // 验证语言
    private static boolean isValidLanguage(String language) {
        return language.matches("[a-z]{2}(_[A-Z]{2})?");
    }

    // 验证时区
    private static boolean isValidTimezone(String timezone) {
        return timezone.contains("/");
    }

    // 用户记录类
    private record User(int id, String username, String displayName) {
    }

    // 用户偏好设置记录类
    private record UserPreferences(String theme, String language, String timezone) {
    }
}