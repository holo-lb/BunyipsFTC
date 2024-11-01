package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.teleop;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.UnaryFunction;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicVectorDriveTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;

/**
 * TeleOp for Vance
 * <p></p>
 * <b>gamepad1:</b><br>
 * Left Stick X: Strafe<br>
 * Left Stick Y: Forward/Back<br>
 * <p></p>
 * <b>gamepad2:</b><br>
 * Left Stick Y: Vertical Arm<br>
 * Right Stick Y: Horizontal Arm<br>
 * X: Toggle Both Claws<br>
 * A: Toggle Claw Rotator<br>
 * Y: Toggle Basket Rotator<br>
 *
 * @author Lachlan Paul, 2024
 */
@TeleOp
public class VanceTeleOp extends CommandBasedBunyipsOpMode {
    private final Vance robot = new Vance();

    @Override
    protected void onInitialise() {
        robot.init();
        gamepad1.set(Controls.AnalogGroup.STICKS, UnaryFunction.SQUARE_KEEP_SIGN);
    }

    @Override
    protected void assignCommands() {
        operator().whenPressed(Controls.X)
                        .run(robot.claws.tasks.toggleBoth());
        operator().whenPressed(Controls.A)
                        .run(robot.clawRotator.tasks.toggle());
        operator().whenPressed(Controls.Y)
                        .run(robot.basketRotator.tasks.toggle());

//        operator().whenPressed(Controls.RIGHT_BUMPER)
//                .run(new SampleToBasket(verticalLift, horizontalLift, clawRotator, claws));

        robot.verticalLift.setDefaultTask(robot.verticalLift.tasks.control(() -> -gamepad2.lsy));
        robot.horizontalLift.setDefaultTask(robot.horizontalLift.tasks.control(() -> -gamepad2.rsy));
        robot.drive.setDefaultTask(new HolonomicVectorDriveTask(gamepad1, robot.drive, () -> false));
    }

    @Override
    protected void periodic() {
        boolean robotIsMoving = robot.drive.getVelocity().linearVel.norm() > 0;

        // LED Management
        if (timer.elapsedTime().in(Seconds) <= 145) {
            // Activates 5 seconds before endgame
            robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_RED);
        } else if (robotIsMoving) {
            robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
        }
    }
}
