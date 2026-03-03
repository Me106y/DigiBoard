<div align="center">
<h1>DigiBoard ✨</h1>
<h3>实时端侧智能白板重建系统 | 消除遮挡 · 低延迟 · 高保真</h3>

<p align="center">
  <a href="#-简介">简介</a> •
  <a href="#-核心特性">核心特性</a> •
  <a href="#-技术架构">技术架构</a> •
  <a href="#-快速开始">快速开始</a> •
  <a href="#-性能指标">性能指标</a> •
  <a href="#-引用">引用</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/平台-Android%20%7C%20iOS%20%7C%20Linux-blue" alt="Platform">
  <img src="https://img.shields.io/badge/框架-Paddle%20%7C%20ONNX-orange" alt="Framework">
  <img src="https://img.shields.io/badge/延迟-30fps-brightgreen" alt="Latency">
  <img src="https://img.shields.io/badge/license-Apache%202.0-green" alt="License">
</p>
</div>

---

## 📖 简介

**DigiBoard** 是一个实时、运行在端侧设备的移动系统，旨在消除白板演示中的人物遮挡，并以低延迟和高视觉保真度重建白板内容。无论是远程教学、视频会议还是线下演示，DigiBoard都能让白板内容清晰可见，仿佛演讲者从未遮挡过它。

DigiBoard专为移动端和边缘设备设计，通过轻量级模型与高效算法，在保持高画质的同时实现实时处理。

## ✨ 核心特性

- **🚀 实时端侧推理**：针对移动端深度优化，在主流手机上可达 **30FPS+** 的处理速度。
- **🖼️ 精准语义分割**：集成 **EASPP（Enhanced Atrous Spatial Pyramid Pooling）** 模块的轻量级分割模型，精确提取演示者人像掩模，准确定位被遮挡区域。
- **⏱️ 时序背景重建**：通过高效维护的历史帧缓冲区，检索并填充先前暴露的像素信息，实现背景内容的**无缝修复与时序稳定**，避免闪烁。
- **🌈 自适应图像增强**：内置自动白平衡校正、自适应二值化处理及色彩还原流水线，消除光影不均和环境噪声，输出如同电子文档般清晰的白板内容。


## 🏗️ 技术架构

DigiBoard的工作流程由三大核心模块构成，形成一个高效的实时处理流水线：

<div align="center">
  <img width="2711" alt="DigiBoard演示效果" src="https://github.com/user-attachments/assets/b9d62b60-e314-4ca0-9520-a2d72f306c9e" />
  <p><em>图：DigiBoard端到端处理流程</em></p>
</div>

### 1. 实时语义感知模块
- **输入**：摄像头原始视频帧
- **核心**：轻量级分割网络 + **EASPP模块**（增强特征金字塔）
- **输出**：精确的演示者人像掩模，准确定位被遮挡的白板区域

### 2. 时间序列重建模块
- **核心**：高效历史帧缓冲区 + 像素检索策略
- **机制**：当演示者移动时，系统从历史缓冲区中检索被遮挡区域的“干净”像素信息进行填充
- **优势**：确保背景修复的自然过渡，避免人工痕迹和时序闪烁

### 3. 自适应增强流水线
- **处理步骤**：
  1.  **自动白平衡**：校正不同光源下的色偏
  2.  **自适应二值化**：增强笔迹对比度，消除阴影
  3.  **色彩还原**：恢复白板本色，输出高清画面
- **最终输出**：清晰、无遮挡、视觉友好的白板数字内容

## 🚀 快速开始

### 环境要求
- Python 3.8+
- PyTorch 1.10+ (用于训练/导出)
- Android Studio / Xcode (用于移动端部署)

### 安装与使用

#### 1. 克隆仓库
```bash
git clone https://github.com/your_username/DigiBoard.git
cd DigiBoard

没问题，既然要更准确地对应你的项目现状，我们把平台和框架的信息精简一下，并确保整体风格统一。

以下是修改后的版本，我移除了 iOS、Linux 和 ONNX，并将平台锁定为 Android，框架专注于 Paddle：

---

```markdown
<div align="center">
<h1>DigiBoard ✨</h1>
<h3>实时端侧智能白板重建系统 | 消除遮挡 · 低延迟 · 高保真</h3>

<p align="center">
  <a href="#-简介">简介</a> •
  <a href="#-核心特性">核心特性</a> •
  <a href="#-技术架构">技术架构</a> •
  <a href="#-快速开始">快速开始</a> •
  <a href="#-性能指标">性能指标</a> •
  <a href="#-引用">引用</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/平台-Android-green" alt="Platform">
  <img src="https://img.shields.io/badge/框架-PaddlePaddle-red" alt="Framework">
  <img src="https://img.shields.io/badge/延迟-30fps-brightgreen" alt="Latency">
  <img src="https://img.shields.io/badge/license-Apache%202.0-blue" alt="License">
</p>
</div>

---

## 📖 简介

**DigiBoard** 是一个专门为 Android 端侧设备设计的实时移动系统。它旨在解决白板演示中常见的“人影遮挡”痛点，通过高效的视觉算法，在移动端本地实时消除演示者遮挡，并以高保真度重建白板内容。

该系统在保证极低延迟（~30 FPS）的同时，能输出如同电子文档般清晰的数字化白板画面，非常适合混合式教学、远程会议及线下演示记录。

## ✨ 核心特性

- **🚀 极致端侧性能**：深度适配 Android 移动端硬件，在商用智能手机上实现 **30FPS+** 的流畅处理，无需依赖云端。
- **🖼️ 实时语义感知**：采用集成 **EASPP（Enhanced Atrous Spatial Pyramid Pooling）** 模块的轻量级分割模型，在复杂背景下也能精准提取人像掩模并定位遮挡区。
- **⏱️ 时序背景重建**：利用视频流的时间连续性，通过动态维护的历史帧缓冲区检索并填充先前暴露的像素，实现背景内容的**无缝修复与时序稳定**。
- **🌈 自适应图像增强**：内置自动白平衡校正、自适应二值化处理及色彩还原流水线，彻底消除环境光影不均和噪声，提升文字可读性。



## 🏗️ 技术架构

DigiBoard 的工作流程由三大核心模块构成：

1. **实时语义感知模块**：利用轻量级 Encoder-Decoder 架构配合增强特征金字塔（EASPP），在移动端受限资源下快速生成高质量的人像 Mask。
2. **时间序列重建模块**：系统维护一个高效的历史帧缓冲区。当演示者移动时，算法会自动从缓冲区检索被遮挡区域的“干净”像素进行实时填充，确保画面无闪烁且过渡自然。
3. **自适应增强流水线**：
    - **自动白平衡**：校正复杂光源下的色偏。
    - **自适应二值化**：增强笔迹对比度并剔除阴影。
    - **色彩还原**：恢复白板原本色调，输出高清数字内容。



## 🚀 快速开始

### 环境要求
- Android 10.0+ (API Level 29+)
- Paddle Lite 2.10+
- OpenCV 4.5.3+ (Android SDK)
- 支持 OpenGL ES 3.0+ 的移动设备

### 编译与运行
1. **克隆仓库**：
   ```bash
   git clone [https://github.com/your_username/DigiBoard.git](https://github.com/your_username/DigiBoard.git)
   cd DigiBoard

```
2. **导入项目**：
使用 Android Studio 打开项目文件夹，等待 Gradle 同步完成。
3. **部署**：
连接 Android 设备，点击 **Run** 按钮即可在手机上体验。

## 📊 性能指标

| 指标类型 | 参数/结果 |
| --- | --- |
| **分割模型大小** | 1.97 M |
| **计算开销** | 0.102 G FLOPs |
| **平均延迟** | 33.1 ms (小米 10 测试) |
| **分割精度** | 0.9096 (mIoU) |

---

## 📖 引用

如果您在研究中使用了本项目，请引用我们的论文：

```bibtex
@article{zhang2021digiboard,
  title={DigiBoard: Occlusion-Free Whiteboard Digitization at the Edge},
  author={Wei Zhang, Dalin Zhang, Yu Zhang, Tao Gu},
  journal={JOURNAL OF LATEX CLASS FILES},
  year={2021}
}

```

```

---

**建议步骤：**
1. 检查 `性能指标` 表格里的数据是否需要根据你的最新实验结果微调。
2. 如果你有项目的 Demo 视频或 GIF，记得把 `🏗️ 技术架构` 上方的图片链接替换成真实的。

还需要我帮你写一段具体的 **“如何贡献代码 (Contributing)”** 或者 **“常见问题 (FAQ)”** 吗？

```
