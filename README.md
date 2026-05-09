# 书伴 BookBuddy — 智能书柜管家

一款基于 Android 原生开发的个人藏书管理应用，通过扫描图书 ISBN 条形码一键录入藏书，结合 Google Books API 自动获取书籍信息，并借助 LLM 大模型提供个性化书单推荐。

## 功能特性

- **ISBN 扫码录入** — ZXing 扫描图书条形码，自动识别 ISBN
- **书籍信息自动获取** — 调用 Google Books API 获取书名、作者、封面、出版社、简介
- **个人书柜管理** — 网格展示、Tab 筛选（想读/在读/已读）、书名搜索、长按删除
- **阅读状态追踪** — 标记每本书的阅读状态
- **AI 智能推荐** — 接入 LLM（OpenAI 兼容 API），根据书柜藏书分析阅读品味，生成个性化书单
- **AI 聊书** — 点击书籍即可让 AI 以文学评论家身份聊聊这本书
- **读书笔记** — 每本书可添加私人笔记，本地 Room 数据库持久化
- **日/夜间主题** — 支持跟随系统、浅色、深色三种模式

## 技术栈

| 层面 | 技术 |
|------|------|
| 语言 | Java 11 |
| 架构 | Activity + Fragment + Room (MVVM-lite) |
| 网络 | Retrofit2 + Gson |
| 数据库 | Room (SQLite) |
| 图片加载 | Glide |
| 扫码 | ZXing (zxing-android-embedded) |
| UI | Material Design 3 + ViewBinding |
| 构建 | Gradle 9.4.1 + AGP 9.2.1 |
| CI/CD | GitHub Actions (自动编译 + Release 打包 APK) |

## 项目结构

```
app/src/main/java/com/example/bookbuddy/
├── data/
│   ├── entity/       Book.java, Note.java
│   ├── dao/          BookDao.java, NoteDao.java
│   └── AppDatabase.java
├── network/
│   ├── model/        BooksResponse, LlmRequest, LlmResponse 等
│   ├── GoogleBooksApi.java
│   ├── LlmApi.java
│   └── RetrofitClient.java
├── ui/
│   ├── SplashActivity.java
│   ├── MainActivity.java
│   ├── ScanActivity.java
│   ├── BookDetailActivity.java
│   ├── NoteActivity.java
│   ├── adapter/      BookAdapter.java
│   └── fragment/     HomeFragment, BookshelfFragment, RecommendFragment, SettingsFragment
└── util/
    ├── PreferencesHelper.java
    └── ThemeUtil.java
```

## 构建与运行

### 环境要求

- Android Studio (Latest Stable)
- JDK 17+
- Android SDK 36+

### 构建步骤

```bash
git clone https://github.com/ceng10086/BookBuddy.git
cd BookBuddy
./gradlew assembleDebug
```

APK 输出路径: `app/build/outputs/apk/debug/app-debug.apk`

### 配置 LLM API

1. 运行应用后进入「设置」页
2. 填入 LLM API Endpoint（OpenAI 兼容格式）
3. 填入 API Key
4. 选择模型名称（如 `gpt-4o`、`deepseek-chat` 等）
5. 保存即可使用 AI 推荐和 AI 聊书功能

## API 说明

| API | 用途 | 是否需要 Key |
|-----|------|-------------|
| Google Books API | 通过 ISBN 获取书籍信息 | 否（有频率限制） |
| LLM API (OpenAI 兼容) | AI 书单推荐 + 聊书 | 是（用户自行配置） |

## License

MIT (课程项目)
