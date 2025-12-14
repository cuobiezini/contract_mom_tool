// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType.IntellijIdea
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType.IntellijIdeaCommunity

plugins {
  id("java")
  id("org.jetbrains.intellij.platform") version "2.9.0"
}

group = "org.intellij.sdk.contract_mom_tool"
version = "1.0.0"

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    // 使用本地的 IDE 路径来避免下载
    // 确保这个路径是 IDE 的根目录，且真实存在
    // 例如: "D:/IDEs/idea-IC-243.25045.118/idea-IC-243.25045.118"
    // 请替换为你在文件资源管理器中验证过的真实路径
    local("D:/JetBrains/IntelliJ IDEA Community Edition 2025.2.4")
  }
  implementation("com.google.code.gson:gson:2.10.1")
}

intellijPlatform {
  buildSearchableOptions = false

  pluginConfiguration {
    ideaVersion {
      sinceBuild = "243"
    }
  }
  pluginVerification  {
    ides {
      // since 253, IntelliJ IDEA Community and Ultimate have been merged into IntelliJ IDEA
      select {
        types = listOf(IntellijIdeaCommunity)
        untilBuild = "252.*"
      }
      select {
        types = listOf(IntellijIdea)
        sinceBuild = "253"
      }
    }
  }
}
