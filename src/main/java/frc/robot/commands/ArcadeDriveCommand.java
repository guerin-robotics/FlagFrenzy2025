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
        
        // Calculate left and right speeds for arcade drive
        double leftSpeed = speedValue + turnValue;
        double rightSpeed = speedValue - turnValue;
        
        m_driveSubsystem.setMotors(leftSpeed, rightSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop motors if command is interrupted
        m_driveSubsystem.setMotors(0, 0);
    }

    @Override
    public boolean isFinished() {
        // Run continuously until interrupted
        return false;
    }
}

