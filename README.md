# Carte 增加得接口列表
主要针对存储库得作业和转换，按kettle源码，统一叫做repository object，这里检测对象


参数说明
- rep： 存储库名称
- id：  对象ID（通过tree接口获得，类似文件路径）

## 获取树装列表
/kettle/plugin-repository-object/tree
## 上传
/kettle/plugin-repository-object/upload
## 删除 
/kettle/plugin-repository-object/delete
## 获取xml内容
/kettle/plugin-repository-object/content

# 使用方式
- 主要是配置文件和类文件，参考压缩包的结构，放到kettle根目录

- 如果之前安装过老版本，先删除plugins/servlets 目录下得内容

- 然后解压（解压到当前位置），然后会发现plugins目录下多了servlets插件目录


