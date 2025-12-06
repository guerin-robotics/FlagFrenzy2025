# Flag Frenzy 2025 - FRC Robot Code

This is the robot code for the Flag Frenzy 2025 FRC competition season. The codebase uses WPILib's Command-Based programming framework with Java.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Building and Deploying](#building-and-deploying)
- [Code Reviews](#code-reviews)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Overview

This robot code follows WPILib's Command-Based programming paradigm, providing a clean, maintainable architecture for FRC robot control.

## Prerequisites

- **WPILib 2025.3.1** or later
- **Java 17** (JDK)
- **VS Code** with WPILib extension (recommended)
- **Git** (for version control)

## Setup

1. Clone this repository:
   ```bash
   git clone <repository-url>
   cd FlagFrenzy2025
   ```

2. Open the project in VS Code with the WPILib extension installed

3. Configure your team number in `.wpilib/wpilib_preferences.json`

4. Build the project:
   ```bash
   ./gradlew build
   ```

## Building and Deploying

### Build the Project

```bash
./gradlew build
```

### Deploy to Robot

```bash
./gradlew deploy
```

### Run in Simulation

```bash
./gradlew simulateJava
```

## Code Reviews

### Code Review Process

All code changes should be reviewed before merging. This ensures code quality, proper WPILib patterns, and prevents bugs from reaching production.

### Code Review Prompt

Use the following prompt with your AI coding assistant or code review tool:

```
Review this Java code for a pull request. Check that it follows proper WPILib command-based programming structure, uses correct syntax, and avoids logic/API errors. Verify that commands are scheduled correctly, do not conflict or overrun, and follow lifecycle expectations (initialize, execute, end). Look for opportunities for defensive coding, improved robustness, and cleaner organization. Point out any bugs, anti-patterns, or readability issues, and suggest concise fixes and then act and execute those fixes and update the readme and other documentation.
```

### Review Checklist

When reviewing code, verify:

#### Command-Based Structure ✅
- [ ] Commands properly extend `Command` class
- [ ] Subsystems properly extend `SubsystemBase`
- [ ] Commands declare requirements with `addRequirements()`
- [ ] Default commands are set appropriately
- [ ] `RobotContainer` properly initializes subsystems and commands

#### Command Lifecycle ✅
- [ ] `initialize()` - Sets up command state correctly
- [ ] `execute()` - Runs continuously while command is active
- [ ] `end(boolean interrupted)` - Cleans up when command finishes
- [ ] `isFinished()` - Returns true when command should end
- [ ] Commands properly stop motors/subsystems in `end()`

#### Command Scheduling ✅
- [ ] No conflicting commands requiring same subsystem
- [ ] Commands properly cancelled when needed
- [ ] Default commands don't interfere with scheduled commands
- [ ] Button bindings use appropriate triggers (`whileTrue`, `toggleOnTrue`, etc.)
- [ ] Autonomous commands properly scheduled in `autonomousInit()`
- [ ] Autonomous commands cancelled in `teleopInit()`

#### Defensive Coding ✅
- [ ] Null checks where appropriate
- [ ] Input validation (NaN, Infinity checks for motor speeds)
- [ ] Exception handling for critical operations
- [ ] Safety timeouts for autonomous commands
- [ ] Error reporting using `DriverStation.reportError()`

#### Code Quality ✅
- [ ] Clear, descriptive names
- [ ] Proper JavaDoc comments
- [ ] No magic numbers (use constants)
- [ ] Consistent formatting and style
- [ ] Follows WPILib naming conventions (`m_` prefix for members)

#### API Usage ✅
- [ ] Correct WPILib API calls
- [ ] Proper motor controller usage
- [ ] Correct sensor reading methods
- [ ] Appropriate use of suppliers and lambdas

### Running Code Reviews

#### Before Creating a Pull Request

1. Run the code review prompt on your changes
2. Address all critical and high-priority issues
3. Document any medium/low-priority issues for follow-up
4. Ensure all tests pass (if applicable)

#### During Pull Request Review

1. Reviewers should use the same prompt
2. Focus on command conflicts and lifecycle issues
3. Verify defensive coding practices
4. Check for test coverage
5. Review for proper WPILib patterns

#### Common Issues to Watch For

- **Commands not declaring requirements** - Can cause conflicts
- **Commands that never finish** - Missing `isFinished()` logic
- **Subsystems without default commands** - When continuous control is needed
- **Missing null checks** - On subsystem references
- **Hardcoded values** - Should use constants
- **Missing `end()` methods** - That should stop motors
- **No exception handling** - In critical initialization code
- **Command scheduler not called** - In `robotPeriodic()`

### Code Review Tools

- **AI Coding Assistants**: Use the prompt above with tools like GitHub Copilot, Cursor, or ChatGPT
- **Static Analysis**: Run `./gradlew check` for basic checks
- **Manual Review**: Have team members review using the checklist above
- **Automated Reviews**: See `PR_CODE_REVIEW.md` for example review format

### Review Documentation

- See [`docs/CODE_REVIEW.md`](docs/CODE_REVIEW.md) for comprehensive code review guidelines and findings
- See [`docs/CODE_SUMMARY.md`](docs/CODE_SUMMARY.md) for a complete overview of the robot code

## Project Structure

```
src/main/java/frc/robot/
├── Main.java                 # Entry point
├── Robot.java                # Main robot class
├── RobotContainer.java       # Button bindings and command setup
├── Constants.java            # All robot constants
├── commands/
│   ├── ArcadeDriveCommand.java              # Teleop drive control
│   ├── DriveForwardCommand.java             # Autonomous forward movement
│   ├── DriveToTargetDistanceCommand.java    # Position using distance sensors
│   ├── AlignWithDistanceSensorsCommand.java # PID alignment with sensors
│   └── IntakeSetCommand.java               # Intake control
└── subsystems/
    ├── DriveSubsystem.java          # Drive motors and encoders
    ├── IntakeSubsystem.java        # Intake motor control
    ├── FeederSubsystem.java        # Shooter motor with PID
    └── DistanceSensorSubsystem.java # LaserCAN distance sensors
```

## Robot Features

### Distance Sensor System
- **Grapple Robotics LaserCAN sensors** (left and right)
- **PID-based alignment** - Hold Button 7 to automatically align robot using sensors
- **Autonomous positioning** - Commands to position robot at optimal shooting distance
- **SmartDashboard telemetry** - Real-time distance readings and alignment status

### Control Scheme
- **Button 5**: Toggle shooter on/off
- **Button 6**: Run intake (while held)
- **Button 7**: PID alignment with distance sensors (while held)

## Contributing

When making changes:

1. **Follow WPILib Patterns**:
   - Use command-based architecture
   - Declare subsystem requirements
   - Use constants for configuration values

2. **Code Review**:
   - Use the code review prompt before submitting PRs
   - Address all critical issues
   - Document design decisions

3. **Testing**:
   - Test in simulation before deploying
   - Verify commands work as expected
   - Test edge cases and error conditions

4. **Documentation**:
   - Update JavaDoc for new methods
   - Update README if structure changes
   - Document any non-obvious design decisions

## Documentation

Additional documentation is available in the [`docs/`](docs/) folder:
- [`CODE_SUMMARY.md`](docs/CODE_SUMMARY.md) - Complete overview of robot code and architecture
- [`CODE_REVIEW.md`](docs/CODE_REVIEW.md) - Code review guidelines and findings
- [`SHOOTER_TUNING_GUIDE.md`](docs/SHOOTER_TUNING_GUIDE.md) - Shooter tuning instructions
- [`BUILD_TROUBLESHOOTING.md`](docs/BUILD_TROUBLESHOOTING.md) - Build and deployment troubleshooting

## License

This project uses the WPILib BSD license. See `WPILib-License.md` for details.

## Team Information

- **Team**: 10021
- **Season**: Flag Frenzy 2025
- **Framework**: WPILib 2025.3.1
- **Language**: Java 17

