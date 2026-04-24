# EventBundler

**A comprehensive custom event system for Minecraft servers**

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Minecraft](https://img.shields.io/badge/minecraft-1.12--1.21+-green)
![Java](https://img.shields.io/badge/java-8+-orange)

---

## Overview

EventBundler provides **110 highly customizable events** across 10 categories, designed to boost server interactivity and automation. With a robust developer API, comprehensive documentation, and practical examples, EventBundler enables developers to create complex systems and seamless plugin integration.

## Key Features

- **110 Custom Events** - Organized into 10 logical categories
- **Developer-Friendly API** - Simple, intuitive event system
- **Full Configurability** - Enable/disable events and categories
- **Event Statistics** - Track event usage and performance
- **Cross-Version Support** - Works on Minecraft 1.12 through 1.21+
- **Zero Dependencies** - Only requires Spigot/Paper API
- **Extensive Documentation** - API docs and usage examples included

## Event Categories

| Category | Events | Description |
|----------|--------|-------------|
| **Transfer** | 10 | Money, items, data transfers |
| **Player** | 15 | Extended player interactions |
| **Block** | 12 | Custom block behaviors |
| **Entity** | 12 | Enhanced entity systems |
| **Combat** | 10 | Advanced combat mechanics |
| **Economy** | 10 | Transaction and shop events |
| **Social** | 10 | Party, friend, guild systems |
| **World** | 10 | Chunk and world manipulation |
| **Admin** | 8 | Moderation and logging |
| **Custom** | 13 | Server-specific events |

## Quick Start

### For Server Owners

1. Download `EventBundler.jar`
2. Place in your `plugins/` folder
3. Restart your server
4. Configure in `plugins/EventBundler/config.yml`

### For Developers

**Add as dependency:**

```xml
<!-- Maven -->
<dependency>
    <groupId>com.eventbundler</groupId>
    <artifactId>EventBundler</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

```gradle
// Gradle
dependencies {
    compileOnly 'com.eventbundler:EventBundler:1.0.0'
}
```

**Use the API:**

```java
import com.eventbundler.EventBundler;
import com.eventbundler.api.EventBundlerAPI;
import com.eventbundler.api.events.categories.transfer.EBTransferEvent;

// Fire an event
EventBundlerAPI api = EventBundler.getApi();
EBTransferEvent event = new EBTransferEvent(sender, receiver, type, data, amount);
api.fireEvent(event);

// Listen to events
@EventHandler
public void onTransfer(EBTransferEvent event) {
    if (event.getAmount() > 1000) {
        event.setCancelled(true);
    }
}
```

## Featured Events

### EBTransferEvent
Generic transfer event supporting multiple transfer types (money, items, data, etc.)

```java
EBTransferEvent event = new EBTransferEvent(
    sender,
    receiver,
    EBTransferEvent.TransferType.MONEY,
    moneyData,
    1000.0
);
api.fireEvent(event);
```

### EBTransferCancelEvent
Fired when a transfer is cancelled, allowing plugins to react appropriately

```java
@EventHandler
public void onTransferCancel(EBTransferCancelEvent event) {
    String reason = event.getReason();
    event.getSender().sendMessage("Transfer cancelled: " + reason);
}
```

### EBPlayerLevelUpEvent
Comprehensive player leveling with configurable rewards

```java
@EventHandler
public void onLevelUp(EBPlayerLevelUpEvent event) {
    int newLevel = event.getNewLevel();
    double reward = event.getReward();
    
    event.getPlayer().sendMessage("Level " + newLevel + "! Reward: $" + reward);
}
```

### EBCombatStartEvent
Flexible combat tagging system

```java
@EventHandler
public void onCombatStart(EBCombatStartEvent event) {
    event.setCombatDuration(15); // 15 seconds
    event.setAllowFlight(false); // Disable flight in combat
}
```

### EBShopPurchaseEvent
Dynamic shop system with discount support

```java
@EventHandler
public void onShopPurchase(EBShopPurchaseEvent event) {
    if (event.getPlayer().hasPermission("shop.vip")) {
        event.setDiscount(0.20); // 20% off for VIPs
    }
}
```

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/eb reload` | `eventbundler.reload` | Reload configuration |
| `/eb list [category]` | `eventbundler.list` | List events |
| `/eb info` | `eventbundler.list` | Show plugin info |
| `/eb toggle <event>` | `eventbundler.toggle` | Toggle event on/off |
| `/eb stats` | `eventbundler.admin` | View statistics |
| `/eb reset` | `eventbundler.admin` | Reset statistics |

## Configuration

Events can be individually configured in `config.yml`:

```yaml
events:
  eb-transfer:
    enabled: true
    cancellable: true
    async: false
    description: "Fired when any transfer occurs"
  
  eb-money-transfer:
    enabled: true
    cancellable: true
    async: false
    description: "Fired when money is transferred"
```

Categories can be toggled to disable multiple events at once:

```yaml
categories:
  transfer:
    enabled: true
    description: "Money, item, and data transfer events"
  
  combat:
    enabled: false  # Disables all combat events
```

## Documentation

- **[API Documentation](API_DOCUMENTATION.md)** - Complete API reference
- **[Usage Examples](USAGE_EXAMPLES.md)** - Practical implementation examples
- **[Config Guide](config.yml)** - Inline configuration documentation

## Use Cases

### Economy Systems
Track all financial transactions, apply taxes, detect suspicious activity

### Custom Leveling
Create RPG-style progression with rewards and milestones

### Combat Tag
Prevent combat logging with flexible tagging system

### Shop Systems
Dynamic pricing with VIP discounts and purchase tracking

### Party Quests
Cooperative gameplay with party-wide rewards

### Anti-Cheat
Detect suspicious patterns across multiple event types

### Minigame Frameworks
Generic event-driven minigame system

### Admin Tools
Enhanced moderation with automatic punishment escalation

### Achievement System
Track player accomplishments with meta-achievements

### Server Automation
Automatic restarts, backups, and maintenance

## Performance

EventBundler is designed for high-performance servers:
- **Minimal overhead** - Events only fire when needed
- **Async support** - Heavy operations can run asynchronously
- **Smart caching** - Configuration cached for fast lookups
- **Memory efficient** - Automatic cleanup of inactive data

## Compatibility

- **Minecraft:** 1.12 - 1.21+
- **Java:** 8+
- **Server Software:** Spigot, Paper, Purpur, and forks
- **Dependencies:** None (standalone)

## Support

- **Issues:** Report bugs via GitHub Issues
- **Documentation:** See `API_DOCUMENTATION.md` and `USAGE_EXAMPLES.md`
- **Examples:** Check `com.eventbundler.examples` package

## License

This project is licensed under the MIT License.

---

**EventBundler** - Empowering developers with 110 events for infinite possibilities.