# Getting Started

This guide walks through setting up your first Hytale plugin project from scratch.

---

## Prerequisites

- **JDK 25** — Hytale Server runs on Java 25
- **IntelliJ IDEA** — recommended IDE (Community or Ultimate)
- **Gradle** — bundled via the Gradle wrapper in the template, no separate install needed

---

## 1. Create a Project from the Template

Clone the [Hytale Example Plugin](https://github.com/Kaupenjoe/Hytale-Example-Plugin) template:

```bash
git clone https://github.com/Kaupenjoe/Hytale-Example-Plugin.git my-plugin
cd my-plugin
```

Or use the **Use this template** button on GitHub to create your own repository.

---

## 2. Open the Project in IntelliJ IDEA

1. Open IntelliJ IDEA and choose **File > Open**, then select the project folder.
2. IntelliJ will detect the Gradle project and prompt you to load it — click **Load Gradle Project**.
3. Wait for indexing and dependency download to finish.

---

## 3. Configure Your Plugin

Edit `gradle.properties` to set your plugin's metadata:

```properties
plugin_group=com.example
plugin_name=MyPlugin
plugin_version=0.1.0
plugin_description=My first Hytale plugin
plugin_author=YourName
plugin_website=https://example.com
plugin_main_entrypoint=com.example.myplugin.MyPlugin
```

---

## 4. Run the Server

Use the Gradle task provided by the `hytale-mod` plugin:

```bash
./gradlew runServer
```

Or run it directly from IntelliJ IDEA via the **Gradle** tool window under **Tasks > hytale > runServer**.

Once the server starts, look for your plugin's log line:

```
[MyPlugin] Hello from com.example:MyPlugin version 0.1.0
```

---

## Next Steps

- **Write Kotlin instead of Java** — see [Converting a Java Plugin to Kotlin](java-to-kotlin.md)
