// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.IntakeSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command to run the intake at a set velocity when button is pressed.
 */
public class IntakeSetCommand extends Command {
    private final IntakeSubsystem m_intakeSubsystem;

    /**
     * Creates a new IntakeSetCommand that runs the intake at the configured velocity.
     *
     * @param intakeSubsystem The intake subsystem to use
     */
    public IntakeSetCommand(IntakeSubsystem intakeSubsystem) {
        m_intakeSubsystem = intakeSubsystem;
        addRequirements(intakeSubsystem);
    }

    @Override
    public void initialize() {
        // Velocity control is handled in execute() - no initialization needed
    }

    @Override
    public void execute() {
        // Run at configured velocity while command is active
        m_intakeSubsystem.runAtVelocity();
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the intake motor when button is released
        m_intakeSubsystem.stop();
    }

    @Override
    public boolean isFinished() {
        // Run continuously until interrupted (button released)
        return false;
    }
}

