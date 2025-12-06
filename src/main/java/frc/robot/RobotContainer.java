package frc.robot;

import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.AlignWithDistanceSensorsCommand;
import frc.robot.commands.ArcadeDriveCommand;
import frc.robot.commands.DriveForwardCommand;
import frc.robot.commands.DriveToTargetDistanceCommand;
import frc.robot.commands.IntakeSetCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.DistanceSensorSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared.
 * Since Command-based is a "declarative" paradigm, very little robot logic
 * should actually be in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    private final DriveSubsystem driveSubsystem = new DriveSubsystem();
    private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
    private final FeederSubsystem feederSubsystem = new FeederSubsystem();
    private final DistanceSensorSubsystem distanceSensorSubsystem = new DistanceSensorSubsystem();
    // Joystick port: USB port number on the roboRIO/roboRIO 2.0
    // Port 0 = First USB port, Port 1 = Second USB port, etc.
    // Configure this in Driver Station under "USB Devices" or check the USB port number
    private final Joystick joystick1 = new Joystick(OIConstants.kDriverJoystickPort);

    public RobotContainer() {
        // Validate joystick connection
        if (joystick1.getButtonCount() == 0) {
            System.err.println("WARNING: Joystick not detected on port " + OIConstants.kDriverJoystickPort);
        }
        
        configureButtonBindings();

        driveSubsystem.setDefaultCommand(new ArcadeDriveCommand(driveSubsystem, //
                () -> -joystick1.getRawAxis(OIConstants.kArcadeDriveSpeedAxis),
                () -> joystick1.getRawAxis(OIConstants.kArcadeDriveTurnAxis))//
        );
        // No default command for intake - motor only runs when button is pressed
    }

    private void configureButtonBindings() {
        // Button 6: Run intake motor at set velocity (while held)
        new JoystickButton(joystick1, OIConstants.kIntakeCloseButtonIdx)
                .whileTrue(new IntakeSetCommand(intakeSubsystem));
        
        // Button 5: Toggle shooter on/off - runs at target velocity using PID control
        new JoystickButton(joystick1, OIConstants.kShooterButtonIdx)
                .toggleOnTrue(feederSubsystem.shootCommand());
        
        // Button 7: Align robot using distance sensors with PID control (while held)
        new JoystickButton(joystick1, OIConstants.kAlignButtonIdx)
                .whileTrue(new AlignWithDistanceSensorsCommand(driveSubsystem, distanceSensorSubsystem));
    }

    /**
     * Returns a command to drive forward until both distance sensors reach the target distance.
     * Useful for precise positioning before shooting.
     * 
     * @return Command to position robot using distance sensors
     */
    public Command getDriveToTargetDistanceCommand() {
        return new DriveToTargetDistanceCommand(driveSubsystem, distanceSensorSubsystem);
    }

    public Command getAutonomousCommand() {
        return new SequentialCommandGroup( //
                new DriveForwardCommand(driveSubsystem, AutoConstants.kAutoDriveForwardDistance) //
                // Intake can be added here if needed for autonomous
        );
    }

    /**
     * Example autonomous command that positions using distance sensors, then shoots.
     * Uncomment and customize as needed.
     */
    /*
    public Command getPositionAndShootAutonomous() {
        return new SequentialCommandGroup(
                // Position robot using distance sensors
                getDriveToTargetDistanceCommand(),
                // Wait a moment for robot to settle
                new WaitCommand(0.5),
                // Start shooter
                feederSubsystem.shootCommand()
        );
    }
    */
}

