# Jackson 2.x 升级到 Jackson 3.x 实战指南

## 一、背景与概述

Jackson 是 Java 生态中最流行的 JSON 处理库，广泛应用于 Spring Boot、Spring Cloud Gateway 等主流框架中。2024 年 Jackson 3.0 正式发布，带来了重大架构升级，从包名到 API 都发生了显著变化。本文将基于实际项目升级经验，详细记录 Jackson 2.x 向 Jackson 3.x 迁移过程中需要注意的关键变更点。

Jackson 3.x 的主要设计目标包括：统一包命名空间、分离核心模块与数据绑定模块、优化异步 JSON 处理能力等。这些变更虽然带来了更好的性能和扩展性，但也意味着开发者需要花费一定时间进行代码适配。本文将以一个实际的 JSON 工具类 JsonTools 为例，展示完整的升级过程。

## 二、包名变更

Jackson 2.x 使用 com.fasterxml.jackson 作为包名前缀，这是 Jackson 2.x 时代的标准命名空间。然而，在 Jackson 3.x 中，为了更好地支持 Jakarta EE 环境并解决包名冲突问题，核心库的包名前缀变更为 tools.jackson。这一变更影响深远，几乎所有涉及 Jackson 的 import 语句都需要相应修改。

以下是本次升级中涉及的主要包名对照关系。在 Jackson 2.x 中，核心包位于 com.fasterxml.jackson.core，数据绑定包位于 com.fasterxml.jackson.databind。升级后，这些包分别迁移到了 tools.jackson.core 和 tools.jackson.databind。这种变更看似简单，但在大型项目中可能涉及数百个文件，需要谨慎处理。建议使用 IDE 的全局搜索替换功能批量处理，同时注意区分同名但不同包的其他类。

```
// Jackson 2.x
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

// Jackson 3.x
import tools.jackson.core.JsonProcessingException;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
```

值得注意的是，并非所有 Jackson 相关的包都发生了变更。Jackson Annotations 注解包（如 @JsonProperty、@JsonIgnore 等）仍然保留在 com.fasterxml.jackson.annotation 包下，这是因为注解的变更会影响大量第三方库，迁移成本过高。这一设计决策体现了 Jackson 团队对向后兼容性的考量，我们在升级时需要特别注意区分哪些类需要修改 import，哪些保持不变。

## 三、JsonParser.Feature 的移除与替代

这是 Jackson 3.x 中最重大的 API 变更之一。在 Jackson 2.x 中，JsonParser.Feature 枚举类包含了大量与 JSON 解析相关的配置选项，如允许无引号字段名、允许单引号、允许注释等。这些特性在 Jackson 3.x 中被完全重构，不再使用 JsonParser.Feature 枚举，而是统一迁移到 JsonReadFeature 枚举中。

这种设计变更的主要原因是将 JSON 格式相关的读取特性集中管理，使 API 更加清晰。Jackson 2.x 时代，解析器特性分散在 JsonParser.Feature 和 JsonReadFeature 两个地方，容易造成混淆。Jackson 3.x 通过整合这些特性，提供了一个更加一致和可预测的 API 表面。

```
// Jackson 2.x 配置方式
ObjectMapper mapper = new ObjectMapper();
mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

Jackson 3.x 中，JsonParser.Feature 枚举已被彻底移除。原来的 ALLOW_UNQUOTED_FIELD_NAMES 更名为 ALLOW_UNQUOTED_PROPERTY_NAMES，而 ALLOW_COMMENTS 则更名为 ALLOW_JAVA_COMMENTS，以更准确地描述其功能——支持 Java/C++ 风格的注释。所有这些特性现在都通过 JsonReadFeature 枚举进行配置。

// Jackson 3.x 配置方式
JsonMapper.builder()
    .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
    .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
    .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
    .build();
```

此外，在 Jackson 2.x 中使用 JsonReadFeature 时，某些特性需要通过 .mappedFeature() 方法获取底层特性对象才能配置。在 Jackson 3.x 中，这一要求被移除，可以直接使用 JsonReadFeature 枚举值进行配置，使代码更加简洁直观。

// Jackson 2.x
mapper.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true);

// Jackson 3.x
mapper.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS, true);

## 四、ObjectMapper 创建方式的变化


Jackson 3.x 推荐使用建造者模式（Builder Pattern）来创建 ObjectMapper 实例，这取代了传统的直接实例化方式。这种变更使得配置更加流畅和链式化，同时也为未来的扩展预留了空间。JsonMapper.builder() 方法返回一个 JsonMapperBuilder 实例，支持链式调用来配置各种特性，最后通过 build() 方法生成配置完成的 ObjectMapper。

```
// Jackson 2.x
private static final ObjectMapper MAPPER = new ObjectMapper();
MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

// Jackson 3.x
private static final ObjectMapper MAPPER = JsonMapper.builder()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
    .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
    .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
    .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
    .build();
```

使用建造者模式的优势在于配置过程更加清晰，所有配置集中在一处，避免了分散的 configure 调用使代码变得杂乱。此外，建造者模式支持更灵活的配置组合，可以方便地创建具有不同配置的多个 ObjectMapper 实例。在性能方面，建造者模式允许在构建过程中进行优化，减少运行时的配置开销。

## 五、特性对照表


为了方便开发者快速查阅和对照，以下整理了 Jackson 2.x 到 Jackson 3.x 的完整特性迁移对照表。在进行代码迁移时，建议将此表放在手边作为参考。

```
功能描述            Jackson 2.x	Jackson 3.x	备注
允许无引号字段名	   JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES	JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES	枚举名变更
允许单引号           JsonParser.Feature.ALLOW_SINGLE_QUOTES	JsonReadFeature.ALLOW_SINGLE_QUOTES	包位置变更
允许Java/C++注释    JsonParser.Feature.ALLOW_COMMENTS	JsonReadFeature.ALLOW_JAVA_COMMENTS	枚举名变更
允许YAML注释            JsonParser.Feature.ALLOW_YAML_COMMENTS	JsonReadFeature.ALLOW_YAML_COMMENTS	包位置变更
允许前导零数字             JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS	JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS	配置方式变更
允许控制字符          JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS	JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS	配置方式变更
允许浮点数前导小数点      无	JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS	Jackson 3.x 新增
允许前导加号数字        无	JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS	Jackson 3.x 新增
允许NaN值          JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS	JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS	包位置变更
允许数组尾部逗号        无	JsonReadFeature.ALLOW_TRAILING_COMMA	Jackson 3.x 新增
允许缺失值           无	      JsonReadFeature.ALLOW_MISSING_VALUES	Jackson 3.x 新增
```

从表中可以看出，Jackson 3.x 不仅完成了旧特性的迁移，还新增了一批实用的解析特性。这些新特性使得 Jackson 对非标准 JSON 格式的容忍度更高，在处理来自不同数据源的 JSON 数据时更加灵活。特别是 ALLOW_MISSING_VALUES 和 ALLOW_TRAILING_COMMA 这两个特性，在处理某些遗留系统或不规范数据时非常有用。

## 六、常见问题与解决方案

### 6.1 依赖冲突问题

在升级 Jackson 版本时，最常见的问题是依赖冲突。由于 Jackson 2.x 和 Jackson 3.x 的包名完全不同，理论上可以共存，但在实际项目中往往会遇到各种问题。建议通过 Maven  BOM（Bill of Materials）来统一管理 Jackson 版本，确保所有模块使用一致的 Jackson 版本。

在 Maven 项目中，可以这样配置 jackson-bom：

```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>tools.jackson</groupId>
            <artifactId>jackson-bom</artifactId>
            <version>3.0.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 6.2 Spring Boot 集成问题

Spring Boot 2.x 默认使用 Jackson 2.x，Spring Boot 3.x 则升级到了 Jackson 3.x。如果你的项目使用了 Spring Boot，需要确保 Spring Boot 版本与 Jackson 版本保持一致。Spring Boot 4.0 推荐使用 Jackson 3.x，具体的版本对应关系请参考 Spring Boot 官方文档。

### 6.3 第三方库兼容性问题

一些基于 Jackson 的第三方库可能尚未完成 Jackson 3.x 的升级。在升级过程中，如果遇到第三方库报错，首先需要检查该库是否有支持 Jackson 3.x 的新版本。如果没有，可能需要暂时降级 Jackson 版本，或寻找替代方案。

## 七、升级建议与最佳实践

基于本次实际升级经验，总结以下建议供读者参考。首先，在升级前务必做好完整的代码备份和测试用例准备，Jackson 作为核心库，任何变更都可能影响到系统的 JSON 处理功能，建议在测试环境充分验证后再上线。

其次，建议使用 IDE 的全局搜索功能批量处理 import 语句变更，可以大大提高迁移效率。同时，使用正则表达式进行查找替换时要注意匹配准确性，避免误改其他包下的同名类。对于大型项目，可以考虑分模块逐步升级，每次升级后运行相关测试用例，确保功能正常。

第三，虽然 Jackson 3.x 保留了大部分 API 的兼容性，但仍有一些废弃（Deprecated）API 在未来版本中可能被移除。建议在升级后检查项目中的警告信息，逐步迁移到推荐的 API 写法。

最后，建议在项目中统一 Jackson 版本管理，避免不同模块使用不同版本导致的潜在问题。通过 Maven BOM 或 Gradle version catalog 可以很好地解决这个问题。

## 八、总结

Jackson 2.x 到 Jackson 3.x 的升级是一次重大版本变更，涉及包名重构、API 调整等多个方面。从本文的实践来看，虽然变更点较多，但整体升级路径清晰，Jackson 团队在设计 3.x 版本时充分考虑了迁移成本，大多数业务代码只需要修改 import 语句和少量 API 调用方式即可完成升级。

核心变更可以归纳为三点：包名前缀从 com.fasterxml.jackson 变为 tools.jackson；JsonParser.Feature 枚举被移除，相关特性迁移到 JsonReadFeature；ObjectMapper 的创建推荐使用 JsonMapper.builder() 建造者模式。

对于正在使用 Jackson 2.x 的项目，建议尽快规划升级计划。Jackson 3.x 不仅修复了 2.x 版本中的一些历史遗留问题，还引入了许多新特性和性能优化。尽早升级可以让项目更好地融入 Jackson 生态系统，享受新版本带来的各种改进。
