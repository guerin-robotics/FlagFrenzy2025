// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.Constants.AutoConstants;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command to drive forward a specified distance.
 */
public class DriveForwardCommand extends Command {
    private final DriveSubsystem m_driveSubsystem;
    private final double m_distanceMeters;
    private int m_timeoutCounter = 0;
    private static final int MAX_ITERATIONS = 200; // ~4 seconds at 50Hz

    /**
     * Creates a new DriveForwardCommand.
     *
     * @param driveSubsystem The drive subsystem to use
     * @param distanceMeters The distance to drive forward in meters (must be positive and finite)
     * @throws IllegalArgumentException if distanceMeters is not finite or is negative
     */
    public DriveForwardCommand(DriveSubsystem driveSubsystem, double distanceMeters) {
        m_driveSubsystem = driveSubsystem;
        if (!Double.isFinite(distanceMeters) || distanceMeters < 0) {
            throw new IllegalArgumentException("Distance must be a positive finite number, got: " + distanceMeters);
        }
        m_distanceMeters = distanceMeters;
        addRequirements(driveSubsystem);
    }

    @Override
    public void initialize() {
        // Reset encoders to zero at the start of the command for accurate distance measurement
        m_driveSubsystem.resetEncoders();
        m_timeoutCounter = 0;
    }

    @Override
    public void execute() {
        // Drive forward at a constant speed
        m_driveSubsystem.setMotors(AutoConstants.kDriveSpeed, AutoConstants.kDriveSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the drive when command ends
        m_driveSubsystem.setMotors(0, 0);
    }

    @Override
    public boolean isFinished() {
        // Safety timeout to prevent infinite execution if encoders fail
        if (++m_timeoutCounter > MAX_ITERATIONS) {
            System.err.println("WARNING: DriveForwardCommand timed out after " + MAX_ITERATIONS + " iterations");
            return true;
        }
        
        // Since encoders were reset in initialize(), current distance is just getEncoderMeters()
        double currentDistance = m_driveSubsystem.getEncoderMeters();
        return currentDistance >= m_distanceMeters;
    }
}

