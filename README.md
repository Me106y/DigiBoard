# DigiBoard ✨

[中文](README_CN.md) | English

<div align="center">
<h1>DigiBoard ✨</h1>
<h3>Mobile Digital Unobstructed Whiteboard System | Occlusion Removal · Low Latency · High Fidelity</h3>

<p align="center">
  <a href="#-introduction">Introduction</a> •
  <a href="#-key-features">Key Features</a> •
  <a href="#-technical-architecture">Technical Architecture</a> •
  <a href="#-quick-start">Quick Start</a> •
  <a href="#-performance-metrics">Performance Metrics</a> •
  <a href="#-acknowledgments">Acknowledgments</a> •
  <a href="#-citation">Citation</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-blue" alt="Platform">
  <img src="https://img.shields.io/badge/Framework-Paddle%20%7C%20pdmodel-orange" alt="Framework">
  <img src="https://img.shields.io/badge/Latency-30fps-brightgreen" alt="Latency">
  <img src="https://img.shields.io/badge/license-Apache%202.0-green" alt="License">
</p>
</div>

---

## 📖 Introduction

**DigiBoard** is a real-time, on-device mobile system designed to eliminate presenter occlusion in whiteboard demonstrations while reconstructing whiteboard content with low latency and high visual fidelity. Whether for remote education, video conferencing, or in-person presentations, DigiBoard ensures whiteboard content remains clearly visible, as if the presenter never obstructed it.

Specifically engineered for mobile and edge devices, DigiBoard achieves real-time processing through lightweight models and efficient algorithms while maintaining high image quality.

## 🎥 Demo Video
https://github.com/user-attachments/assets/1f4ab039-659b-4eb4-8250-c13f2d39793a

## ✨ Key Features

- **🚀 Real-time On-device Inference**: Deeply optimized for mobile devices, achieving processing speeds of **30FPS+** on mainstream smartphones.
- **🖼️ Precise Semantic Segmentation**: Lightweight segmentation model integrated with **EASPP (Enhanced Atrous Spatial Pyramid Pooling)** module, accurately extracting presenter silhouettes and precisely locating occluded areas.
- **⏱️ Temporal Background Reconstruction**: Efficiently maintains a historical frame buffer to retrieve and fill previously exposed pixel information, achieving **seamless background restoration with temporal stability** and eliminating flickering artifacts.
- **🌈 Adaptive Image Enhancement**: Built-in pipeline featuring automatic white balance correction, adaptive binarization, and color restoration, eliminating uneven lighting and environmental noise to output whiteboard content as clear as digital documents.

## 🏗️ Technical Architecture

DigiBoard's workflow consists of three core modules, forming an efficient real-time processing pipeline:

<div align="center">
  <img width="2711" alt="DigiBoard Demonstration Effect" src="https://github.com/user-attachments/assets/b9d62b60-e314-4ca0-9520-a2d72f306c9e" />
  <p><em>Figure: DigiBoard End-to-End Processing Pipeline</em></p>
</div>

### 1. Portrait Segmentation Module
- **Input**: Raw camera video frames
- **Core**: Lightweight segmentation network + **EASPP module** (Enhanced Feature Pyramid)
- **Output**: Precise presenter silhouette mask, accurately locating occluded whiteboard areas
  <img width="2242" height="1207" alt="Segmentation Output" src="https://github.com/user-attachments/assets/dee27ed8-6595-417d-8e01-5158955abf1b" />

### 2. Content Restoration Module
- **Core**: Efficient historical frame buffer + pixel retrieval strategy
- **Mechanism**: When the presenter moves, the system retrieves "clean" pixel information of occluded areas from the historical buffer for filling
- **Advantages**: Ensures natural transition of background restoration, avoiding artificial artifacts and temporal flickering

### 3. Content Enhancement Module
- **Processing Pipeline**:
  1. **Automatic White Balance**: Corrects color shifts under different lighting conditions
  2. **Adaptive Binarization**: Enhances stroke contrast, eliminates shadows
  3. **Color Restoration**: Restores whiteboard's original appearance, outputs high-definition visuals
- **Final Output**: Clear, occlusion-free, visually-friendly digital whiteboard content

## 🚀 Quick Start

### Requirements

- Android 10.0+ (API Level 29+)
- Paddle Lite 2.10+
- OpenCV 4.5.3+ (Android SDK)
- Mobile devices with OpenGL ES 3.0+ support

### Installation and Usage

#### 1. Clone Repository
```bash
git clone https://github.com/Me106y/DigiBoard.git
cd DigiBoard
```

#### 2. Import Project
Open the project folder with Android Studio and wait for Gradle synchronization to complete.

#### 3. Deployment
Connect your Android device and click the **Run** button to experience DigiBoard on your phone.

## 📊 Performance Metrics

| Metric Type | Parameters/Results |
| --- | --- |
| **Segmentation Model Size** | 1.97 M |
| **Computational Cost** | 0.102 G FLOPs |
| **Average Latency** | 33.1 ms (Tested on Mi 10) |
| **Segmentation Accuracy** | 0.9096 (mIoU) |

## 🙏 Acknowledgments

DigiBoard's development would not have been possible without the wisdom and support of the open-source community. We extend our sincere gratitude to:

Thanks to **PaddleSeg** for providing efficient semantic segmentation tools and rich pre-trained models, which laid a solid foundation for the portrait segmentation module of this project.

Thanks to **CS-23-SW-6-21** for providing valuable insights in architectural design, which provided critical support for the smooth progress of the project.

---

## 📄 License
DigiBoard is released under the Apache 2.0 License. See the [LICENSE](LICENSE) file for more details.
