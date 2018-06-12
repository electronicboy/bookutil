### BookUtil

Because coming up with cool names is sooooo next year....


# Usage

Basic usage example:
```java
public final class Booktest extends JavaPlugin implements Listener {

    private BookUtil bookUtil;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        bookUtil = new BookUtil(this);
    }


    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.addPage("books, wooo!!");
        book.setItemMeta(bookMeta);

        bookUtil.openBook(event.getPlayer(), book);
    }

}

```

# How to get it?

Maven!

```xml
    <repositories>
        <repository>
            <id>valaria</id>
            <url>https://repo.valaria.pw/repository/maven-public/</url>
        </repository>
    </repositories>
 ```
 
 ```xml
    <dependencies>
        <dependency>
            <groupId>pw.valaria</groupId>
            <artifactId>bookutil</artifactId>
            <version>1.1-SNAPSHOT</version>
        </dependency>
    </dependencies>

```