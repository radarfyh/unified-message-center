### 错误：

```
Fatal error compiling[m: Cannot load from object array because "this.hashes" is null
```

### 详细：

```
[[1;34mINFO[m] Scanning for projects...
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m----------------< [0;36mltd.huntinginfo:feng-library3-security[0;1m >-----------------[m
[[1;34mINFO[m] [1mBuilding feng-library3-security 1.0.0-SNAPSHOT[m
[[1;34mINFO[m]   from pom.xml
[[1;34mINFO[m] [1m--------------------------------[ jar ]---------------------------------[m
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mresources:3.3.1:resources[m [1m(default-resources)[m @ [36mfeng-library3-security[0;1m ---[m
[[1;34mINFO[m] Copying 3 resources from src\main\resources to target\classes
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mcompiler:3.13.0:compile[m [1m(default-compile)[m @ [36mfeng-library3-security[0;1m ---[m
[[1;34mINFO[m] Recompiling the module because of [1mchanged source code[m.
[[1;34mINFO[m] Compiling 61 source files with javac [debug parameters target 21] to target\classes
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[[1;34mINFO[m] [1;31mBUILD FAILURE[m
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[[1;34mINFO[m] Total time:  4.277 s
[[1;34mINFO[m] Finished at: 2026-02-06T10:39:44+08:00
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[[1;31mERROR[m] Failed to execute goal [32morg.apache.maven.plugins:maven-compiler-plugin:3.13.0:compile[m [1m(default-compile)[m on project [36mfeng-library3-security[m: [1;31mFatal error compiling[m: Cannot load from object array because "this.hashes" is null -> [1m[Help 1][m
[[1;31mERROR[m] 
[[1;31mERROR[m] To see the full stack trace of the errors, re-run Maven with the [1m-e[m switch.
[[1;31mERROR[m] Re-run Maven using the [1m-X[m switch to enable full debug logging.
[[1;31mERROR[m] 
[[1;31mERROR[m] For more information about the errors and possible solutions, please read the following articles:
[[1;31mERROR[m] [1m[Help 1][m http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException
```

![hashes为空截图](./hashes为空截图.png "hashes为空截图")

### 分析：

这是一个非常典型的 Lombok 与 JDK 21 不兼容 导致的编译错误。

错误信息 Fatal error compiling: Cannot load from object array because "this.hashes" is null 是 Lombok 旧版本在 JDK 21 下运行时的已知 Bug。

### 解决：

确保 feng-cloud3 父工程或 feng-library3-security 中的 Lombok 版本 至少为 1.18.30。强烈建议使用最新的 1.18.42。

![lombok下载截图](./lombok下载截图.png "lombok下载截图")

![替换lombok文件](./替换lombok文件.png "替换lombok文件")

![修改STS的ini文件加载lombok](./修改STS的ini文件加载lombok.png "修改STS的ini文件加载lombok")


在父项目POM文件中：

```
            <lombok.version>1.18.42</lombok.version>

            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </dependency>
```

在子项目中：

```
        <!--Lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
```