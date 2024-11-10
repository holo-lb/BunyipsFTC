package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.teleop;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Radians;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.UnaryFunction;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicVectorDriveTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.bases.Task;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.SampleToBasket;

/**
 * TeleOp for Vance.
 *
 * @author Lachlan Paul, 2024
 */
@TeleOp(name = "TeleOp")
@Config
public class VanceTeleOp extends CommandBasedBunyipsOpMode {
    /**
     * Field-centric mode.
     */
    public static boolean FC = true;
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
        operator().whenPressed(Controls.Y)
                .run(robot.clawRotator.tasks.toggle());
        operator().whenPressed(Controls.B)
                .run(robot.basketRotator.tasks.toggle());
        operator().whenRising(Controls.Analog.RIGHT_TRIGGER, (v) -> v == 1.0)
                .run(robot.verticalLift.tasks.home());
        driver().whenPressed(Controls.A)
                .run("Reset FC Offset", () -> {
                    Task t = robot.drive.getCurrentTask();
                    if (t instanceof HolonomicVectorDriveTask)
                        ((HolonomicVectorDriveTask) t).setFieldCentricOffset(Radians.of(robot.drive.getPose().heading.log()));
                });

        operator().whenPressed(Controls.RIGHT_BUMPER)
                .run(new SampleToBasket(robot.verticalLift, robot.horizontalLift, robot.clawRotator, robot.basketRotator, robot.claws));

        robot.verticalLift.setDefaultTask(robot.verticalLift.tasks.control(() -> -gamepad2.rsy));
        robot.horizontalLift.setDefaultTask(robot.horizontalLift.tasks.control(() -> -gamepad2.lsy));
        robot.drive.setDefaultTask(new HolonomicVectorDriveTask(gamepad1, robot.drive, () -> FC));
    }
}
