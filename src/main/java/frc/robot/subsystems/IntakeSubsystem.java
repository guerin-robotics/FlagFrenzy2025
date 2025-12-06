package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.VelocityVoltage;

public class IntakeSubsystem extends SubsystemBase {

    private final TalonFX m_intakeMotor = new TalonFX(IntakeConstants.kIntakeMotorPort);
    private final VelocityVoltage m_velocityControl = new VelocityVoltage(0);

    public IntakeSubsystem() {
    }

    @Override
    public void periodic() {
    }

    /**
     * Sets the intake motor to run at the configured velocity.
     * Runs at the velocity specified in IntakeConstants.kIntakeVelocityRPS.
     */
    public void runAtVelocity() {
        m_intakeMotor.setControl(m_velocityControl.withVelocity(IntakeConstants.kIntakeVelocityRPS));
    }

    /**
     * Stops the intake motor.
     */
    public void stop() {
        m_intakeMotor.set(0);
    }

    // Legacy method for backwards compatibility (if needed)
    public void setPosition(boolean open) {
        if (open) {
            m_intakeMotor.set(IntakeConstants.kOpenSpeed);
        } else {
            m_intakeMotor.set(IntakeConstants.kCloseSpeed);
        }
    }
}

