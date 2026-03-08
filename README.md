<div align="center">

# MagicComputer

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg?logo=kotlin)](https://kotlinlang.org) [![Android](https://img.shields.io/badge/Android-API%2021%2B-green.svg)](https://developer.android.com) [![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg)](https://developer.android.com/topic/architecture)

</div>
<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="MagicComputer App Icon" width="120" />
</div>

> 一款基于 **2026年春晚魔术** 的智能计算 App，使用 **Kotlin + MVVM** 架构打造，支持个性化输入、步骤引导、结果展示，全流程神奇有趣。

---

## 🎩 完整魔术流程

MagicComputer 模拟了经典的手机计算器魔术流程，让用户体验神奇的数学运算。无论输入什么数字，最终都会显示当前时间！以下是完整步骤：

1. **进入魔术模式**：
   - 长按 **÷** 按钮（进入魔术模式，无任何提示，用户不知情）。

2. **输入第一个数字**：
   - 输入任意数字，例如 `1106`（可以是生日、年龄等）。

3. **按 +**：
   - 点击 **+** 按钮。

4. **输入第二个数字**：
   - 输入另一个任意数字，例如 `88396`。

5. **按 =**：
   - 点击 **=** 按钮。
   - 显示结果 `89502`，历史记录中显示计算过程。

6. **继续操作**：
   - 再次按 **+**（显示 "89502 +"）。

7. **随意输入数字**：
   - 随意输入一些数字，模拟用户乱点，例如 `12345`。

8. **长按 ÷**：
   - 长按 **÷** 按钮（应用自动计算差值并替换输入）。

9. **按 =**：
   - 点击 **=** 按钮。
   - 显示当前时间，例如 `03150821`（格式为 MMDDHHMM，即月日时分）。

**最终效果**：无论前两个数字输入什么，最后都会得到当前的时间数字！这个魔术展示了数学的巧妙之处，让计算器变成“预言机”。

---

## 🧰 技术栈

| 模块 | 技术 |
|------|------|
| **语言** | Kotlin |
| **架构** | MVVM |
| **UI框架** | Android Views + Material Design |
| **异步编程** | Kotlin Coroutines |
| **构建工具** | Gradle |
| **其他** | 模块化架构 / 输入验证 / 状态管理 |

---

