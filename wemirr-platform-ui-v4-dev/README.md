## 📚 文档与支持

| 资源          | 链接                                          |
| ------------- | --------------------------------------------- |
| 📖 开发文档   | [docs.battcn.com](https://docs.battcn.com)    |
| 🎯 演示地址   | [cloud.battcn.com](https://cloud.battcn.com/) |
| 💬 技术交流群 | QQ群：789517089                               |
| 📱 微信       | battcn2022                                    |

**演示账号**：

- 平台管理员：租户码 `0000`，账号 `admin`，密码 `123456`
- 租户管理员：租户码 `8888`，账号 `admin`，密码 `123456`

---

## 安装使用

- 获取项目代码

```bash
git clone https://gitee.com/battcn/wemirr-platform-ui.git
```

- 安装依赖

```bash
cd wemirr-platform-ui
# 如果没有 pnpm 请先安装 pnpm
# npm install -g pnpm
pnpm install

```

- 运行

```bash
# 如果您已运行了配套后端, 那么请注释掉
# vite.config.ts  target: "https://cloud.battcn.com/api" 将地址改成 http://localhost:15000
pnpm run dev:antd
```

- 代码提交

```bash
git add .
# 可以参考下面文件,提交格式
# wemirr-platform-ui/internal/lint-configs/commitlint-config/index.mjs
git commit -m '提交内容'
# 如果未过 eslint 可以通过下面命令进行修复
npx eslint --fix
```

- 打包

```bash
pnpm build
```

## 版本管理

**项目代码全部开源,非阉割版,有`Spring Cloud` 工作经验的 无需付费协助一样可以玩转项目**

| 版本 | 描述 |
| --- | --- |
| v3-dev | 基于 Vue3.x 与 Vben5.x 版本进行开发（强烈推荐,因为作者后期重心在它） |
| v2-dev | 基于 Vue3.x 与 Vben2.x 版本进行开发（~~Vben2.x 官方已停止更新~~）配套 WP 后端 v2-dev / v2-master） |
| v1-dev | ~~基于 Vue2.x 与 D2admin 进行开发（已废弃/不建议使用）~~ |
