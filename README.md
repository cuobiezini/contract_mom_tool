# Contract MOM Tool [![JetBrains IntelliJ Platform SDK Docs](https://jb.gg/badges/docs.svg)][docs]
*Reference: [Tool Windows in IntelliJ SDK Docs][docs:tool_windows]*

## Quickstart

This plugin provides a tool window to assist with contract management and MOM (Minutes of Meeting) processes within the IntelliJ IDE.

The tool window allows users to view relevant contract data, search for specific project properties, and manage related tasks directly from the IDE.

### Core Features
- **Data Table**: Displays a list of relevant items (e.g., contracts, services).
- **Fetch Data**: Fetches and displays data in the table.
- **Find app.id**: Searches the project for `app.properties` files to extract key information.

The UI is built using Swing and is designed to be simple and efficient. The main logic is contained within the `DataToolWindowFactory` class.

### Extension Points

| Name                      | Implementation                                        | Extension Point Class |
|---------------------------|-------------------------------------------------------|-----------------------|
| `com.intellij.toolWindow` | [DataToolWindowFactory][file:DataToolWindowFactory]   | `ToolWindowFactory`   |

*Reference: [Plugin Extension Points in IntelliJ SDK Docs][docs:ep]*


[docs]: https://plugins.jetbrains.com/docs/intellij/
[docs:tool_windows]: https://plugins.jetbrains.com/docs/intellij/tool-windows.html
[docs:ep]: https://plugins.jetbrains.com/docs/intellij/plugin-extensions.html

[file:DataToolWindowFactory]: ./src/main/java/org/intellij/sdk/toolWindow/DataToolWindowFactory.java
