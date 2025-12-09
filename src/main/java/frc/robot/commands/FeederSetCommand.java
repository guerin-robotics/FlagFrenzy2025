// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.FeederSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command to run the feeder at a fixed percent output while button is held.
 * The feeder moves balls from the intake into the shooter.
 */
public class FeederSetCommand extends Command {
    private final FeederSubsystem m_feederSubsystem;

    /** Creates a new FeederSetCommand that runs the feeder at the configured percent output. */
    public FeederSetCommand(FeederSubsystem feederSubsystem) {
        m_feederSubsystem = feederSubsystem;
        addRequirements(feederSubsystem);
    }

    @Override
    public void execute() {
        // Run at configured percent output while button is held
        m_feederSubsystem.runAtVelocity();
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the feeder motor when button is released
        m_feederSubsystem.stop();
    }

    @Override
    public boolean isFinished() {
        // Run continuously until interrupted (button released)
        return false;
    }
}

