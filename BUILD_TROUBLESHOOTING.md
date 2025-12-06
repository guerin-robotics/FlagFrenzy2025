# Fixing "No Available Builders" Error

## What Does "No Available Builders" Mean?

This error typically appears in VS Code when:
- The Java Language Server can't find your project structure
- Gradle isn't properly recognized by the IDE
- The Java extension isn't configured correctly
- The project needs to be refreshed/reloaded

## Quick Fixes (Try in Order)

### 1. Reload VS Code Window
**Easiest fix - try this first!**

1. Press `Cmd+Shift+P` (Mac) or `Ctrl+Shift+P` (Windows/Linux)
2. Type: `Developer: Reload Window`
3. Press Enter
4. Wait 30-60 seconds for Java Language Server to initialize

### 2. Clean Java Language Server Workspace
**If reload didn't work:**

1. Press `Cmd+Shift+P` / `Ctrl+Shift+P`
2. Type: `Java: Clean Java Language Server Workspace`
3. Select it
4. Click "Restart and delete" when prompted
5. Wait for re-indexing (may take 1-2 minutes)

### 3. Refresh Gradle Project
**If Java server is working but Gradle isn't:**

1. Press `Cmd+Shift+P` / `Ctrl+Shift+P`
2. Type: `Gradle: Refresh Gradle Project`
3. Select it
4. Wait for Gradle sync to complete

### 4. Verify Extensions Are Installed
**Make sure you have the right extensions:**

Required Extensions:
- ✅ **Extension Pack for Java** (includes Java Language Support)
- ✅ **Gradle for Java** (for Gradle support)

To check:
1. Click Extensions icon (left sidebar)
2. Search for "Extension Pack for Java"
3. Search for "Gradle for Java"
4. Install if missing

### 5. Check Java Installation
**Verify Java 17 is available:**

In terminal, run:
```bash
java -version
```

Should show Java 17. If not, you may need to:
- Install Java 17
- Configure VS Code to use Java 17

### 6. Build from Terminal (Verify Project Works)
**Test if the project actually builds:**

```bash
cd /path/to/FlagFrenzy2025
./gradlew build
```

If this works, the project is fine - it's just an IDE issue.

## Advanced Fixes

### Option A: Create VS Code Settings (If Needed)

Create `.vscode/settings.json`:

```json
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.import.gradle.enabled": true,
    "java.import.gradle.wrapper.enabled": true,
    "java.compile.nullAnalysis.mode": "automatic"
}
```

### Option B: Check Java Home

If Java 17 is installed but not found:

1. Find Java 17 path:
   ```bash
   /usr/libexec/java_home -V
   ```

2. Add to VS Code settings:
   ```json
   {
       "java.jdt.ls.java.home": "/path/to/java17"
   }
   ```

### Option C: Restart Gradle Daemon

Sometimes Gradle daemon gets stuck:

```bash
./gradlew --stop
./gradlew build
```

## Verification Steps

After trying fixes, verify:

1. **Check Status Bar** (bottom of VS Code):
   - Should show "Java Language Server" progress
   - Should show "Gradle" when ready

2. **Check Problems Panel**:
   - Press `Cmd+Shift+M` / `Ctrl+Shift+M`
   - Should show compilation errors (not "no builders")

3. **Try Building**:
   - Press `Cmd+Shift+P` / `Ctrl+Shift+P`
   - Type: `Java: Build Workspace`
   - Should compile without "no builders" error

## Common Causes

| Issue | Solution |
|-------|----------|
| First time opening project | Wait for indexing (1-2 min) |
| Java extension not installed | Install Extension Pack for Java |
| Wrong Java version | Install/configure Java 17 |
| Gradle not synced | Run "Gradle: Refresh Gradle Project" |
| Corrupted cache | Clean Java Language Server Workspace |

## Still Not Working?

1. **Check Output Panel**:
   - View → Output
   - Select "Java" or "Gradle" from dropdown
   - Look for error messages

2. **Check Terminal Build**:
   - If `./gradlew build` works, project is fine
   - Focus on IDE configuration

3. **Restart Everything**:
   - Close VS Code completely
   - Restart computer (if needed)
   - Reopen project

## Project-Specific Notes

This is a **WPILib FRC project** which requires:
- Java 17 (not Java 11 or Java 21)
- WPILib extension (if using VS Code)
- Gradle wrapper (already included)

The project should build fine from terminal even if IDE shows "no builders" - this is usually just an IDE configuration issue.



