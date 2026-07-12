# Template System

Sertraline's template system allows reusable YAML fragments with parameter substitution, value transformation (`$t`), and type casting (`$c`). Templates are loaded from the `templates/` folder and can be referenced from any config file using the `$template` directive.

---

## Defining Templates

Place YAML files in the `templates/` directory. Each top-level key is a template name.

```yaml
# templates/example.yml
my_weapon_template:
  xbuilder:
    material: "%{material?:IRON_SWORD}"
    name: "%{name?:Unnamed Weapon}"
    lore:
      - "%{lore_line?:}"
    unbreakable: true
    attributes:
      ATTACK_DAMAGE:
        amount: "%{damage?:10}"
        operation: ADD_NUMBER
        slot: HAND
      ATTACK_SPEED:
        amount: "%{speed?:1.6}"
        operation: ADD_NUMBER
        slot: HAND
```

Templates can reference other templates (chaining):

```yaml
# templates/tiers.yml
iron_melee:
  $template:
    use: my_weapon_template
    arguments:
      material: IRON_SWORD
      damage: 8
      speed: 1.6
      name: Iron Sword
```

---

## Using Templates

In any config file (items, recipes, etc.), use the `$template` directive:

```yaml
my_custom_sword:
  $template:
    use: iron_melee
    arguments:
      name: My Custom Sword
  sertraline:
    tier: C
```

Fields outside `$template` are merged into the resolved result, overriding template defaults.

### Auto `_ID` Parameter

Every entry automatically receives `_ID` set to its root key:

```yaml
flame_sword:
  $template:
    use: my_weapon_template
    arguments:
      material: NETHERITE_SWORD
      name: "Flame Sword of %{_ID}"
# _ID = "flame_sword" → name becomes "Flame Sword of flame_sword"
```

---

## Parameter Substitution

### Syntax

| Pattern | Description |
|---------|-------------|
| `%{param}` | Replaces with argument `param`. If missing, kept as-is. |
| `%{param?:default}` | Replaces with argument `param`, or `default` if missing. |

### Default Values

```yaml
name: "%{name?:Unnamed Weapon}"     # default: "Unnamed Weapon"
color: "%{color?:WHITE}"            # default: "WHITE"
lore: "%{lore?:}"                   # default: empty string
```

### Escape Sequences

| Escape | Output |
|--------|--------|
| `\{` | Literal `{` |
| `\}` | Literal `}` |
| `\?:` | Literal `?:` |

---

## Transformers (`$t`)

Transformers process argument values before substitution. Place them in `$template.arguments` under any key:

```yaml
arguments:
  name:
    $t:
      type: upper
      value: "sword"
```

### Short Form
```yaml
$t: upper
```
When the value is a string (not a map), it is used as the type directly.

### `$t: when`

Maps a source value through a lookup table:

```yaml
$t:
  type: when
  source: "%{_ID}"
  when:
    wild_guardian: "&2Wild Guardian"
    forest_guardian: "&aForest Guardian"
  fallback: "Unknown"
```

### `$t: upper` / `$t: lower`

Case conversion:

```yaml
$t:
  type: upper
  value: "%{name}"
  locale: en             # optional locale

$t:
  type: lower
  value: "HELLO"
```

Aliases: `uppercase`, `to_upper_case` / `lowercase`, `to_lower_case`

### `$t: condition`

Boolean conditional selection:

```yaml
$t:
  type: condition
  condition: "%{enchanted?:false}"   # true/false/yes/no/1/0
  true: "&dEnchanted Sword"
  false: "&5Normal Sword"
```

### `$t: self_increase`

Auto-incrementing counter evaluated on each resolution:

```yaml
$t:
  type: self_increase
  from: 1
  to: 999
  step: 1
  step_interval: 1
```
- `step_interval`: number of calls between increments (e.g., 2 means 0, 0, 1, 1, ...)

### `$t: kether`

Evaluate a Kether expression. The `shell` value supports `%{...}` parameter substitution before evaluation.

```yaml
$t:
  type: kether
  shell: |
    random 1 to 10
  sandbox: true
  fallback: 1
  nullfallback: 2
  unitfallback: 3
```

**Using parameters inside Kether:**

```yaml
arguments:
  min: 5
  max:
    $t:
      type: kether
      shell: |
        math add %{min} 10
      fallback: 15
  bonus_name:
    $t:
      type: kether
      shell: |
        if check "%{_ID}" == "super_sword" then "Legendary" else "Normal"
```

The Kether expression runs via `KetherShell.eval` with the console sender as context. Use `sandbox: false` to throw exceptions instead of falling back.

**Fallback priority:** `nullfallback` > `unitfallback` > `fallback` — matched against the Kether return type (null, Unit, or exception).

### `$t: js` / `$t: graaljs` / `$t: fluxon` / `$t: jexl`

Evaluate script expressions with the same config shape:

```yaml
$t:
  type: js
  shell: Math.max(5, 10)
  fallback: 0

$t:
  type: jexl
  shell: |
    Math.min(%{min}, %{max})
  fallback: 0
```

---

## Type Casts (`$c`)

Type converters cast string values to specific types with optional rounding:

```yaml
arguments:
  damage:
    $c:
      type: int
      round: ceil
```

### Short Form
```yaml
$c: double
```

### Types

| Type | Options | Description |
|------|---------|-------------|
| `int` | `round`: `ceil` / `cell`, `round`, `floor` | Integer conversion with rounding |
| `double` | — | Double conversion |
| `string` | `case`: `uppercase` / `upper`, `lowercase` / `lower` | String with optional case conversion |

### Case Conversion (any type)

```yaml
$c:
  type: string
  case: uppercase
  locale: en
```

---

## Combined `$t` + `$c`

Transform first, then cast:

```yaml
arguments:
  damage:
    $t:
      type: when
      source: "%{_ID}"
      when:
        shadow_blade: "15.7"
      fallback: "10"
    $c:
      type: int
      round: ceil
  name:
    $t:
      type: upper
      value: "shadow blade"
    $c:
      type: string
      case: upper
```

---

## Argument Resolution Priority

When templates chain (A → B → C), outer arguments take priority over inner defaults:

1. `_ID` is always injected from the root entry key
2. Arguments from the outermost `$template.arguments` are applied first
3. Inner template arguments only apply for keys not already provided by outer layers

---

## API (Programmatic Access)

### Template Access (via `SertralineAPI`)

```kotlin
val api = Sertraline.api()

// Query
val template: Map<String, Any?>? = api.getTemplate("my_weapon_template")
val names: Set<String> = api.getTemplateNames()
val all: Map<String, Map<String, Any?>> = api.getAllTemplates()
val count: Int = api.getTemplateCount()

// Resolve
val resolved = api.resolveTemplate("my_weapon_template", mapOf("name" to "Test"))
```

### Custom Processor Registration

Register custom `$t` transformer types:

```kotlin
api.registerTransformer("my_type") { type, config, resolveCtx ->
    // type: "my_type"
    // config: the full $t map
    // resolveCtx: current args including _ID, _ORIGIN
    "transformed value"
}

api.unregisterTransformer("my_type")
```

Register custom `$c` converter types:

```kotlin
api.registerConverter("my_format") { type, config, value, resolveCtx ->
    // value: the input string to convert
    "converted value"
}

api.unregisterConverter("my_format")
```

Register custom argument directives (same level as `$t`/`$c`):

```kotlin
api.registerDirective("\$myDirective") { name, config, key, currentArgs, dynamicArgs, resolveCtx ->
    // Process like $t/$c but with full control over currentArgs/dynamicArgs
}

api.unregisterDirective("\$myDirective")
```

The functional interfaces (defined in `TemplateManager`):

```kotlin
fun interface TransformerProvider {
    fun process(type: String, config: Map<String, Any?>, resolveCtx: Map<String, String>): String
}

fun interface ConverterProvider {
    fun process(type: String, config: Map<String, Any?>, value: String, resolveCtx: Map<String, String>): String
}

fun interface DirectiveProvider {
    fun process(name: String, config: Map<String, Any?>, key: String, currentArgs: MutableMap<String, String>, dynamicArgs: MutableMap<String, () -> String>, resolveCtx: Map<String, String>)
}
```

---

## Kether Integration

### Template Arguments in Kether Actions

Template-resolved values flow into item actions. Any `%{...}` in action scripts is substituted before execution.

```yaml
my_sword:
  $template:
    use: basic_melee_weapon
    arguments:
      damage: 20
      material: DIAMOND_SWORD
      name: "&bSword of %{_ID}"
      power: 50
  sertraline:
    tier: S
    actions:
      onRightClick:
        - kether: |
            tell inline "&e%{name} &7deals &c%{damage} &7damage!"
            tell inline "&7Power level: &a%{power}"
            if check %{power} > 30 then give %{material} 1
```

### Using `%{...}` in All Script Types

```yaml
actions:
  onConsume:
    - kether: |
        tell inline "&aYou consumed %{name} (+%{heal?:5} HP)"
    - js: |
        player.sendMessage("Healed by " + "%{heal?:5}");
    - jexl: |
        player.sendMessage("Heal value: " + Math.max(%{heal?:5}, 0))
```

### Template in Kether `$t` Chains

You can combine `$t: kether` with other transformers:

```yaml
arguments:
  enchant_power:
    $t:
      type: when
      source: "%{_ID}"
      when:
        flame_sword: "5"
        ice_sword: "3"
      fallback: "1"
    $c:
      type: int
  damage:
    $t:
      type: kether
      shell: |
        math multiply %{enchant_power} 4
      fallback: 10
      sandbox: true
```

---

## Commands

### `/sertraline reload`

Reload all configs including templates:

```
/sertraline reload
```

Templates are always loaded first during reload so they're available for all other config types (items, recipes, crafting stations).

### Template Verification

Use `/sertraline debug getItem <id>` to inspect a resolved item and verify template substitution worked correctly:

```
/sertraline debug getItem my_custom_sword
```

Templates are loaded from `plugins/Sertraline/templates/` during reload. The folder is auto-created if missing. Template loading happens **before** all other configs, so templates are available for items, recipes, etc.

```
plugins/Sertraline/
├── templates/
│   ├── basic_gear.yml
│   └── material_tiers.yml
├── workspace/
├── recipes/
└── crafting-stations/
```
