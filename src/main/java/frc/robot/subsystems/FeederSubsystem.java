package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.FeederConstants;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.VelocityVoltage;

/**
 * Subsystem for the feeder mechanism that feeds balls into the shooter.
 * Controls the motor that moves game pieces from intake to shooter.
 */
public class FeederSubsystem extends SubsystemBase {

    private final TalonFX m_feederMotor = new TalonFX(FeederConstants.kFeederMotorPort);
    private final VelocityVoltage m_velocityControl = new VelocityVoltage(0);

    public FeederSubsystem() {
    }

    @Override
    public void periodic() {
    }

    /**
     * Sets the feeder motor to run at the configured velocity.
     * Runs at the velocity specified in FeederConstants.kFeederVelocityRPS.
     */
    public void runAtVelocity() {
        m_feederMotor.setControl(m_velocityControl.withVelocity(FeederConstants.kFeederVelocityRPS));
    }

    /**
     * Stops the feeder motor.
     */
    public void stop() {
        m_feederMotor.set(0);
    }

    /**
     * Applies brake power to the feeder motor to help slow it down.
     * Used when the robot is disabled.
     *
     * @param brakePower Brake power to apply (0.0 to 1.0, typically negative for reverse/braking)
     */
    public void brake(double brakePower) {
        // Validate input
        if (!Double.isFinite(brakePower)) {
            brakePower = 0;
        }
        
        m_feederMotor.set(brakePower);
    }

    // Legacy method for backwards compatibility (if needed)
    public void setPosition(boolean open) {
        if (open) {
            m_feederMotor.set(FeederConstants.kOpenSpeed);
        } else {
            m_feederMotor.set(FeederConstants.kCloseSpeed);
        }
    }
}

