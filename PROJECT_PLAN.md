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

- [ ] 项目分包结构搭建（ui / data / network / util）
- [ ] 添加依赖：ZXing、Retrofit2、Gson、Glide、Room
- [ ] 封装网络层（Google Books API 接口 + LLM API 接口）
- [ ] Room 数据库定义（Book 表 + Note 表 + DAO）
- [ ] SplashActivity（启动页）
- [ ] MainActivity 骨架（底部导航栏框架）
- [ ] ScanActivity（扫码页面 + 手动输入ISBN兜底）
- [ ] 扫码 → 获取ISBN → 调用API得到书籍信息 → 调试日志打印

**验收**：扫码后能在Logcat看到Google Books返回的书籍信息JSON。

---

## Iteration 2：书籍详情 + 书柜管理（5.11 - 5.14）

- [ ] BookDetailActivity 完整实现（封面、信息展示、加入书柜）
- [ ] 阅读状态选择器（想读/在读/已读）
- [ ] BookshelfActivity（网格列表 + Tab筛选）
- [ ] 书柜搜索（按书名过滤）
- [ ] 长按删除 + 确认对话框
- [ ] Room CRUD 全链路打通

**验收**：扫码 → 查看详情 → 加入书柜 → 书柜中可见 → 可筛选/搜索/删除。

---

## Iteration 3：AI推荐 + 笔记 + 设置（5.15 - 5.17）

- [ ] RecommendActivity（输入偏好 → LLM返回推荐列表）
- [ ] 「根据我的书柜推荐」快捷入口
- [ ] 「AI聊聊这本书」单书讨论功能
- [ ] NoteActivity（笔记编辑 + 保存 + 查看）
- [ ] SettingsActivity（LLM API配置页 + 密文显示）
- [ ] SharedPreferences 封装（API Key、主题等配置存储）

**验收**：配置API Key后，能根据书柜内容获取AI推荐；单书讨论正常返回；笔记可增删改查。

---

## Iteration 4：UI打磨 + 主题切换 + 收尾（5.18 - 5.20）

- [ ] 日间/夜间模式切换（Material3主题）
- [ ] 主页统计卡片真实数据接入
- [ ] 「最近添加」横向列表
- [ ] 全局加载态 + 空态 + 错误态处理
- [ ] 图标、颜色、间距统一（Material Design规范）
- [ ] 代码review、去除调试代码

**验收**：全流程无卡顿、无crash；主题切换即时生效；各状态展示正常。

---

## Iteration 5：CI/CD + 测试 + 打包发布（5.20 - 5.22）

- [ ] 编写GitHub Actions工作流（CI：编译检查；CD：打release APK）
- [ ] 单元测试（关键工具类 + Room DAO）
- [ ] 打包Release APK，创建第一个GitHub Release
- [ ] README.md（项目说明 + 截图 + 构建指南）
- [ ] 最终演示PPT准备

**验收**：push代码自动触发CI检查；创建Release自动生成APK下载链接。

---

## CI/CD 工作流设计

### 触发条件
- **CI**：push 到 main 分支、Pull Request
- **CD**：创建 GitHub Release

### 流程
```
CI:
  checkout → setup JDK 17 → setup Android SDK → gradle build → 跑单元测试

CD:
  checkout → setup JDK 17 → setup Android SDK → gradle assembleRelease → 
  签名APK → 上传到Release Assets
```
- Android签名使用debug keystore（课程演示用），签名文件编码为Base64存于GitHub Secrets
