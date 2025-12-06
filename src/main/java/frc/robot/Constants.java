// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.util.Units;


/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final class DriveConstants {
    /**
     * CAN IDs for drive motors (TalonFX/Kraken).
     * These values must match the CAN IDs configured on the physical motors using Phoenix Tuner.
     * 
     * To configure CAN IDs:
     * 1. Connect motors to CAN bus
     * 2. Open Phoenix Tuner X (or Phoenix Tuner)
     * 3. Select each motor and set its Device ID (CAN ID)
     * 4. Update these constants to match the configured CAN IDs
     */
    public static final int kLeftMotorPort = 1;  // CAN ID for left drive motor
    public static final int kRightMotorPort = 2; // CAN ID for right drive motor

    public static final int[] kLeftEncoderPorts = {0, 1};
    public static final int[] kRightEncoderPorts = {2, 3};
    public static final int kLeftEncoderChannelA = 0;
    public static final int kLeftEncoderChannelB = 1;
    public static final int kRightEncoderChannelA = 2;
    public static final int kRightEncoderChannelB = 3;
    public static final boolean kLeftEncoderReversed = false;
    public static final boolean kRightEncoderReversed = true;

    public static final int kEncoderCPR = 1024;
    public static final double kWheelDiameterMeters = Units.inchesToMeters(2);
    public static final double kEncoderDistancePerPulse =
        // Assumes the encoders are directly mounted on the wheel shafts
        (kWheelDiameterMeters * Math.PI) / kEncoderCPR;
    public static final double kEncoderTick2Meter = kEncoderDistancePerPulse; // Alias for compatibility

    // TalonFX internal encoder constants (for Kraken motors)
    // Gear ratio: motor rotations per wheel rotation
    // Typical FRC drivetrain gear ratio is around 10.71:1 (adjust for your specific robot)
    public static final double kGearRatio = 3; // Motor rotations per wheel rotation
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    // Conversion: TalonFX position (rotations) * gear ratio * wheel circumference = distance in meters
    public static final double kTalonFXRotationsToMeters = kGearRatio * kWheelCircumferenceMeters;

    /**
     * Maximum speed multiplier for the drivetrain (0.0 to 1.0).
     * This limits the maximum speed of the robot.
     * 1.0 = 100% speed (full speed)
     * 0.5 = 50% speed (half speed)
     * 0.75 = 75% speed
     */
    public static final double kMaxDriveSpeedPercent = 0.7; // 70% speed (limited)

    // These are example values only - DO NOT USE THESE FOR YOUR OWN ROBOT!
    // These values MUST be determined either experimentally or theoretically for *your* robot's
    // drive. The SysId tool provides a convenient method for obtaining feedback and feedforward
    // values for your robot.
    public static final double kTurnP = 1;
    public static final double kTurnI = 0;
    public static final double kTurnD = 0;

    public static final double kTurnToleranceDeg = 5;
    public static final double kTurnRateToleranceDegPerS = 10; // degrees per second

    public static final double kMaxTurnRateDegPerS = 100;
    public static final double kMaxTurnAccelerationDegPerSSquared = 300;

    public static final double ksVolts = 1;
    public static final double kvVoltSecondsPerDegree = 0.8;
    public static final double kaVoltSecondsSquaredPerDegree = 0.15;
  }

  public static final class ShooterConstants {
    /** CAN ID for shooter motor */
    public static final int kShooterMotorPort = 3; // CAN ID for shooter motor

    /**
     * Shooter speed as a percentage (-1.0 to 1.0).
     * Negative values run the motor in reverse.
     * 0.10 = 10% forward speed, -0.10 = 10% reverse speed
     * NOTE: This is kept for reference, but shooter now uses velocity control
     */
    public static final double kShooterSpeedPercent = -0.28; // 28% (kept for reference)

    /**
     * Target shooter velocity in rotations per second (RPS).
     * This should match the RPM that 28% power produces.
     * 
     * For SMALLER motors (NEO 550, Falcon 500, small TalonFX):
     * - Typical range: 20-30 RPS (1,200-1,800 RPM at 28%)
     * - Start with 25 RPS, measure actual RPM, adjust accordingly
     * 
     * For LARGER motors (Kraken, large TalonFX):
     * - Typical range: 30-40 RPS (1,800-2,400 RPM at 28%)
     * 
     * Negative value for reverse direction.
     * 
     * TO SPIN FASTER: Increase the absolute value (e.g., -25.0 → -30.0 → -35.0)
     * TO SPIN SLOWER: Decrease the absolute value (e.g., -25.0 → -20.0 → -15.0)
     */
    public static final double kShooterTargetVelocityRPS = -25.0; // Start here for smaller motors (adjust based on testing)
    
    /**
     * PID gains for shooter velocity control.
     * 
     * For SMALLER motors - Conservative starting values:
     * - kP: 0.05 (lower for smoother response, increase if too slow)
     * - kI: 0.0 (add 0.0001-0.001 if steady-state error persists)
     * - kD: 0.0 (add 0.001-0.005 if overshooting)
     * 
     * Tuning guide:
     * - Too slow to reach target? Increase kP (try 0.08-0.10)
     * - Oscillating/overshooting? Decrease kP (try 0.03-0.04) or add kD
     * - Consistent undershoot? Add small kI (0.0001-0.0005)
     */
    public static final double kShooterP = 0.05; // Start conservative for smaller motors
    public static final double kShooterI = 0.0001;  // Add if steady-state error
    public static final double kShooterD = 0.0;   // Add if overshooting
    
    /**
     * Feedforward gains for shooter velocity control.
     * 
     * kS = static friction (volts) - Usually 0 for smaller motors
     * kV = velocity feedforward (volts per RPS) - MOST IMPORTANT VALUE
     * 
     * For SMALLER motors (NEO 550, Falcon 500):
     * - Typical kV range: 0.08-0.12
     * - Start with 0.10, adjust to match 28% power RPM
     * 
     * kV Tuning:
     * - Motor too fast? Lower kV (try 0.08)
     * - Motor too slow? Raise kV (try 0.12)
     * - kV should get you close to target WITHOUT PID
     */
    public static final double kShooterKS = 0.0;  // Static friction (usually 0 for smaller motors)
    public static final double kShooterKV = 0.10; // Start here - most critical value! Tune this first

  }

  public static final class IntakeConstants {
    /** CAN ID for intake motor */
    public static final int kIntakeMotorPort = 4; // CAN ID for intake motor
    public static final double kOpenSpeed = 0; // Speed for opening intake
    public static final double kCloseSpeed = -0.10; // Speed for closing intake
    
    /**
     * Intake velocity in rotations per second.
     * Positive values run the intake forward (intaking), negative values run reverse.
     */
    public static final double kIntakeVelocityRPS = 10.0; // Rotations per second
  }

  public static final class AutoConstants {
    public static final double kTimeoutSeconds = 3;
    public static final double kAutoDriveForwardDistance = 2.0; // Distance in meters for autonomous
    public static final double kDriveSpeed = 0.5; // Speed for autonomous driving
  }

  public static final class DistanceSensorConstants {
    /**
     * CAN IDs for Grapple Robotics LaserCAN distance sensors.
     * 
     * To configure:
     * 1. Connect left LaserCAN sensor to CAN bus
     * 2. Connect right LaserCAN sensor to CAN bus
     * 3. Set CAN IDs using Grapple Robotics configuration tool or Phoenix Tuner
     * 4. Update these constants to match the configured CAN IDs
     */
    public static final int kLeftDistanceSensorCANId = 5;  // CAN ID for left LaserCAN sensor
    public static final int kRightDistanceSensorCANId = 6; // CAN ID for right LaserCAN sensor

    /**
     * Target distance in meters for shooting alignment.
     * The robot will move forward until both sensors read this distance.
     * 
     * Tuning:
     * - Measure the optimal shooting distance
     * - Update this value based on testing
     * - LaserCAN typically measures 0.1m to 4.0m accurately
     */
    public static final double kTargetDistanceMeters = 1.0; // Target distance in meters

    /**
     * Tolerance for distance matching (in meters).
     * Both sensors must be within this tolerance of the target distance.
     * Smaller values = more precise positioning, but may take longer to achieve.
     */
    public static final double kDistanceToleranceMeters = 0.05; // 5cm tolerance

    /**
     * Slow forward speed for precise positioning (0.0 to 1.0).
     * Lower values = slower, more precise movement.
     */
    public static final double kPositioningSpeed = 0.2; // 20% speed for precise positioning

    /**
     * Maximum time to spend positioning (seconds).
     * Safety timeout to prevent infinite positioning attempts.
     */
    public static final double kMaxPositioningTimeSeconds = 5.0;

    /**
     * PID gains for alignment control.
     * The alignment PID controls turning to keep both sensors at the same distance.
     * 
     * Tuning guide:
     * - kP: Start with 0.5, increase if too slow, decrease if oscillating
     * - kI: Add 0.01-0.05 if there's steady-state error
     * - kD: Add 0.01-0.1 if overshooting or oscillating
     */
    public static final double kAlignmentP = 0.5;  // Proportional gain for alignment
    public static final double kAlignmentI = 0.0;  // Integral gain (add if needed)
    public static final double kAlignmentD = 0.0;  // Derivative gain (add if needed)

    /**
     * PID gains for distance control (forward/backward movement).
     * Controls moving forward/backward to reach target distance.
     * 
     * Tuning guide:
     * - kP: Start with 0.3, adjust based on response
     * - kI: Add if steady-state error persists
     * - kD: Add if overshooting
     */
    public static final double kDistanceP = 0.3;  // Proportional gain for distance
    public static final double kDistanceI = 0.0;  // Integral gain (add if needed)
    public static final double kDistanceD = 0.0;  // Derivative gain (add if needed)

    /**
     * Maximum turn speed for alignment (0.0 to 1.0).
     * Limits how fast the robot can turn during alignment.
     */
    public static final double kMaxAlignmentTurnSpeed = 0.4; // 40% max turn speed

    /**
     * Maximum forward/backward speed for distance control (0.0 to 1.0).
     * Limits how fast the robot moves forward/backward during alignment.
     */
    public static final double kMaxAlignmentDriveSpeed = 0.3; // 30% max drive speed

    /**
     * Tolerance for alignment (in meters).
     * Both sensors must be within this difference to be considered aligned.
     */
    public static final double kAlignmentToleranceMeters = 0.02; // 2cm difference tolerance
  }
 
  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
    /**
     * Joystick USB port number on the roboRIO/roboRIO 2.0.
     * 
     * Port numbers correspond to the USB port position in the Driver Station:
     * - Port 0 = First USB port
     * - Port 1 = Second USB port
     * - Port 2 = Third USB port, etc.
     * 
     * To configure:
     * 1. Connect your joystick to a USB port on the roboRIO
     * 2. Open Driver Station software
     * 3. Navigate to the "USB Devices" or "Joysticks" tab
     * 4. Note which port number your joystick is assigned to (starts at 0)
     * 5. Update this constant to match that port number
     */
    public static final int kDriverJoystickPort = 0;
    public static final int kArcadeDriveSpeedAxis = 1; // Y-axis for forward/backward
    public static final int kArcadeDriveTurnAxis = 0; // X-axis for turning
    public static final int kIntakeCloseButtonIdx = 6; // Button index for closing intake
    public static final int kShooterButtonIdx = 5; // Button index for turning on shooter
    public static final int kAlignButtonIdx = 7; // Button index for aligning with distance sensors
    
    /**
     * Deadband value for joystick axes.
     * Values below this threshold will be treated as zero to prevent drift.
     * Typical values: 0.05 - 0.15
     */
    public static final double kJoystickDeadband = 0.1;
  }
}

