# FTB Ultimine Plus

把新版 FTB Ultimine（2101.1.x，Minecraft 1.21.1）的功能移植回 **Minecraft 1.18.2 Forge** 的补丁模组。
以 Mixin 方式钩入 FTB Ultimine 1802.3.4 本体，**不修改、不重新打包**官方 jar。

## 功能（对照高版本）

| 功能 | 高版本来源 | 本移植实现 |
|---|---|---|
| 连锁去皮（斧头） | `rightclick/AxeStripping` | `rightclick/RightClickActions` + `mixin/UltimineRightClickMixin` |
| 连锁压路（锹） | `rightclick/ShovelFlattening` | 同上 |
| 大隧道形状（3×3） | `shape/LargeTunnelShape` | `shape/LargeTunnelShape`（注册进 Shape 环） |
| 预览线透明度可配 | 客户端配置 `preview_line_alpha` | `config/FTBUltiminePlusClientConfig` + `mixin/UltimineClientRenderMixin` |

1.18.2 本体已自带连锁耕地（锄头）与连锁收获（作物），无需移植。

## 版本矩阵

- Minecraft 1.18.2 / Forge 40.2.21 / Java 17 / Gradle 7.4.2 / ForgeGradle 5.1.x
- FTB Ultimine 1802.3.4-build.93 / FTB Library 1802.3.6-build.115 / Architectury 4.1.36
- 上游参考：`ftb-ultimine-neoforge-2101.1.15.jar`（反编译存于 `.ref/neoforge2101-src`）

## 构建

```powershell
$env:JAVA_HOME='D:\agent\FTBUltimine\tools\jdk17\jdk-17.0.8+7'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='D:\agent\FTBUltimine\.gradle-home'
$env:JAVA_TOOL_OPTIONS="--patch-module=jdk.zipfs=D:/agent/FTBUltimine/tools/zipfs-patch"
.\gradlew.bat --offline build
```

产物：`build/libs/ftbultimineplus-1.18.2-forge-1.0.1-1.18.2.jar`

## 运行验证

```powershell
.\gradlew.bat --offline runServer   # 需要先接受 eula（run/server/eula.txt 已写 eula=true）
```

启动日志应出现：

- `Mixing UltimineRightClickMixin from mixins.ftbultimineplus.json into dev.ftb.mods.ftbultimine.FTBUltimine`
- `[self-check] OK: large_tunnel registered, shape cycle length=6`

## 配置

- 服务端（每世界）：`world/serverconfig/ftbultimineplus-server.snbt`
  - `features.right_click_axe`（默认 true）：斧头连锁去皮
  - `features.right_click_shovel`（默认 true）：锹连锁压路
- 客户端：`config/ftbultimineplus-client.snbt`
  - `preview_line_alpha`（默认 10，0–255）：预览轮廓半透明线的透明度

## 部署

把 jar 放入 `D:\MC\versions\CABR\mods\`（连同已有的 ftb-ultimine-forge-1802.3.4、ftb-library、architectury）。

## 使用

按住 Ultimine 键（默认 `，` 左括号键）右键：

- 持斧 → 对预览范围内所有方块连锁剥皮（原木去皮、铜块刮蜡/除锈）
- 持锹 → 对预览范围内所有草方块/泥土连锁压成土径
- 滚轮/方向键切换形状（含新增的 Large Tunnel 大隧道）

## 移植要点

- 1.18.2 的 Forge 工具动作 API 是 `net.minecraftforge.common.ToolActions`（高版本叫 `ItemAbilities`）。
- 注入点：`FTBUltimine.blockRightClick` 在 `updateBlocks(...)` 赋值后（局部变量
  `serverPlayer, result, data, shapeContext`），与 1.18.2 官方映射描述符精确匹配，`remap=false`。
- 形状注册：`Shape.register` 为 public static；FTB Ultimine 构造器已执行过 `postinit()`，
  补丁注册后再次调用 `postinit()`（幂等）重建 next/prev 环。
- 客户端配置同样用 FTB Library 1.18.2 SNBT API（`getInt`），未使用高版本 `addXxx` 新 API。