# BookBuddy 项目迭代计划

## 总览

| 迭代 | 时间 | 目标 |
|------|------|------|
| Iteration 1 | 5.9 - 5.10 | 基础架构 + 网络层 + 扫码功能 |
| Iteration 2 | 5.11 - 5.14 | 书籍详情 + 书柜管理 |
| Iteration 3 | 5.15 - 5.17 | AI推荐 + 笔记 + 设置 |
| Iteration 4 | 5.18 - 5.20 | UI打磨 + 主题切换 + 收尾 |
| Iteration 5 | 5.20 - 5.22 | CI/CD + 测试 + 打包发布 |

---

## Iteration 1：基础架构 + 网络层 + 扫码（5.9 - 5.10）

- [x] 项目分包结构搭建（ui / data / network / util）
- [x] 添加依赖：ZXing、Retrofit2、Gson、Glide、Room
- [x] 封装网络层（Google Books API 接口 + LLM API 接口 + 响应模型类）
- [x] Room 数据库定义（Book 表 + Note 表 + DAO）
- [x] SplashActivity（启动页）
- [x] MainActivity 骨架（底部导航栏框架 + 4个Fragment占位）
- [x] ScanActivity（扫码页面 + 手动输入ISBN兜底）
- [x] 扫码 → 获取ISBN → 调用API得到书籍信息 → 跳转详情页

**验收**：扫码后能获取Google Books返回的书籍信息并跳转详情页。

---

## Iteration 2：书籍详情 + 书柜管理（5.11 - 5.14）

- [x] BookDetailActivity 完整实现（封面、信息展示、加入书柜）
- [x] 阅读状态Chip选择器（想读/在读/已读）
- [x] BookshelfFragment（网格列表 + Tab筛选）
- [x] 书柜搜索（SearchView按书名过滤）
- [x] 长按删除 + AlertDialog确认对话框
- [x] HomeFragment 统计卡片（全部/在读/已读计数）

**验收**：扫码 → 查看详情 → 加入书柜 → 书柜中可见 → 可筛选/搜索/删除。

---

## Iteration 3：AI推荐 + 笔记 + 设置（5.15 - 5.17）

- [x] RecommendFragment（输入偏好 → LLM返回推荐列表 → 解析书名格式 → 展示结果）
- [x] 「根据我的书柜推荐」快捷入口
- [x] 「AI聊聊这本书」单书讨论功能（Dialog + LLM）
- [x] NoteActivity（笔记编辑 + 保存 + 自动加载已有笔记）
- [x] SettingsFragment（LLM API配置页 + 密文显示 + Theme切换）
- [x] PreferencesHelper 封装（API Key、Endpoint、Model、主题模式存储）

**验收**：配置API Key后，能根据书柜内容获取AI推荐；单书讨论正常返回；笔记可增删改查。

---

## Iteration 4：UI打磨 + 主题切换 + 收尾（5.18 - 5.20）

- [x] 日间/夜间模式切换（Material3 DayNight主题，即时生效）
- [x] 主页统计卡片真实数据接入（Room查询）
- [x] 「最近添加」横向RecyclerView列表（最多6本）
- [x] ThemeUtil工具类（所有Activity统一应用主题）
- [x] 代码review、去除调试Log调用

**验收**：全流程无卡顿、无crash；主题切换即时生效且Tab状态保持；各状态展示正常。

---

## Iteration 5：CI/CD + 测试 + 打包发布（5.20 - 5.22）

- [x] 编写GitHub Actions工作流（CI：编译+测试+上传debug APK；CD：Release自动打release APK）
- [x] 单元测试（Book、Note模型类 + LlmResponse JSON解析，共7个测试用例）
- [x] 版本号设为 1.0.0，创建 Git Tag v1.0.0
- [x] 创建 GitHub Release v1.0.0，CD自动构建APK并上传到Release Assets
- [x] README.md（项目介绍 + 功能列表 + 技术栈 + 项目结构 + 构建指南）

**验收**：push代码自动触发CI检查；创建Release自动生成APK下载链接。

---

## CI/CD 工作流设计

### 触发条件
- **CI**：push 到 master 分支、Pull Request
- **CD**：创建 GitHub Release

### 流程
```
CI:
  checkout → setup JDK 17 → setup Android SDK → gradle test → gradle assembleDebug → 上传APK artifact

CD:
  checkout → setup JDK 17 → setup Android SDK → gradle assembleRelease → 上传APK到Release Assets
```
- CD 需要 `permissions.contents: write` 以上传 Release Asset
