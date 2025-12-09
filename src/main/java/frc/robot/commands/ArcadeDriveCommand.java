// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.subsystems.DriveSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.DoubleSupplier;

/**
 * Command for arcade drive control.
 */
public class ArcadeDriveCommand extends Command {
    private final DriveSubsystem m_driveSubsystem;
    private final DoubleSupplier m_speed;
    private final DoubleSupplier m_turn;

    /**
     * Creates a new ArcadeDriveCommand.
     *
     * @param driveSubsystem The drive subsystem to use
     * @param speed Supplier for forward/backward speed
     * @param turn Supplier for turn speed
     */
    public ArcadeDriveCommand(DriveSubsystem driveSubsystem, DoubleSupplier speed, DoubleSupplier turn) {
        m_driveSubsystem = driveSubsystem;
        m_speed = speed;
        m_turn = turn;
        addRequirements(driveSubsystem);
    }

    @Override
    public void execute() {
        // Get raw joystick values
        double speedValue = m_speed.getAsDouble();
        double turnValue = m_turn.getAsDouble();
        
        // Validate inputs to prevent NaN/Infinity from damaging motors
        if (!Double.isFinite(speedValue)) {
            speedValue = 0;
        }
        if (!Double.isFinite(turnValue)) {
            turnValue = 0;
        }
        
        // Apply deadband to prevent drift from small joystick movements
        speedValue = MathUtil.applyDeadband(speedValue, OIConstants.kJoystickDeadband);
        turnValue = MathUtil.applyDeadband(turnValue, OIConstants.kJoystickDeadband);
        
        // Apply star wheel (cubic) for smoother control
        // This makes small movements more precise while still allowing full range
        // Preserve sign explicitly for clarity
        speedValue = Math.copySign(speedValue * speedValue * speedValue, speedValue);
        turnValue = Math.copySign(turnValue * turnValue * turnValue, turnValue);
        
        // Apply speed multiplier to limit maximum speed
        speedValue *= DriveConstants.kMaxDriveSpeedPercent;
        turnValue *= DriveConstants.kMaxDriveSpeedPercent;
        
        // Use arcade drive method which applies turn sensitivity multiplier
        m_driveSubsystem.arcadeDrive(speedValue, turnValue);
    }

    // Note: No end() method needed - this is a default command that should seamlessly
    // resume when interrupted commands finish. Stopping motors here would cause
    // the robot to stop moving when switching between commands.

    @Override
    public boolean isFinished() {
        // Run continuously until interrupted
        return false;
    }
}

