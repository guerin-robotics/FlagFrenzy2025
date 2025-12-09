// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command to turn the robot a specified number of degrees.
 * Positive degrees = turn right (clockwise), negative = turn left (counter-clockwise).
 */
public class TurnCommand extends Command {
    private final DriveSubsystem m_driveSubsystem;
    private final double m_targetDegrees;
    private int m_timeoutCounter = 0;
    private static final int MAX_ITERATIONS = 300; // ~6 seconds at 50Hz
    private double m_initialLeftMeters;
    private double m_initialRightMeters;

    /**
     * Creates a new TurnCommand.
     *
     * @param driveSubsystem The drive subsystem to use
     * @param degrees The angle to turn in degrees (positive = right/clockwise, negative = left/counter-clockwise)
     * @throws IllegalArgumentException if degrees is not finite
     */
    public TurnCommand(DriveSubsystem driveSubsystem, double degrees) {
        m_driveSubsystem = driveSubsystem;
        if (!Double.isFinite(degrees)) {
            throw new IllegalArgumentException("Degrees must be a finite number, got: " + degrees);
        }
        m_targetDegrees = degrees;
        addRequirements(driveSubsystem);
    }

    @Override
    public void initialize() {
        // Handle zero-degree turns (already at target)
        if (Math.abs(m_targetDegrees) < 0.1) {
            return; // Will finish immediately in isFinished()
        }
        
        // Store initial encoder positions
        m_initialLeftMeters = m_driveSubsystem.getLeftEncoderMeters();
        m_initialRightMeters = m_driveSubsystem.getRightEncoderMeters();
        m_timeoutCounter = 0;
    }

    @Override
    public void execute() {
        // Determine turn direction: positive degrees = turn right (left forward, right backward)
        double turnSpeed = m_targetDegrees > 0 ? AutoConstants.kTurnSpeed : -AutoConstants.kTurnSpeed;
        
        // Turn in place: left and right motors move in opposite directions
        m_driveSubsystem.setMotors(turnSpeed, -turnSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the drive when command ends
        m_driveSubsystem.setMotors(0, 0);
    }

    @Override
    public boolean isFinished() {
        // Handle zero-degree turns (already at target)
        if (Math.abs(m_targetDegrees) < 0.1) {
            return true;
        }
        
        // Safety timeout to prevent infinite execution if encoders fail
        if (++m_timeoutCounter > MAX_ITERATIONS) {
            System.err.println("WARNING: TurnCommand timed out after " + MAX_ITERATIONS + " iterations");
            return true;
        }
        
        // Validate track width to prevent division by zero
        if (DriveConstants.kTrackWidthMeters <= 0) {
            System.err.println("ERROR: Invalid track width! Cannot calculate rotation.");
            return true;
        }
        
        // Calculate current rotation angle in radians
        // Rotation = (left_distance - right_distance) / track_width
        double currentLeftMeters = m_driveSubsystem.getLeftEncoderMeters();
        double currentRightMeters = m_driveSubsystem.getRightEncoderMeters();
        
        double leftDelta = currentLeftMeters - m_initialLeftMeters;
        double rightDelta = currentRightMeters - m_initialRightMeters;
        
        // Calculate rotation angle in radians
        double rotationRadians = (leftDelta - rightDelta) / DriveConstants.kTrackWidthMeters;
        
        // Convert to degrees
        double rotationDegrees = Math.toDegrees(rotationRadians);
        
        // Check if we've reached the target
        if (m_targetDegrees > 0) {
            // Turning right - check if we've turned enough
            return rotationDegrees >= m_targetDegrees;
        } else {
            // Turning left - check if we've turned enough (rotationDegrees will be negative)
            return rotationDegrees <= m_targetDegrees;
        }
    }
}

