# 动态表单系统开发约定

## 项目概述
- 主路径: `/Users/jiashenglin/Desktop/好玩的项目/saas`
- PRD: `prd/PRD-动态表单系统.md`
- 后端: `wemirr-platform-4-dev`
- 前端: `wemirr-platform-ui-v4-dev`

## 开发流程

### 第一阶段：功能开发
1. 调用 `/everything-claude-code:orchestrate feature` 规划开发计划
2. 按 PRD 功能模块开发，遵循第12章技术框架规范
3. 前后端同步推进，每完成一个功能点就联调验证

### 第二阶段：质量保障（每个功能完成后）
1. `/everything-claude-code:orchestrate bugfix` - 查找并修复潜在问题
2. `/everything-claude-code:orchestrate refactor` - 代码重构优化
3. `/everything-claude-code:orchestrate security` - 安全审查

### 第三阶段：部署验收（全部功能完成后）
1. 确保 `docker-compose.yml` 配置正确
2. 执行 `docker-compose up -d` 启动所有中间件
3. 启动后端服务，验证所有接口正常
4. 启动前端服务，验证所有页面正常
5. 按照 PRD 第10章"验收标准"逐条验证
6. **开箱即用，可直接部署并马上启用**

## 验收方式
- 分阶段是对工作安排，**只验收最终版本**
- 只有完成第三阶段全部工作后才通知用户验收
- 验收标准：开箱即用，可直接部署并马上启用

## 开发原则
- 小步快跑，每完成一个小功能就联调验证
- 遵循 wemirr-platform 框架规范（见 PRD 第1.1.4节）
- 前后端同步交付，不做半成品

## Orchestrate 命令验证
已确认以下命令均可正常使用：
- `feature` - planner → tdd-guide → code-reviewer → security-reviewer
- `bugfix` - explorer → tdd-guide → code-reviewer
- `refactor` - architect → code-reviewer → tdd-guide
- `security` - security-reviewer → code-reviewer → architect
