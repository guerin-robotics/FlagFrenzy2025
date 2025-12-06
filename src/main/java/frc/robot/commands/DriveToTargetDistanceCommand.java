// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.DistanceSensorConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.DistanceSensorSubsystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command to drive forward slowly until both distance sensors reach the target distance.
 * This is used for precise positioning before shooting.
 */
public class DriveToTargetDistanceCommand extends Command {
    private final DriveSubsystem m_driveSubsystem;
    private final DistanceSensorSubsystem m_distanceSensorSubsystem;
    private final double m_targetDistance;
    private final double m_tolerance;
    private final double m_speed;
    private final Timer m_timer = new Timer();
    private final double m_timeoutSeconds;
    
    // Warning suppression to avoid console spam
    private boolean m_hasWarnedInvalidSensors = false;

    /**
     * Creates a new DriveToTargetDistanceCommand using default constants.
     *
     * @param driveSubsystem The drive subsystem to use
     * @param distanceSensorSubsystem The distance sensor subsystem to use
     */
    public DriveToTargetDistanceCommand(
            DriveSubsystem driveSubsystem,
            DistanceSensorSubsystem distanceSensorSubsystem) {
        this(driveSubsystem, distanceSensorSubsystem,
                DistanceSensorConstants.kTargetDistanceMeters,
                DistanceSensorConstants.kDistanceToleranceMeters,
                DistanceSensorConstants.kPositioningSpeed,
                DistanceSensorConstants.kMaxPositioningTimeSeconds);
    }

    /**
     * Creates a new DriveToTargetDistanceCommand with custom parameters.
     *
     * @param driveSubsystem The drive subsystem to use
     * @param distanceSensorSubsystem The distance sensor subsystem to use
     * @param targetDistance Target distance in meters
     * @param tolerance Tolerance in meters
     * @param speed Forward speed (0.0 to 1.0)
     * @param timeoutSeconds Maximum time to spend positioning
     */
    public DriveToTargetDistanceCommand(
            DriveSubsystem driveSubsystem,
            DistanceSensorSubsystem distanceSensorSubsystem,
            double targetDistance,
            double tolerance,
            double speed,
            double timeoutSeconds) {
        m_driveSubsystem = driveSubsystem;
        m_distanceSensorSubsystem = distanceSensorSubsystem;
        m_targetDistance = targetDistance;
        m_tolerance = tolerance;
        m_speed = speed;
        m_timeoutSeconds = timeoutSeconds;

        addRequirements(driveSubsystem);
        // Note: We don't require the sensor subsystem since we only read from it
    }

    @Override
    public void initialize() {
        m_timer.reset();
        m_timer.start();
        m_hasWarnedInvalidSensors = false; // Reset warning flag
        System.out.println("DriveToTargetDistance: Starting positioning to " + m_targetDistance + "m");
    }

    @Override
    public void execute() {
        // Check if sensors are valid
        if (!m_distanceSensorSubsystem.bothSensorsValid()) {
            if (!m_hasWarnedInvalidSensors) {
                System.err.println("WARNING: Distance sensors not reading valid values!");
                m_hasWarnedInvalidSensors = true; // Only warn once per command execution
            }
            // Continue trying, but log the issue only once
        } else {
            // Reset warning flag if sensors become valid again
            m_hasWarnedInvalidSensors = false;
        }

        // Get current distances
        double leftDistance = m_distanceSensorSubsystem.getLeftDistanceMeters();
        double rightDistance = m_distanceSensorSubsystem.getRightDistanceMeters();

        // Determine if we need to move forward or backward
        // If both sensors are too far, move forward
        // If both sensors are too close, move backward
        // If one is too close and one is too far, we're aligned but need to move forward/backward

        boolean leftAtTarget = leftDistance >= 0 && Math.abs(leftDistance - m_targetDistance) <= m_tolerance;
        boolean rightAtTarget = rightDistance >= 0 && Math.abs(rightDistance - m_targetDistance) <= m_tolerance;

        if (leftAtTarget && rightAtTarget) {
            // Both sensors are at target - stop
            m_driveSubsystem.setMotors(0, 0);
            return;
        }

        // Calculate average distance to determine direction
        double avgDistance = m_distanceSensorSubsystem.getAverageDistanceMeters();
        
        if (avgDistance < 0) {
            // Invalid reading - stop for safety
            m_driveSubsystem.setMotors(0, 0);
            return;
        }

        // Determine direction and speed
        double driveSpeed = 0.0;
        
        if (avgDistance < m_targetDistance - m_tolerance) {
            // Too close - move backward slowly
            driveSpeed = -m_speed;
        } else if (avgDistance > m_targetDistance + m_tolerance) {
            // Too far - move forward slowly
            driveSpeed = m_speed;
        } else {
            // Within tolerance - stop
            driveSpeed = 0.0;
        }

        // Drive forward/backward at slow speed
        m_driveSubsystem.setMotors(driveSpeed, driveSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        m_timer.stop();
        // Stop the drive when command ends
        m_driveSubsystem.setMotors(0, 0);
        
        if (interrupted) {
            System.out.println("DriveToTargetDistance: Interrupted");
        } else {
            System.out.println("DriveToTargetDistance: Completed positioning");
        }
    }

    @Override
    public boolean isFinished() {
        // Check timeout
        if (m_timer.hasElapsed(m_timeoutSeconds)) {
            System.err.println("WARNING: DriveToTargetDistance timed out after " + m_timeoutSeconds + " seconds");
            return true;
        }

        // Check if both sensors are at target
        return m_distanceSensorSubsystem.bothSensorsAtTarget(m_targetDistance, m_tolerance);
    }
}

