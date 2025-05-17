# Device Performance Classifier

🚀 **Device Performance Classifier** is a comprehensive Kotlin utility designed to classify Android devices into High, Medium, or Low performance tiers based on GPU specifications. This classification is achieved using advanced regular expressions to identify key GPU families and models.

## 🌟 Features

* Accurate GPU detection using OpenGL ES and EGL APIs.
* Classification into High, Medium, or Low performance categories.
* Comprehensive support for Adreno, Mali, PowerVR, Tegra, Vivante, and VideoCore GPU families.
* Efficient regex-based matching for rapid execution.
* Robust error handling for EGL initialization failures.

## 📂 Class Overview

### `DevicePerformanceClassifier`

* A singleton object that provides GPU classification based on GPU renderer string fetched using EGL.

### `DeviceClass` Enum

* `HIGH`: Represents high-end GPUs.
* `MEDIUM`: Represents mid-tier GPUs.
* `LOW`: Represents low-end GPUs.

## 📦 Usage

### **Import the Class**

```kotlin
import com.example.DevicePerformanceClassifier
```

### **Classify GPU:**

```kotlin
val performanceTier = DevicePerformanceClassifier.classifyGpu()
when (performanceTier) {
    3 -> Log.d("DeviceClass", "High-End Device")
    2 -> Log.d("DeviceClass", "Mid-Tier Device")
    1 -> Log.d("DeviceClass", "Low-End Device")
    else -> Log.d("DeviceClass", "Unknown Device")
}
```

## 🛠️ Implementation Details

* GPU detection is performed using EGL and OpenGL ES 2.0 APIs.
* GPU renderer string is obtained using `GLES20.glGetString(GLES20.GL_RENDERER)`.
* Classification logic is driven by regex patterns for each GPU family.

### **High-End GPU Regex Patterns:**

* Adreno: `530-590`, `6xx`, `7xx`, `8xx`
* Mali: `G70+`, `Immortalis`
* PowerVR: `XT`, `9`
* Tegra: `K1+`, `X1+`

### **Mid-Tier GPU Regex Patterns:**

* Adreno: `500-529`, `400-499`
* Mali: `G30-G69`
* PowerVR: `GX`, `GE`

### **Low-End GPU Regex Patterns:**

* Adreno: `300-399`, `400-429`
* Mali: `400`, `450`
* PowerVR: `SGX`
* Vivante: `GC7000+`

## ⚡ Performance Considerations

* The regex-based approach ensures minimal computational overhead.
* EGL and OpenGL ES context handling is optimized to prevent memory leaks.
* Ensure that the method `classifyGpu()` is called in a background thread to avoid blocking the main thread.

## 🧪 Testing

* Extensive testing can be performed using various emulators and real devices to validate the accuracy of GPU classification.

## 🔥 Error Handling

* Handles EGL initialization failures gracefully.
* Logs relevant error information using the Android logging mechanism.

## 📝 License

MIT License. See [LICENSE](LICENSE) for details.

---

For further enhancements or contributions, feel free to open a pull request or issue.
