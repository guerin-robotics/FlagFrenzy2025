# FlagFrenzy2025 Robot Code Summary

## Overview
This is an FRC (FIRST Robotics Competition) robot codebase for the 2025 Flag Frenzy season. The robot uses WPILib's Command-Based programming framework with Java, implementing a differential drive robot with feeder and shooter subsystems.

**Team Number**: 10021  
**Framework**: WPILib 2025.3.1  
**Language**: Java 17

---

## Robot Architecture

### System Components
The robot consists of four main subsystems:
1. **DriveSubsystem** - Differential drive with two TalonFX/Kraken motors
2. **FeederSubsystem** - Single motor feeder system that feeds balls into the shooter
3. **ShooterSubsystem** - Shooter system with flywheel and firing mechanism, PID velocity control
4. **DistanceSensorSubsystem** - Grapple Robotics LaserCAN distance sensors for alignment

---

## Subsystems

### 1. DriveSubsystem
**Purpose**: Controls the robot's movement using a differential drive configuration.

**Hardware**:
- 2x TalonFX/Kraken motors (CAN IDs: 1 = left, 2 = right)
- Internal encoders on motors for position tracking
- 2-inch diameter wheels
- Gear ratio: 3:1 (motor rotations per wheel rotation)

**Features**:
- Arcade drive control (forward/backward + turning)
- Encoder-based distance tracking in meters
- Speed limiting (70% maximum speed)
- Input validation (NaN/Infinity protection)
- SmartDashboard telemetry (encoder values, motor positions)
- Right motor inverted to match physical orientation

**Key Methods**:
- `setMotors(leftSpeed, rightSpeed)` - Sets motor speeds with safety checks
- `getEncoderMeters()` - Returns average distance traveled in meters
- `resetEncoders()` - Resets encoder positions to zero

---

### 2. FeederSubsystem
**Purpose**: Controls the feeder mechanism that feeds balls into the shooter.

**Hardware**:
- 1x TalonFX motor (CAN ID: 4)
- Velocity control mode

**Features**:
- Velocity-based control (10 RPS forward)
- Feeds balls from intake into shooter
- Runs only when button is pressed (no default command)
- Stops automatically when button is released

**Key Methods**:
- `runAtVelocity()` - Runs feeder at configured velocity (10 RPS)
- `stop()` - Stops the feeder motor

---

### 3. ShooterSubsystem
**Purpose**: Controls the shooter/feeder mechanism with precise velocity control.

**Hardware**:
- 1x TalonFX motor (CAN ID: 3)
- PID + Feedforward velocity control

**Features**:
- PID-controlled velocity targeting (-25 RPS = ~1,500 RPM)
- Configured PID gains: kP=0.05, kI=0.0001, kD=0.0
- Feedforward gains: kS=0.0, kV=0.10
- Default command keeps motor idle (off) when not shooting
- Toggle control (press button to start/stop)

**Key Methods**:
- `runAtTargetVelocity()` - Runs shooter at target velocity using PID
- `shootCommand()` - Returns a command for shooting
- `configureShooterMotor()` - Sets up PID and feedforward gains

---

### 4. DistanceSensorSubsystem
**Purpose**: Manages left and right Grapple Robotics LaserCAN distance sensors for precise positioning.

**Hardware**:
- 2x Grapple Robotics LaserCAN sensors (CAN IDs: 5 = left, 6 = right)
- CAN-based communication
- Range: 0.1m to 4.0m accurately

**Features**:
- Direct distance reading in meters (converts from millimeters)
- Validates sensor readings
- Checks if both sensors are at target distance
- SmartDashboard telemetry (left/right distances, average, alignment status)

**Key Methods**:
- `getLeftDistanceMeters()` - Returns left sensor distance in meters
- `getRightDistanceMeters()` - Returns right sensor distance in meters
- `getAverageDistanceMeters()` - Returns average of both sensors
- `bothSensorsAtTarget()` - Checks if both sensors are within tolerance of target
- `bothSensorsValid()` - Checks if both sensors are reading valid values

---

## Commands

### 1. ArcadeDriveCommand
**Purpose**: Default command for teleoperated driving.

**Behavior**:
- Continuously reads joystick inputs (Y-axis for speed, X-axis for turn)
- Applies 10% deadband to prevent drift
- Applies cubic curve ("star wheel") for smoother, more precise control
- Limits maximum speed to 70%
- Calculates left/right motor speeds for arcade drive
- Runs continuously until interrupted

**Input Processing**:
1. Read raw joystick values
2. Apply deadband (0.1 threshold)
3. Apply cubic curve (preserves sign)
4. Apply speed limit (70%)
5. Calculate differential speeds

---

### 2. DriveForwardCommand
**Purpose**: Autonomous command to drive forward a specified distance.

**Behavior**:
- Resets encoders at start
- Drives forward at constant speed (50%)
- Monitors encoder distance
- Stops when target distance reached
- Includes safety timeout (4 seconds max)

**Safety Features**:
- Input validation (distance must be positive and finite)
- Timeout protection (200 iterations ≈ 4 seconds)
- Automatic motor stop on completion

**Usage**: Used in autonomous mode to drive forward 2 meters.

---

### 3. DriveToTargetDistanceCommand
**Purpose**: Autonomous command to drive forward/backward until both distance sensors reach target distance.

**Behavior**:
- Moves forward/backward slowly (20% speed)
- Monitors both left and right sensor distances
- Stops when both sensors are within tolerance of target
- Includes safety timeout (5 seconds max)

**Usage**: Used in autonomous mode for precise positioning before shooting.

---

### 4. AlignWithDistanceSensorsCommand
**Purpose**: Teleop command with PID control to align robot using distance sensors.

**Behavior**:
- Uses two PID controllers:
  - **Alignment PID**: Turns robot to minimize difference between left/right sensors
  - **Distance PID**: Moves forward/backward to reach target distance
- Runs continuously while button is held
- Combines both corrections simultaneously
- Stops when button is released

**Trigger**: Button 7 (while held)

**PID Tuning**:
- Alignment: kP=0.5 (adjust if too slow/oscillating)
- Distance: kP=0.3 (adjust based on response)
- Max speeds: 40% turn, 30% drive

---

### 5. FeederSetCommand
**Purpose**: Runs the feeder motor when button is held.

**Behavior**:
- Starts feeder at configured velocity when button pressed
- Continues running while button held
- Stops feeder when button released
- Uses velocity control (10 RPS)
- Feeds balls into shooter

**Trigger**: Button 6 (while held)

---

## Control Scheme

### Joystick Configuration
- **Port**: USB Port 0 on roboRIO
- **Speed Axis**: Y-axis (axis 1) - forward/backward
- **Turn Axis**: X-axis (axis 0) - left/right

### Button Mappings
- **Button 5**: Toggle shooter on/off
- **Button 6**: Run feeder (while held)
- **Button 7**: PID alignment with distance sensors (while held)

### Drive Control
- **Default Command**: ArcadeDriveCommand (always active in teleop)
- **Speed Limit**: 70% maximum
- **Deadband**: 10% (prevents drift)
- **Control Curve**: Cubic (smoother small movements)

---

## Autonomous Mode

**Current Implementation**:
- Drives forward 2 meters at 50% speed
- Uses encoder-based distance measurement
- Includes safety timeout

**Future Expansion**:
- Can add feeder commands
- Can add shooter commands
- Can chain multiple commands in sequence

---

## Robot Lifecycle

### Initialization (`robotInit()`)
1. Creates RobotContainer
2. Initializes all subsystems
3. Configures button bindings
4. Sets default commands
5. Validates joystick connection

### Periodic Updates (`robotPeriodic()`)
- Runs CommandScheduler every 20ms
- Executes active commands
- Updates subsystem periodic methods
- Polls button states

### Mode Transitions
- **Disabled**: Cancels all commands, applies motor braking (15% reverse power)
- **Autonomous**: Schedules autonomous command sequence, stops braking
- **Teleop**: Cancels autonomous, enables default drive command, stops braking
- **Test**: Cancels all commands

---

## Safety Features

1. **Input Validation**:
   - NaN/Infinity checks on motor speeds
   - NaN/Infinity checks on joystick values
   - Distance validation in autonomous commands
   - Joystick connection detection

2. **Timeout Protection**:
   - Autonomous commands have maximum execution time
   - Prevents infinite loops if sensors fail

3. **Motor Safety**:
   - Automatic stop when commands end
   - Default commands prevent motors from running uncontrolled
   - Subsystem requirements prevent command conflicts
   - **Disabled braking**: All motors apply 15% reverse power when robot is disabled

4. **Error Handling**:
   - Try-catch in robot initialization
   - Warning messages for connection issues (with spam prevention)
   - Error logging for critical failures
   - Sensor validation with graceful degradation

5. **Disabled Mode Safety**:
   - All commands cancelled when entering disabled mode
   - Motor braking applied to all motors (drive, feeder, shooter)
   - Braking automatically stops when robot re-enables

---

## Constants Configuration

### Drive Constants
- **CAN IDs**: Left=1, Right=2
- **Wheel Diameter**: 2 inches
- **Gear Ratio**: 3:1
- **Max Speed**: 70%
- **Deadband**: 10%
- **Disabled Brake Power**: 15% (applied to all motors when disabled)

### Shooter Constants
- **CAN ID**: 3
- **Target Velocity**: -25 RPS (~1,500 RPM)
- **PID**: P=0.05, I=0.0001, D=0.0
- **Feedforward**: kS=0.0, kV=0.10

### Feeder Constants
- **CAN ID**: 4
- **Velocity**: 10 RPS forward

### Distance Sensor Constants
- **CAN IDs**: Left=5, Right=6
- **Target Distance**: 1.0 meters (adjustable)
- **Tolerance**: 5cm
- **Positioning Speed**: 20%
- **Alignment PID**: kP=0.5, kI=0.0, kD=0.0
- **Distance PID**: kP=0.3, kI=0.0, kD=0.0

### Autonomous Constants
- **Drive Distance**: 2.0 meters
- **Drive Speed**: 50%

---

## Telemetry & Debugging

**SmartDashboard Output**:
- Drive encoder value (meters)
- Left motor position (rotations)
- Right motor position (rotations)
- Left Distance (m) - from LaserCAN sensor
- Right Distance (m) - from LaserCAN sensor
- Average Distance (m) - average of both sensors
- Sensors At Target (boolean) - alignment status
- Sensors Valid (boolean) - sensor health status

**Console Output**:
- Mode transition messages
- Warning messages (joystick, timeouts, sensor errors)
- Error messages (initialization failures)
- Alignment command status messages

---

## Code Structure

```
frc.robot/
├── Robot.java              # Main robot class, lifecycle management
├── RobotContainer.java     # Subsystem/command setup, button bindings
├── Main.java               # Entry point
├── Constants.java          # All robot constants
├── commands/
│   ├── ArcadeDriveCommand.java    # Teleop drive control
│   ├── DriveForwardCommand.java   # Autonomous forward movement
│   └── FeederSetCommand.java      # Feeder control
└── subsystems/
    ├── DriveSubsystem.java        # Drive motors and encoders
    ├── FeederSubsystem.java       # Feeder motor control
    └── ShooterSubsystem.java       # Shooter motor with PID
```

---

## Key Design Patterns

1. **Command-Based Architecture**: All robot actions are commands
2. **Subsystem Abstraction**: Hardware access through subsystems
3. **Supplier Pattern**: Joystick inputs passed as suppliers
4. **Default Commands**: Continuous behaviors (drive, shooter idle)
5. **Requirement Declaration**: Commands declare subsystem requirements
6. **Defensive Coding**: Input validation and error handling throughout

---

## Future Enhancements

Potential improvements:
- Add more autonomous routines
- Implement path following (PathPlanner integration ready)
- Add vision targeting
- Implement shooter angle control
- Add sensor feedback (limit switches, color sensors)
- Expand button mappings for more controls

---

## Summary

This robot code implements a complete FRC robot with:
- ✅ Differential drive with encoder-based navigation (2" wheels)
- ✅ Feeder system with velocity control (feeds balls into shooter)
- ✅ Shooter system with PID velocity control (flywheel and firing)
- ✅ **Grapple Robotics LaserCAN distance sensors** for precise positioning
- ✅ **PID-based alignment system** for teleop (Button 7)
- ✅ **Autonomous positioning** using distance sensors
- ✅ **Disabled motor braking** (15% reverse power on all motors)
- ✅ Teleoperated control with arcade drive
- ✅ Autonomous mode with distance-based movement
- ✅ Comprehensive safety features and error handling
- ✅ Proper WPILib command-based architecture

The code follows WPILib best practices and is ready for competition use. The distance sensor system enables accurate shooting positioning through both autonomous commands and teleop PID alignment. Disabled braking helps the robot stop faster for improved safety.

