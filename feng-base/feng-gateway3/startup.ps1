<#
注意文件编码要设置为 UTF-8 BOM，否则会显示中文乱码
#>

# ========== 可配置变量 ==========
$serviceName = "feng-gateway3"           # 服务名称（用于标题、提示和 JAR 文件名）
$jarPath = "target\$serviceName.jar" # JAR 文件路径
$jvmArgs = @(                        # JVM 参数数组（易修改）
    '-Dfile.encoding=UTF-8',
    '-Xmx256m',
    '-Xms128m'
)
# ================================

# 设置控制台编码为 UTF-8
$OutputEncoding = [console]::InputEncoding = [console]::OutputEncoding = New-Object System.Text.UTF8Encoding

# 设置窗口标题
$Host.UI.RawUI.WindowTitle = $serviceName

# 设置缓冲区大小（宽度200，高度2000）
$Host.UI.RawUI.BufferSize = New-Object Management.Automation.Host.Size(200, 2000)

# 设置窗口大小（宽度200，高度50，可根据屏幕调整）
$Host.UI.RawUI.WindowSize = New-Object Management.Automation.Host.Size(200, 50)

# 设置颜色（黑底绿字）
$Host.UI.RawUI.ForegroundColor = "Green"
$Host.UI.RawUI.BackgroundColor = "Black"
Clear-Host

# 检查 Java 环境
java -version 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "未找到 Java 运行环境，请先安装 JDK 或 JRE。"
    Write-Host "按任意键继续..." -NoNewline
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

# 检查目标 JAR 文件是否存在
if (-not (Test-Path $jarPath)) {
    Write-Host "错误：找不到 $jarPath 文件。"
    Write-Host "按任意键继续..." -NoNewline
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

# 启动应用（使用展开的 JVM 参数）
Write-Host "启动 $serviceName 应用..."
java $jvmArgs -jar $jarPath

# 暂停
Write-Host "按任意键继续..." -NoNewline
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")