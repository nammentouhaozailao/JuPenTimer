# GitHub Actions 自动构建指南

## 🚀 快速开始（3分钟搞定）

### 第一步：创建 GitHub 账号
1. 打开 https://github.com
2. 点击右上角 **Sign up**
3. 输入邮箱、密码、用户名
4. 验证邮箱

---

### 第二步：创建仓库
1. 登录 GitHub
2. 点击右上角 **+** → **New repository**
3. 填写信息：
   - Repository name: `JuPenTimer`
   - Description: `举盆计时器 - 健身训练APP`
   - ✓ 勾选 **Add a README file**
   - 点击 **Create repository**

---

### 第三步：上传代码

#### 方法A：用 Git 命令行（推荐）

```bash
# 1. 进入项目文件夹
cd C:\Users\Administrator\JuPenTimer

# 2. 初始化 git
git init

# 3. 添加所有文件
git add .

# 4. 提交
git commit -m "Initial commit"

# 5. 连接远程仓库（替换为你的用户名）
git branch -M main
git remote add origin https://github.com/你的用户名/JuPenTimer.git

# 6. 上传代码
git push -u origin main
```

#### 方法B：用 GitHub Desktop（图形界面）
1. 下载 https://desktop.github.com
2. File → Add local repository
3. 选择 `JuPenTimer` 文件夹
4. 填写 Summary: "Initial commit"
5. 点击 Commit to main
6. 点击 Publish repository

#### 方法C：直接网页上传
1. 在 GitHub 仓库页面
2. 点击 **Add file** → **Upload files**
3. 将 `JuPenTimer` 文件夹内所有文件拖入（除了 `.gradle` 和 `build` 文件夹）
4. 点击 **Commit changes**

---

### 第四步：触发构建

上传完成后，GitHub 会自动开始构建！

#### 查看构建状态
1. 打开你的仓库页面
2. 点击顶部 **Actions** 标签
3. 看到 **Build APK** 工作流在运行（黄色圆圈）

#### 等待完成
- 首次构建需要 5-8 分钟（下载依赖）
- 后续构建只需 2-3 分钟
- 成功后会显示绿色 ✓

---

### 第五步：下载 APK

#### 方法1：从 Artifacts 下载
1. 点击完成的构建记录
2. 滚动到页面底部 **Artifacts**
3. 点击 **JuPenTimer-Debug** 下载
4. 解压 zip 文件，得到 `app-debug.apk`

#### 方法2：从 Release 下载（正式版）
1. 点击仓库右侧的 **Releases**
2. 下载最新版本的 APK

---

## 📁 项目结构说明

上传后你的仓库应该是这样：

```
JuPenTimer/
├── .github/
│   └── workflows/
│       ├── build.yml      # 自动构建配置
│       └── release.yml    # 发布版构建
├── app/
│   ├── build.gradle.kts
│   └── src/
├── gradle/
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── README.md
└── .gitignore
```

---

## 🔄 后续更新

### 修改代码后重新构建

```bash
# 1. 修改代码后

# 2. 提交更改
git add .
git commit -m "更新说明：如 添加新功能"

# 3. 推送到 GitHub
git push

# 4. GitHub Actions 会自动触发构建
```

---

## 🏷️ 打标签发布正式版

```bash
# 1. 创建标签
git tag -a v1.0.0 -m "版本 1.0.0"

# 2. 推送标签到 GitHub
git push origin v1.0.0

# 3. GitHub 会自动：
#    - 构建 Release APK
#    - 创建 Release 页面
#    - 上传 APK 到 Release
```

---

## ⚠️ 常见问题

### 问题1：git push 失败，提示 "403 Forbidden"
**解决**：
```bash
# 使用 Personal Access Token
git remote remove origin
git remote add origin https://你的用户名:Token@github.com/你的用户名/JuPenTimer.git
```

### 问题2：Actions 显示红色 ❌
**解决**：
1. 点击进入失败的构建
2. 查看错误日志（通常是依赖问题）
3. 点击右上角的 **Re-run jobs** 重试

### 问题3：找不到下载的 APK
**解决**：
- Debug 版：Actions → 最新构建 → Artifacts
- Release 版：右侧 Releases 标签

### 问题4：构建超时
**解决**：
- 免费版 GitHub Actions 单次限制 6 小时
- 我们的构建只需几分钟，一般不会超时
- 如超时，点击 Re-run 重试

---

## 📱 安装 APK

1. 把 APK 传输到手机（微信/QQ/数据线）
2. 在手机上点击 APK
3. 允许 "安装未知来源应用"
4. 完成安装！

---

## 🎉 恭喜！

你现在拥有：
- ✅ 自动构建的健身计时器 APP
- ✅ 每次更新代码自动编译
- ✅ 版本发布系统
- ✅ 代码备份在云端

开始训练吧！💪
