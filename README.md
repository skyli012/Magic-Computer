<div align="center">

# MagicComputer

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg?logo=kotlin)](https://kotlinlang.org) [![Android](https://img.shields.io/badge/Android-API%2021%2B-green.svg)](https://developer.android.com) [![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg)](https://developer.android.com/topic/architecture)

</div>
<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="MagicComputer App Icon" width="120" />
</div>

> 一款基于 **2026年春晚魔术** 的智能计算 App，使用 **Kotlin + MVVM** 架构打造，支持个性化输入、步骤引导、结果展示，全流程神奇有趣。

---

## 🪄 项目简介

**MagicComputer** 是我独立设计与开发的一款基于经典魔术的计算应用。  
它实现了从 **输入个人信息 → 引导计算步骤 → 显示神奇结果** 的完整体验，最终所有用户都会得到同一个数字 **2162227**，对应节目播出时刻。

---

## ✨ 核心功能与亮点

### 🎩 魔术计算与个性化体验
- 基于 **2026年春晚邓男子表演** 的手机计算器魔术。
- 支持 **生日输入 / 年龄输入** 等个性化数据。
- 引导用户进行一系列计算步骤，最终显示神奇数字 **2162227**。
- 解释数字含义：对应 **2026年2月16日22点27分**。

### ⚡ 现代化架构与流畅体验
- 基于 **Kotlin + MVVM** 的响应式架构。
- 使用 **协程** 实现异步操作，主线程无阻塞。
- 全局 UI 状态管理：输入 / 计算中 / 结果展示。

### 🧩 系统性优化与兼容性设计
- 全面优化 **输入验证** 与 **错误处理**。
- 自适应布局，完美兼容不同屏幕尺寸与 Android 版本。
- 内置 **重试机制** 与 **用户友好提示**。

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

## 🖼️ 应用界面预览

| 首页 | 计算步骤 | 结果展示 |  
|------|-----------|------------------|
| ![screenshot1](docs/show/home.png) | ![screenshot2](docs/show/steps.png) | ![screenshot3](docs/show/result.png) |

---

> 💡 **MagicComputer** 旨在让数学魔术更贴近生活，让神奇计算触手可及。
