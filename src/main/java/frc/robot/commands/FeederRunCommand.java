// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.FeederSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command to run the feeder for a specified number of rotations.
 * Uses the configured percent output speed from FeederConstants.
 */
public class FeederRunCommand extends Command {
    private final FeederSubsystem m_feederSubsystem;
    private final double m_targetRotations;
    private int m_timeoutCounter = 0;
    private static final int MAX_ITERATIONS = 200; // ~4 seconds at 50Hz
    private double m_initialRotations;

    /**
     * Creates a new FeederRunCommand that runs the feeder for a specified number of rotations.
     *
     * @param feederSubsystem The feeder subsystem to use
     * @param rotations The number of rotations to run (must be positive and finite)
     * @throws IllegalArgumentException if rotations is not finite or is negative
     */
    public FeederRunCommand(FeederSubsystem feederSubsystem, double rotations) {
        m_feederSubsystem = feederSubsystem;
        if (!Double.isFinite(rotations) || rotations < 0) {
            throw new IllegalArgumentException("Rotations must be a positive finite number, got: " + rotations);
        }
        m_targetRotations = rotations;
        addRequirements(feederSubsystem);
    }

    @Override
    public void initialize() {
        // Reset encoder to zero for accurate rotation counting
        m_feederSubsystem.resetEncoder();
        // After reset, encoder is at 0, so initial position is 0
        m_initialRotations = 0.0;
        m_timeoutCounter = 0;
    }

    @Override
    public void execute() {
        // Run at configured percent output
        m_feederSubsystem.runAtVelocity();
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the feeder motor when command ends
        m_feederSubsystem.stop();
    }

    @Override
    public boolean isFinished() {
        // Safety timeout to prevent infinite execution if encoder fails
        if (++m_timeoutCounter > MAX_ITERATIONS) {
            System.err.println("WARNING: FeederRunCommand timed out after " + MAX_ITERATIONS + " iterations");
            return true;
        }
        
        // Calculate current rotations since start
        double currentRotations = m_feederSubsystem.getEncoderRotations() - m_initialRotations;
        
        // Check if we've reached the target
        return Math.abs(currentRotations) >= m_targetRotations;
    }
}

