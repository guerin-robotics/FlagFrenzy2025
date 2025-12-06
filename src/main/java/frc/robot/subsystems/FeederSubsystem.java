// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.epilogue.Logged;
import frc.robot.Constants.ShooterConstants;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

@Logged
public class FeederSubsystem extends SubsystemBase {
  private final TalonFX m_shooterMotor = new TalonFX(ShooterConstants.kShooterMotorPort);
  private final VelocityVoltage m_velocityControl = new VelocityVoltage(0);

  /** The shooter subsystem for the robot. */
  public FeederSubsystem() {
    // Configure PID and feedforward gains
    configureShooterMotor();
    
    // Set default command to turn off the shooter motor, and then idle
    setDefaultCommand(
        runOnce(
                () -> {
                  m_shooterMotor.set(0);
                })
            .andThen(run(() -> {}))
            .withName("Idle"));
  }

  /**
   * Configures the shooter motor with PID and feedforward gains.
   */
  private void configureShooterMotor() {
    TalonFXConfiguration config = new TalonFXConfiguration();
    
    // Configure PID gains in slot 0
    Slot0Configs slot0 = config.Slot0;
    slot0.kP = ShooterConstants.kShooterP;
    slot0.kI = ShooterConstants.kShooterI;
    slot0.kD = ShooterConstants.kShooterD;
    slot0.kS = ShooterConstants.kShooterKS; // Static friction
    slot0.kV = ShooterConstants.kShooterKV; // Velocity feedforward
    
    // Apply configuration
    m_shooterMotor.getConfigurator().apply(config);
  }

  /**
   * Runs the shooter at the target velocity using PID control.
   * Uses the velocity specified in ShooterConstants.kShooterTargetVelocityRPS.
   */
  public void runAtTargetVelocity() {
    m_shooterMotor.setControl(m_velocityControl.withVelocity(ShooterConstants.kShooterTargetVelocityRPS));
  }

  /**
   * Returns a command to run the shooter at the configured target velocity using PID control.
   * This matches the RPM that 28% power would produce.
   */
  public Command shootCommand() {
    return run(
            () -> {
              runAtTargetVelocity();
            })
        .withName("Shoot");
  }
}

