# Item Manager System

Sertraline's item manager provides four storage modes for items: **Public** / **Private** × **Persistent** / **Temporary**. The manager handles item creation, retrieval, deletion, and lifecycle management across reloads.

---

## Architecture

```
ItemManager
├── PublicManager          (visible to all players)
│   ├── PERSISTENT         (survives reload, in itemMap)
│   └── TEMPORARY          (survives reload via serialization buffer)
│
└── PrivateManager         (per-UUID, database-backed)
    ├── PERSISTENT         (stored in database)
    └── TEMPORARY          (in-memory only)
```

### Storage Types

| Type | `SubManagerType` | Scope | Persistence |
|------|-----------------|-------|-------------|
| Public Persistent | `PERSISTENT` | All players | itemMap (memory), cleared & reloaded from workspace |
| Public Temporary | `TEMPORARY` | All players | Serialized to buffer during reload, restored after |
| Private Persistent | `PERSISTENT` | Per UUID | Database |
| Private Temporary | `TEMPORARY` | Per UUID | In-memory, lost on shutdown |

### Enums

```kotlin
enum class ManagerType(val alias: Set<String>) {
    PUBLIC(setOf("public")),
    PRIVATE(setOf("private"));
}

enum class SubManagerType(val alias: Set<String>) {
    PERSISTENT(setOf("persistent", "pers", "p")),
    TEMPORARY(setOf("temporary", "temp", "t"));
}
```

---

## Configuration (`manager.yml`)

```yaml
# Default manager type and sub type
defaults:
  scope: "public"          # public / private
  sub: "persistent"        # persistent / temporary

# Allow deleting public-persistent items
allow-delete-public-persistent: false

# Also remove the file when deleting public-persistent items
delete-file: false

# Private manager settings
private:
  # Auto UUID fallback when no player context available
  auto-uuid: ""
```

---

## Public Items

Public items are shared across all players. They are the standard item type loaded from `workspace/`.

### Behavior

| Operation | PERSISTENT | TEMPORARY |
|-----------|-----------|-----------|
| Create | Not supported via manager | `createTemporary(id, item)` |
| Read | `getItem(id)` → itemMap | `getItem(id)` → temporary → itemMap fallback |
| Delete | Remove from itemMap (config-gated) | Remove from temporary + itemMap |
| Reload | Cleared, reloaded from workspace | Serialized → buffer → restore |

### Creation Restriction

Creating public-persistent items through the manager is blocked (`UnsupportedOperationException`). These items are loaded from YAML workspace files only. For runtime registration, use `SertralineAPI.registerItem(id, item)` or the public-temporary path.

---

## Private Items

Private items are scoped to a UUID (player or custom namespace). They support database persistence.

### Behavior

| Operation | PERSISTENT | TEMPORARY |
|-----------|-----------|-----------|
| Create | Cache + database insert | In-memory only |
| Read | Cache → database fallback | In-memory |
| Delete | Cache + database delete | In-memory only |
| Reload | Cache cleared, lazy-load from DB | Cleared |
| Shutdown | Cleared | Cleared |

### UUID Resolution

When no UUID is provided, the manager resolves one in this order:
1. Explicit UUID parameter
2. Player UUID context
3. `manager.yml` `private.auto-uuid` setting
4. Throws `IllegalStateException` if none available

---

## API (via `SertralineAPI`)

```kotlin
val api = Sertraline.api()
```

### Public Item Operations

```kotlin
// Create a public-temporary item
api.createPublicItem("my_item", data, SubManagerType.TEMPORARY)

// Read
val item = api.getPublicItem("my_item")              // PERSISTENT (default)
val tempItem = api.getPublicItem("my_item", SubManagerType.TEMPORARY)

// Delete
api.deletePublicItem("my_item", SubManagerType.TEMPORARY)

// List
val allPersistent = api.getAllPublicItems(SubManagerType.PERSISTENT)
val allTemporary = api.getAllPublicItems(SubManagerType.TEMPORARY)
```

### Private Item Operations

```kotlin
// Create
api.createPrivateItem("uuid-here", "my_private_item", data, SubManagerType.TEMPORARY)
api.createPrivateItem("uuid-here", "my_db_item", data, SubManagerType.PERSISTENT)

// Read
val item = api.getPrivateItem("uuid-here", "my_private_item")

// Delete
api.deletePrivateItem("uuid-here", "my_private_item", SubManagerType.TEMPORARY)

// List
val all = api.getAllPrivateItems("uuid-here", SubManagerType.PERSISTENT)
```

### General Operations

```kotlin
// Register/remove items directly in itemMap (public-persistent)
val item = ModernSItem(key = "custom_item", data = linkedMapOf(), config = linkedMapOf())
api.registerItem("custom_item", item)

val removed = api.unregisterItem("custom_item")

// Count
val total: Int = api.getItemCount()

// UUID resolution
val uuid = api.resolvePrivateUuid(null, player.uniqueId.toString())
```

### Internal Manager Access

```kotlin
// Direct access to the underlying managers
val publicMgr = Sertraline.manager.public
val privateMgr = Sertraline.manager.privateManager
```

---

## Commands

### `/sertraline manager` (`/mgr`)

Manage items across public/private, persistent/temporary scopes.

| Subcommand | Description |
|-----------|-------------|
| `help` | Show command usage |
| `use <public\|private> [persistent\|temporary]` | Set manager scope |
| `switch <uuid>` | Switch private manager UUID |
| `create <id> <json>` | Create item in current scope (JSON body) |
| `clone <templateId> <newId>` | Clone an existing item |
| `delete <id>` | Delete an item by ID |
| `list` | List items in current scope |
| `info <id>` | Show item details |

**Examples:**

```
# Use public-temporary scope
/manager use public temp

# Use private-persistent scope
/manager use private persistent

# Switch to a specific UUID for private operations
/manager switch 550e8400-e29b-41d4-a716-446655440000

# Create a public-temporary item with JSON body
/manager create my_item {"xbuilder":{"material":"stone","name":"&aMy Item"}}

# Clone an existing item
/manager clone sword_of_skyline my_cloned_sword

# List items in current scope
/manager list

# Show item details
/manager info my_item

# Delete an item
/manager delete my_item
```

### `/sertraline item give`

Give Sertraline items to players.

```
/sertraline item give [player] [amount] [silent]
```

**Examples:**
```
# Give yourself 1 sword_of_skyline
/sertraline item give sword_of_skyline

# Give a player 5 of the item silently
/sertraline item give sword_of_skyline 5 true
```

---

## Kether Integration

### Kether Variables in Items

Item variables (`vars` section) support inline Kether expressions using the `${kether:...}$` syntax:

```yaml
my_item:
  xbuilder:
    name: "<!i>&7Power Level: ${kether: check get power_level}$"
    lore:
      - "<!i><gray>Current: <yellow>${var:power_level}$"
  sertraline:
    vars:
      power_level: 1
```

### Conditional Actions with Kether

```yaml
my_item:
  sertraline:
    vars:
      uses: 0
    actions:
      onRightClick:
        - condition: check var uses < 10
          js: |
            var tag = ItemStackUtil.getItemTag(bItem);
            tag.putDeep("sertraline_data.uses", uses + 1);
            ItemStackUtil.setItemTagDirect(bItem, tag);
            player.sendMessage("Used! Remaining: " + (10 - uses - 1));
          kether: |
            tell inline "&aYou used the item!"
```

### Manager Access via API in Kether

The `SertralineAPI` is exposed to script runtimes (JavaScript, GraalJS, Kether via `SertralineObj`). In JavaScript/GraalJS:

```javascript
// Access the API
var api = SertralineAPI;

// Get an item by ID
var item = api.getItem("sword_of_skyline");

// Build an item for a player
var built = api.buildItem("sword_of_skyline", player, null, 1, null);

// Get the Sertraline ID from an ItemStack
var id = api.getId(bItem);

// Check if registered
var exists = api.isRegisteredItem("my_item");

// Rebuild lore
api.rebuildLore(bItem, player);
```

### Kether Action Scripts

Item actions support Kether scripts directly:

```yaml
my_item:
  sertraline:
    actions:
      onRightClick:
        - kether: |
            tell inline "&aYou right-clicked &e{var name}&a!"
            give 1 diamond
      onLeftClick:
        - kether: |
            tell inline "&7Left click!"
```

### Using `SertralineObj` in Scripts

The main `Sertraline` object is available as `SertralineObj` in JavaScript/GraalJS contexts:

```javascript
// Get the console sender
var console = SertralineObj.consoleSender;

// Access itemMap directly
var itemMap = SertralineObj.itemMap;

// Trigger reload
// (Note: reload is async, use with caution)
```

---

```
reloadCustomConfig()
├── manager.preReload()
│   └── PublicManager: serialize temporary items → buffer
│
├── Clear all caches (itemMap, etc.)
├── Reload workspace items from YAML
│
├── manager.postReload()
│   └── PublicManager: deserialize buffer → restore temporary items
│
└── Reload recipes, scripts, etc.
```

During reload:
- **Public-Persistent**: wiped and reloaded from workspace YAML files
- **Public-Temporary**: serialized to string buffer before clear, deserialized after reload
- **Private-Persistent**: cache cleared, lazy-loaded from database on next access
- **Private-Temporary**: cache cleared, lost if not saved externally
