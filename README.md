# Minecraft Bedrock JSON UI Viewer

An Android app to view and test Minecraft Bedrock JSON UI forms (ActionFormData & ModalFormData) from `server_form.json` files.

## Features

- 📂 **File Picker** - Select `server_form.json` using system file picker
- 🔍 **Form Parsing** - Automatically resolves factories, references, bindings, and modifications
- 📋 **Form List** - View all Action Forms and Modal Forms in tabs
- 👁️ **Form Viewer** - Read-only display of form components with raw JSON viewer
- 🧪 **Test Form Builder** - Create custom forms with title and components
- 📤 **Export** - Export as JSON or ScriptAPI Kotlin code
- 🎨 **Themes** - Light, Dark, and System theme support with Material3

## Supported Form Types

| Form Type | Source | Components |
|-----------|--------|------------|
| **ActionFormData** | `long_form` | Buttons, Labels, Headers, Dividers |
| **ModalFormData** | `custom_form` | Text Fields, Dropdowns, Sliders, Toggles, Labels, Headers, Dividers, Buttons |

## JSON UI Features Supported

- ✅ Factory resolution (`control_ids`)
- ✅ Reference resolution (`@namespace.control_name`)
- ✅ Binding evaluation (`#variables`, `not()`, `and`, `or`, `=`)
- ✅ Modifications (insert_back, insert_front, insert_after, insert_before, replace, remove, move_back, move_front, swap)
- ✅ Dynamic `$variables`
- ✅ Visibility conditions

## Building

### Using GitHub Actions (Recommended)

1. Push to GitHub
2. Go to Actions tab
3. Download `app-debug-apk` artifact

### Local Build

```bash
./gradlew assembleDebug
```

APK will be at `app/build/outputs/apk/debug/app-debug.apk`

## Usage

1. Open app → Tap "Pick server_form.json"
2. Select your `server_form.json` file
3. Browse Action Forms / Modal Forms tabs
4. Tap a form to view details
5. Tap "Test" to open form builder
6. Build custom form → Export as JSON or ScriptAPI

## Architecture

- **MVVM** with Kotlin Coroutines & Flow
- **Jetpack Compose** (Material3) for UI
- **Room** database for caching
- **Kotlinx Serialization** for JSON parsing
- **Koin** for dependency injection
- **Navigation Compose** for navigation

## Project Structure

```
app/src/main/java/org/darkness/jsonuiview/
├── data/
│   ├── model/          # Data models (JSON UI, Forms, Export)
│   ├── parser/         # JSON UI parsing pipeline
│   ├── repository/     # FormRepository
│   └── local/          # Room database
├── ui/
│   ├── main/           # File picker screen
│   ├── formlist/       # Form list with tabs
│   ├── viewer/         # Form viewer + JSON bottom sheet
│   ├── testform/       # Form builder + preview
│   └── components/     # Shared UI components
├── navigation/         # Navigation graph
├── util/               # Utilities
└── di/                 # Koin modules
```

## Parser Pipeline

```
server_form.json 
    → ElementRegistry (index all elements)
    → Factory Finder (main_screen_content.modifications)
    → ReferenceResolver (@namespace.control)
    → ModificationApplier (11 operations)
    → BindingEvaluator (#vars, expressions)
    → FormExtractor (traverse UI tree)
    → ResolvedForm (cached in Room)
```

## License

MIT License - Feel free to use and modify.