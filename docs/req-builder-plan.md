# Req/Builder 逻辑修复与优雅化改造计划

## Summary
围绕 `Req` 与相关 Builder 做一次“小步安全重构”：先修复确定的逻辑缺陷，再统一 API 风格与失败语义，最后补齐测试，确保行为稳定且代码可维护性更高。整体保持向后兼容，不做破坏性 API 变更。

## Key Changes
1. 修复逻辑问题（必须）
   - 修复 `retry(Consumer<RetryableTask<Res>> consumer)` 的空值校验对象错误（校验 `consumer` 而非成员 `retry`）。
   - 修复 `before()` 在空 cookie 场景写入空 `Cookie` 头的问题，仅在 `cookieMap` 非空时写入。
   - 优化 `cookie2Str`，移除末尾多余分隔符，输出更规范。

2. 统一失败语义与 API 一致性（优雅性）
   - `url(String)` 对非法 URL 改为 fail-fast：抛出明确 `IllegalArgumentException`，错误信息包含输入值。
   - 统一 query 命名风格：保留现有 `encodeQuery/encodedQuery` 兼容入口，但内部收敛到一套实现并标注推荐方法（避免命名歧义）。
   - `Req` 设为工具类形态：`final` + 私有构造，防止误实例化。
   - `Req` 的 `get/post/put...` 收敛到一个私有工厂方法，消除重复代码并统一行为。

3. 最小行为增强（不破坏兼容）
   - 对 `header/addHeader/cookie/form/query` 继续保持“忽略 null 参数”的兼容策略，但在 javadoc 中明确该约定（避免调用方误解）。
   - 对关键异常信息做可读性增强（方法名 + 入参上下文），便于排查线上问题。

## Test Plan
1. 单元测试新增/修订
   - `retry(null)` 抛出预期异常且提示明确。
   - `form_urlencoded` 提交中文、空格、`+`、`%` 等值时，编码结果符合预期（验证不会双重/错误编码）。
   - 空 cookie 不产生 `Cookie` 头；非空 cookie 头格式正确且无尾部分号空格。
   - `url("::invalid::")` 直接抛明确异常；合法 URL 正常构建。
   - `Req.get/post/...` 通过统一工厂后行为不变（method/url/contentType 断言）。

2. 回归测试
   - HTTP JSON/body、multipart 上传、WS/SSE 构建与执行入口保持原有用法可用。
   - 现有测试全量通过，新增测试覆盖本次修复点。

## Public API / Compatibility
- 不移除现有公开方法，保持二进制兼容。
- `url(String)` 从“可能静默回退”改为“明确抛错”，属于行为收紧；这是有意修复，需在变更说明中标注。
- query 命名只做“推荐收敛 + 兼容保留”，不做强制迁移。

## Assumptions
- 默认采用“先修 bug、后整理 API”的两阶段提交策略，降低回归风险。
- 当前优先级是稳定性与可维护性，不引入大规模架构重写（如不可变 Builder、线程安全全面改造）。
- 以 `kong-http` 模块现有测试框架为基线扩展，不新增额外测试基础设施。
