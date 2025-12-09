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

    public static final double kWheelDiameterMeters = Units.inchesToMeters(2);

    // TalonFX internal encoder constants (for Kraken motors)
    // Gear ratio: motor rotations per wheel rotation
    // Typical FRC drivetrain gear ratio is around 10.71:1 (adjust for your specific robot)
    public static final double kGearRatio = 3; // Motor rotations per wheel rotation
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    // Conversion: TalonFX position (rotations) * gear ratio * wheel circumference = distance in meters
    public static final double kTalonFXRotationsToMeters = kGearRatio * kWheelCircumferenceMeters;

    /**
     * Track width (distance between left and right wheels) in meters.
     * Used for calculating rotation from encoder differences.
     * Measure the distance between the centers of the left and right wheels.
     */
    public static final double kTrackWidthMeters = Units.inchesToMeters(24); // Adjust based on your robot's actual track width

    /**
     * Maximum speed multiplier for the drivetrain (0.0 to 1.0).
     * This limits the maximum speed of the robot.
     * 1.0 = 100% speed (full speed)
     * 0.5 = 50% speed (half speed)
     * 0.75 = 75% speed
     */
    public static final double kMaxDriveSpeedPercent = 0.7; // 70% speed (limited)

    /**
     * Turn sensitivity multiplier (0.0 to 1.0).
     * Reduces the effect of turning compared to forward/backward movement for easier control.
     * 1.0 = Full turning sensitivity (100%)
     * 0.5 = Half turning sensitivity (50% - easier to drive straight)
     * 0.3 = Low turning sensitivity (30% - very easy to drive straight)
     * Lower values make the robot easier to control but reduce turning responsiveness.
     */
    public static final double kTurnSensitivityMultiplier = 0.5; // 50% turn sensitivity

    /**
     * Brake power applied when robot is disabled (0.0 to 1.0).
     * This applies reverse power to help slow down the robot faster.
     * 0.15 = 15% reverse power for braking
     */
    public static final double kDisabledBrakePower = 0.15; // 15% brake power
  }

  public static final class ShooterConstants {
    /** CAN ID for shooter motor */
    public static final int kShooterMotorPort = 3; // CAN ID for shooter motor

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
    public static final double kShooterTargetVelocityRPS = -28.0; // Start here for smaller motors (adjust based on testing)
    
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
    public static final double kShooterI = 0.000;  // Add if steady-state error
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
    public static final double kShooterKV = 0.16; // Start here - most critical value! Tune this first

  }

  public static final class FeederConstants {
    /** CAN ID for feeder motor */
    public static final int kFeederMotorPort = 4; // CAN ID for feeder motor

    /**
     * Feeder motor percent output.
     * Positive values feed balls into the shooter; adjust based on testing.
     */
    public static final double kFeederPercentOutput = -0.11; // 11% power
  }

  public static final class AutoConstants {
    public static final double kAutoDriveForwardDistance = 0.5; // Distance in meters for autonomous
    public static final double kDriveSpeed = 0.5; // Speed for autonomous driving
    public static final double kTurnSpeed = 0.4; // Speed for turning in autonomous
    public static final double kAutoTurnDegrees = 90.0; // Turn angle in degrees for autonomous
  }
 
  public static final class OIConstants {
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
    public static final int kIntakeCloseButtonIdx = 6; // Button index for running feeder (legacy name kept for compatibility)
    public static final int kShooterButtonIdx = 5; // Button index for turning on shooter
    
    /**
     * Deadband value for joystick axes.
     * Values below this threshold will be treated as zero to prevent drift.
     * Typical values: 0.05 - 0.15
     */
    public static final double kJoystickDeadband = 0.05;
  }
}

