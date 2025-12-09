package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.FeederConstants;
import com.ctre.phoenix6.hardware.TalonFX;
import static edu.wpi.first.wpilibj2.command.Commands.run;

/**
 * Subsystem for the feeder mechanism that feeds balls into the shooter.
 * Controls the motor that moves game pieces from intake to shooter.
 */
public class FeederSubsystem extends SubsystemBase {

    private final TalonFX m_feederMotor = new TalonFX(FeederConstants.kFeederMotorPort);

    public FeederSubsystem() {
        // Explicitly stop the motor at initialization to ensure it doesn't run at startup
        m_feederMotor.set(0);
        
        // Set default command to keep feeder motor stopped
        setDefaultCommand(
            run(() -> m_feederMotor.set(0))
            .withName("Idle"));
    }

    @Override
    public void periodic() {
    }

    /**
     * Runs the feeder motor at a fixed percentage output.
     * Uses FeederConstants.kFeederPercentOutput for speed.
     */
    public void runAtVelocity() {
        m_feederMotor.set(FeederConstants.kFeederPercentOutput);
    }

    /**
     * Stops the feeder motor.
     */
    public void stop() {
        m_feederMotor.set(0);
    }

    /**
     * Gets the current encoder position in rotations.
     * 
     * @return Current encoder position in rotations
     */
    public double getEncoderRotations() {
        return m_feederMotor.getPosition().getValueAsDouble();
    }

    /**
     * Resets the encoder position to zero.
     */
    public void resetEncoder() {
        m_feederMotor.setPosition(0);
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
}

