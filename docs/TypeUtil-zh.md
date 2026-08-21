# TypeUtil

`TypeUtil` 提供类型（Type）相关的查询工具：类型继承判断、元数据读取，以及从物品/物品数据中提取类型。类型 ID 匹配均为大小写不敏感。

---

## getType

获取类型对象（大小写不敏感），不存在返回 `null`。

`getType(typeId: String): Type?`

## getMeta

获取类型元数据，类型或键不存在返回 `null`。

`getMeta(typeId: String, key: String): Any?`

## getMetaAsBoolean

获取元数据并宽容解析为 `Boolean`（支持 `true`/`yes`/`1` 等写法），不存在时返回 `false`。

`getMetaAsBoolean(typeId: String, key: String): Boolean`

## getMetaAsString

获取元数据并转为 `String`，不存在时返回 `null`。

`getMetaAsString(typeId: String, key: String): String?`

## getMetaAsInt

获取元数据并转为 `Int`，不存在或非数字时返回 `null`。

`getMetaAsInt(typeId: String, key: String): Int?`

## getMetaAsDouble

获取元数据并转为 `Double`，不存在或非数字时返回 `null`。

`getMetaAsDouble(typeId: String, key: String): Double?`

## getMetaAsList

获取元数据并转为 `List<Any?>`，不存在或非列表时返回 `null`。

`getMetaAsList(typeId: String, key: String): List<Any?>?`

## getAncestors

获取类型的全部祖先（含自身），按从自身到根的顺序返回（第一个元素是自身）。

`getAncestors(typeId: String): List<String>`

## isAssignableFrom

判断 `typeId` 是否继承自（或等于）`potentialAncestorId`。

`isAssignableFrom(typeId: String, potentialAncestorId: String): Boolean`

## getDescendants

获取类型的所有后代（含自身）。注意：此操作需遍历全部类型，性能较差，仅在类型数量较少时使用。

`getDescendants(typeId: String): List<String>`

## getTypeIdFromItemData

从物品数据 Map（`sertraline:type` 键）中提取类型 ID，不存在返回 `null`。

`getTypeIdFromItemData(itemData: Map<String, Any?>): String?`

## getTypeFromItemData

从物品数据 Map 中提取类型对象，不存在返回 `null`。

`getTypeFromItemData(itemData: Map<String, Any?>): Type?`

## getItemTypeId

从物品（`ItemStack` / `ModernSItem` / 物品数据 Map）中获取类型 ID。`ItemStack` 优先读 NBT `sertraline_type`，否则回退到其模板的类型；无法识别时返回 `null`。

`getItemTypeId(item: Any): String?`

## isItemIsType

判断物品的类型是否与指定类型**完全一致**（不含继承，大小写不敏感）。

`isItemIsType(item: Any, typeId: String): Boolean`

## isItemIsExtendType

判断物品的类型是否继承自指定类型（含继承，例如 `sword` 继承 `weapon` 时对 `weapon` 返回 `true`，大小写不敏感）。

`isItemIsExtendType(item: Any, typeId: String): Boolean`
