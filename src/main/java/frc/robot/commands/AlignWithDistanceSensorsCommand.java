// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.DistanceSensorConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.DistanceSensorSubsystem;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command to align the robot using PID control based on distance sensor readings.
 * While held, this command will:
 * 1. Turn the robot to align both sensors (minimize difference between left and right)
 * 2. Move forward/backward to reach target distance
 * 
 * Uses separate PID controllers for alignment (turning) and distance (forward/backward).
 */
public class AlignWithDistanceSensorsCommand extends Command {
    private final DriveSubsystem m_driveSubsystem;
    private final DistanceSensorSubsystem m_distanceSensorSubsystem;
    
    // PID controllers
    private final PIDController m_alignmentPID;
    private final PIDController m_distancePID;
    
    // Target distance
    private final double m_targetDistance;
    
    // Warning suppression to avoid console spam
    private boolean m_hasWarnedInvalidSensors = false;

    /**
     * Creates a new AlignWithDistanceSensorsCommand using default constants.
     *
     * @param driveSubsystem The drive subsystem to use
     * @param distanceSensorSubsystem The distance sensor subsystem to use
     */
    public AlignWithDistanceSensorsCommand(
            DriveSubsystem driveSubsystem,
            DistanceSensorSubsystem distanceSensorSubsystem) {
        this(driveSubsystem, distanceSensorSubsystem, DistanceSensorConstants.kTargetDistanceMeters);
    }

    /**
     * Creates a new AlignWithDistanceSensorsCommand with custom target distance.
     *
     * @param driveSubsystem The drive subsystem to use
     * @param distanceSensorSubsystem The distance sensor subsystem to use
     * @param targetDistance Target distance in meters
     */
    public AlignWithDistanceSensorsCommand(
            DriveSubsystem driveSubsystem,
            DistanceSensorSubsystem distanceSensorSubsystem,
            double targetDistance) {
        m_driveSubsystem = driveSubsystem;
        m_distanceSensorSubsystem = distanceSensorSubsystem;
        m_targetDistance = targetDistance;

        // Initialize alignment PID (controls turning to align sensors)
        m_alignmentPID = new PIDController(
                DistanceSensorConstants.kAlignmentP,
                DistanceSensorConstants.kAlignmentI,
                DistanceSensorConstants.kAlignmentD);
        m_alignmentPID.setTolerance(DistanceSensorConstants.kAlignmentToleranceMeters);
        m_alignmentPID.setSetpoint(0.0); // Target: zero difference between sensors

        // Initialize distance PID (controls forward/backward movement)
        m_distancePID = new PIDController(
                DistanceSensorConstants.kDistanceP,
                DistanceSensorConstants.kDistanceI,
                DistanceSensorConstants.kDistanceD);
        m_distancePID.setTolerance(DistanceSensorConstants.kDistanceToleranceMeters);
        m_distancePID.setSetpoint(m_targetDistance);

        addRequirements(driveSubsystem);
    }

    @Override
    public void initialize() {
        // Reset PID controllers
        m_alignmentPID.reset();
        m_distancePID.reset();
        m_hasWarnedInvalidSensors = false; // Reset warning flag
        System.out.println("AlignWithDistanceSensors: Starting alignment to " + m_targetDistance + "m");
    }

    @Override
    public void execute() {
        // Get current sensor readings
        double leftDistance = m_distanceSensorSubsystem.getLeftDistanceMeters();
        double rightDistance = m_distanceSensorSubsystem.getRightDistanceMeters();

        // Check if sensors are valid
        if (leftDistance < 0 || rightDistance < 0) {
            // Invalid readings - stop for safety
            if (!m_hasWarnedInvalidSensors) {
                System.err.println("WARNING: Distance sensors not reading valid values!");
                m_hasWarnedInvalidSensors = true; // Only warn once per command execution
            }
            m_driveSubsystem.setMotors(0, 0);
            return;
        }
        
        // Reset warning flag if sensors become valid again
        m_hasWarnedInvalidSensors = false;

        // Calculate alignment error (difference between sensors)
        // Positive error = left sensor is further, need to turn left
        // Negative error = right sensor is further, need to turn right
        double alignmentError = leftDistance - rightDistance;
        
        // Calculate average distance from target
        double averageDistance = (leftDistance + rightDistance) / 2.0;

        // Calculate PID outputs
        // Alignment PID: uses error (difference) with setpoint 0.0
        // Distance PID: uses measurement (average distance) with setpoint (target distance)
        double turnOutput = m_alignmentPID.calculate(alignmentError);
        double driveOutput = m_distancePID.calculate(averageDistance);

        // Clamp outputs to maximum speeds
        turnOutput = Math.max(-DistanceSensorConstants.kMaxAlignmentTurnSpeed,
                             Math.min(DistanceSensorConstants.kMaxAlignmentTurnSpeed, turnOutput));
        driveOutput = Math.max(-DistanceSensorConstants.kMaxAlignmentDriveSpeed,
                              Math.min(DistanceSensorConstants.kMaxAlignmentDriveSpeed, driveOutput));

        // Apply to motors (arcade drive: driveOutput = forward/back, turnOutput = left/right)
        // Left motor = drive + turn, Right motor = drive - turn
        double leftSpeed = driveOutput + turnOutput;
        double rightSpeed = driveOutput - turnOutput;

        // Clamp individual motor speeds to prevent exceeding limits
        leftSpeed = Math.max(-1.0, Math.min(1.0, leftSpeed));
        rightSpeed = Math.max(-1.0, Math.min(1.0, rightSpeed));

        m_driveSubsystem.setMotors(leftSpeed, rightSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the drive when command ends
        m_driveSubsystem.setMotors(0, 0);
        
        if (interrupted) {
            System.out.println("AlignWithDistanceSensors: Interrupted");
        } else {
            System.out.println("AlignWithDistanceSensors: Alignment complete");
        }
    }

    @Override
    public boolean isFinished() {
        // This command runs continuously while button is held
        // It will be interrupted when button is released
        return false;
    }
}

