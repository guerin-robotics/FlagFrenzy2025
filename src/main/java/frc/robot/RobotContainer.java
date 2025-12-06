package frc.robot;

import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.ArcadeDriveCommand;
import frc.robot.commands.DriveForwardCommand;
import frc.robot.commands.IntakeSetCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class RobotContainer {
    private final DriveSubsystem driveSubsystem = new DriveSubsystem();
    private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
    private final FeederSubsystem feederSubsystem = new FeederSubsystem();
    // Joystick port: USB port number on the roboRIO/roboRIO 2.0
    // Port 0 = First USB port, Port 1 = Second USB port, etc.
    // Configure this in Driver Station under "USB Devices" or check the USB port number
    private final Joystick joystick1 = new Joystick(OIConstants.kDriverJoystickPort);

    public RobotContainer() {
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
    }

    public Command getAutonomousCommand() {
        return new SequentialCommandGroup( //
                new DriveForwardCommand(driveSubsystem, AutoConstants.kAutoDriveForwardDistance) //
                // Intake can be added here if needed for autonomous
        );
    }
}

