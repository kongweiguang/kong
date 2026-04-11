# Tomls Refactor Design

## Background

`kong-core/src/main/java/io/github/kongweiguang/core/file/Tomls.java` currently mixes multiple responsibilities in one file:

- public file/string I/O entry points
- line-by-line TOML parsing
- scalar and array value parsing
- nested table and table-array path resolution
- TOML serialization and string escaping

This makes the class harder to understand, harder to extend, and harder to test in isolation. The current tests only verify round-trip behavior and null-skipping, so internal regressions are easy to miss.

## Goals

- Split TOML support into multiple focused files with clear responsibilities.
- Normalize API naming so read/write behavior is easier to understand.
- Preserve the currently supported feature set:
  - root key/value pairs
  - nested tables like `[server]`
  - table arrays like `[[clients]]`
  - strings, booleans, integers, longs, doubles
  - arrays of scalar values and nested arrays
  - skip `null` values during serialization
- Improve maintainability and local performance by avoiding repeated mixed-purpose logic in a single class.
- Add focused tests around parser and writer responsibilities.

## Non-Goals

- Full TOML spec compliance.
- Adding date/time types, inline tables, multiline strings, or comments preservation.
- Maintaining backward compatibility with the existing `Tomls` static API.

## Recommended Approach

Use a lightweight modular design with a small public facade and package-private implementation classes.

This is preferred over introducing a full object model such as `TomlDocument` because the current library operates on `Map<String, Object>` and the requested goal is focused refactoring, not a data-model redesign.

## Proposed Structure

Package: `io.github.kongweiguang.core.file.toml`

Files and responsibilities:

- `Tomls`
  - Public facade in the existing package.
  - Exposes the normalized public API for reading and writing TOML.
  - Delegates to parser/writer classes.
- `TomlParser`
  - Reads TOML from `Reader` or `String`.
  - Scans lines, identifies table headers and key-value pairs.
  - Delegates scalar and array parsing.
- `TomlValueParser`
  - Parses a raw TOML value string into Java values.
  - Handles quoted strings, booleans, numbers, arrays, and unescaping.
- `TomlPathResolver`
  - Resolves or creates nested tables and table arrays for dotted paths.
  - Owns validation for conflicts such as “key exists but is not a table”.
- `TomlWriter`
  - Serializes `Map<String, Object>` to `Writer` or `String`.
  - Controls ordering: scalar values first, then tables, then table arrays.
- `TomlValueWriter`
  - Serializes scalar values and arrays.
  - Owns escaping rules for strings.

## Public API

Replace the current API with clearer names:

- `Map<String, Object> read(File file)`
- `Map<String, Object> read(String content)`
- `String write(Map<String, Object> data)`
- `File write(Map<String, Object> data, File file)`

Notes:

- The API remains map-based for simplicity and continuity with current behavior.
- The old `toMap`, `toTomlStr`, and `toFile` methods may be removed because compatibility is not required.
- `Tomls` remains the single public entry point so callers do not need to know internal class structure.

## Parsing Design

`TomlParser` handles document structure only.

Flow:

1. Trim each line and skip blank/comment-only lines.
2. If line starts with `[`:
   - detect regular table or table array
   - delegate path resolution to `TomlPathResolver`
   - update current target table
3. Otherwise parse `key = value`:
   - split on the first `=`
   - trim key and raw value
   - delegate value conversion to `TomlValueParser`
   - store result in current table

Responsibilities intentionally excluded from `TomlParser`:

- escape decoding details
- numeric detection details
- nested array tokenization details

These stay in `TomlValueParser`.

## Writing Design

`TomlWriter` owns document layout.

Flow:

1. Emit simple key-value pairs for the current table.
2. Emit a blank line only if needed before nested sections.
3. Emit nested tables recursively.
4. Emit table arrays recursively.

`TomlValueWriter` serializes values without knowing table layout.

Behavior:

- `null` values are skipped by the writer entry logic.
- Strings are escaped with a char-by-char loop.
- Arrays are emitted recursively.
- Unknown object types are serialized via `toString()` as quoted strings, matching current behavior.

## Performance Considerations

- Keep `LinkedHashMap` for deterministic write order.
- Continue using char-by-char parsing and escaping instead of regex.
- Avoid recomputing “is table array” logic in multiple places by centralizing type checks in writer helpers.
- Separate layout decisions from value serialization so hot paths are smaller and easier to optimize later.

This is a maintainability-first refactor with modest performance improvement from reduced branching and clearer specialization, not a wholesale parser rewrite.

## Error Handling

- Invalid table syntax throws `IllegalArgumentException`.
- Type conflicts during path resolution throw `IllegalArgumentException`.
- I/O failures from file APIs propagate as `IOException`.
- String-based read/write APIs wrap checked I/O only where the backing source/sink is in-memory and failure is unexpected.

## Testing Strategy

Refactor tests into smaller, focused classes:

- `TomlsFacadeTest`
  - verifies public read/write methods
- `TomlParserTest`
  - verifies table parsing, array-of-tables parsing, and scalar parsing
- `TomlWriterTest`
  - verifies serialization ordering, string escaping, and null skipping
- `TomlRoundTripTest`
  - verifies supported structures survive write/read cycles

At minimum, the new suite should cover:

- nested table creation via dotted paths
- table arrays under root and nested tables
- string escaping and unescaping
- integer vs long vs double parsing
- nested arrays
- null omission during serialization

## File Impact

Expected touched files:

- Modify: `kong-core/src/main/java/io/github/kongweiguang/core/file/Tomls.java`
- Create: `kong-core/src/main/java/io/github/kongweiguang/core/file/toml/TomlParser.java`
- Create: `kong-core/src/main/java/io/github/kongweiguang/core/file/toml/TomlValueParser.java`
- Create: `kong-core/src/main/java/io/github/kongweiguang/core/file/toml/TomlPathResolver.java`
- Create: `kong-core/src/main/java/io/github/kongweiguang/core/file/toml/TomlWriter.java`
- Create: `kong-core/src/main/java/io/github/kongweiguang/core/file/toml/TomlValueWriter.java`
- Replace or expand tests under: `kong-core/src/test/java/io/github/kongweiguang/core/file/`

## Open Decisions Resolved

- Backward compatibility: not required.
- Public surface area: one facade class only.
- Data representation: continue using `Map<String, Object>` and `List<?>`.
- Scope: refactor and normalize only the existing supported TOML subset.

## Success Criteria

- No single TOML class mixes document parsing, value parsing, and writing responsibilities.
- Public API names are clearer than the original conversion-style names.
- Existing supported behaviors still work after refactor.
- Tests verify parser and writer responsibilities independently, not only end-to-end.
