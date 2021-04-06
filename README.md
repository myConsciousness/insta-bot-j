![Build](https://img.shields.io/badge/Build-Automated-2980b9.svg?style=for-the-badge)
![Latest Version](https://img.shields.io/badge/Latest_Version-v1.0.0-27ae60.svg?style=for-the-badge)
![License](https://img.shields.io/badge/License-Apache_2.0-e74c3c.svg?style=for-the-badge)</br>
![Java CI with Gradle](https://github.com/myConsciousness/insta-bot-j/workflows/Java%20CI%20with%20Gradle/badge.svg)

# 1. InstaBotJ

**Table of Contents**

- [1. InstaBotJ](#1-instabotj)
  - [1.1. What is it?](#11-what-is-it)
  - [1.2. Benefits](#12-benefits)
  - [1.3. How To Use](#13-how-to-use)
    - [1.3.1. Add the dependencies](#131-add-the-dependencies)
  - [1.4. License](#14-license)
  - [1.5. More Information](#15-more-information)

## 1.1. What is it?

Preparing...

## 1.2. Benefits

## 1.3. How To Use

### 1.3.1. Add the dependencies

> **_Note:_**</br>
> Replace version you want to use. Check the latest [Packages](https://github.com/myConsciousness/insta-bot-j/packages).</br>
> Please contact me for a token to download the package.

**_Maven_**

```xml
<dependency>
  <groupId>org.thinkit.bot.instagram.github</groupId>
  <artifactId>insta-bot-j</artifactId>
  <version>v1.0.0</version>
</dependency>

<servers>
  <server>
    <id>github</id>
    <username>myConsciousness</username>
    <password>xxxxxxxxxxxxxxxxxx</password>
  </server>
</servers>
```

**_Gradle_**

```gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/myConsciousness/insta-bot-j")
        credentials {
          username = "myConsciousness"
          password = "xxxxxxxxxxxxxxxxxx"
        }
    }
}

dependencies {
    implementation 'org.thinkit.bot.instagram.github:insta-bot-j:v1.0.0'
}
```

## 1.4. License

```license
Copyright 2021 Kato Shinya.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
or implied. See the License for the specific language governing permissions and limitations under
the License.
```

## 1.5. More Information

`InstaBotJ` was designed and implemented by Kato Shinya, who works as a freelance developer.

Regardless of the means or content of communication, I would love to hear from you if you have any questions or concerns. I do not check my email box very often so a response may be delayed, anyway thank you for your interest!

- [Creator Profile](https://github.com/myConsciousness)
- [Creator Website](https://myconsciousness.github.io/)
- [License](https://github.com/myConsciousness/insta-bot-j/blob/master/LICENSE)
- [Release Note](https://github.com/myConsciousness/insta-bot-j/releases)
- [Package](https://github.com/myConsciousness/insta-bot-j/packages)
- [File a Bug](https://github.com/myConsciousness/insta-bot-j/issues)
- [Reference](https://myconsciousness.github.io/insta-bot-j/)
