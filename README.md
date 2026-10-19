# 资源节点（Resource Node）功能分类文档

[English](#english) | [中文](#中文)

---

# **English**

## Introduction

Add a configurable resource generation node to the game.

## Features

### Item Duplication

- Place an item in the template slot, and the block will automatically duplicate and spawn it at the configured interval
- Adjustable refresh interval (time per duplication) and output count
- Three spawn modes: Drop, Scatter, Center

### FE Energy System

- The block consumes FE energy to duplicate items
- Energy cost grows exponentially: faster speed and higher count result in higher cost per operation
- When energy is insufficient, the countdown text above the block turns red and displays "Insufficient Energy!"
- Can receive FE from other mods' energy systems (e.g. cables, energy pipes)

### Block Overlay Rendering

- The template item rotates and floats above the block
- Countdown text is displayed below, always facing the player
- Text turns red when energy is insufficient
- Rendering can be toggled globally via config

### GUI Interface

- Template item slot on the left
- Adjustable refresh interval and output count on the right
- Energy bar on the left, hover to see detailed energy values
- Shift/Ctrl to accelerate value adjustment

### Item Tooltip

- Hovering over the block item shows refresh interval and output count range
- Shows precise tick adjustment enabled/disabled status
- Time automatically adapts to seconds/minutes/hours units

---

# **中文**

## 简介

为游戏添加一个可配置的资源生成节点

## 功能

### 物品复制

- 将一个物品放入模板槽，方块会自动按设定间隔复制并生成该物品
- 可调整刷新间隔（生成一次的时间）和每次数量
- 支持三种物品生成方式：掉落、随机分散、正中心

### FE 能量系统

- 方块消耗 FE 能量才能复制物品
- 能量消耗采用指数增长：速度越快、数量越多，单次消耗越高
- 能量不足时，方块上方的倒计时文字变为红色提示"能量不足！"
- 可从其他模组的能量系统（如电缆、能量管道）接收 FE

### 方块上方渲染

- 方块上方会旋转显示当前模板物品
- 下方显示倒计时文字，文字始终面朝玩家
- 能量不足时文字变为红色
- 渲染功能可全局开关

### GUI 界面

- 左侧为模板物品槽
- 右侧可调整刷新间隔和每次数量
- 左侧显示能量条，鼠标悬停可查看详细能量数值
- 支持 Shift/Ctrl 加速调整数值

### 物品 Tooltip

- 悬停方块物品时显示刷新间隔和生成数量范围
- 显示精确 Tick 调整的开启/关闭状态
- 时间自动适配秒/分/时单位