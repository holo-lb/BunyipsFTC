package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.UserSelection;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.UnaryFunction;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicVectorDriveTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.WaitUntilTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.util.Threads;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.TransferSample;

/**
 * TeleOp for Vance.
 *
 * @author Lachlan Paul, 2024
 */
@TeleOp(name = "TeleOp")
@Config
public class VanceTeleOp extends CommandBasedBunyipsOpMode {
    /**
     * Whether to force start the end-game light pattern.
     */
    public static boolean lightForceStart;
    /**
     * Field-centric mode.
     */
    public static boolean FC = true;
    private final Vance robot = new Vance();

    @Override
    protected void onInitialise() {
        robot.init();
        Threads.start("sel", new UserSelection<>((m) -> {
            if (m == null) {
                FC = true;
                return;
            }
            if (m.equals("FIELD-CENTRIC")) {
                FC = true;
            } else if (m.equals("ROBOT-CENTRIC")) {
                FC = false;
            }
        }, "FIELD-CENTRIC", "ROBOT-CENTRIC"));
        setInitTask(new WaitUntilTask(() -> !Threads.isRunning("sel")));
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

        operator().whenPressed(Controls.RIGHT_BUMPER)
                .run(new TransferSample(robot.verticalLift, robot.horizontalLift, robot.clawRotator, robot.basketRotator, robot.claws));

        HolonomicVectorDriveTask hvdt = new HolonomicVectorDriveTask(gamepad1, robot.drive, () -> FC);
        robot.drive.setDefaultTask(hvdt);
        driver().whenPressed(Controls.A)
                .run("Reset FC Offset", () -> hvdt.resetFieldCentricOrigin(robot.drive.getPose()));

        robot.verticalLift.setDefaultTask(robot.verticalLift.tasks.control(() -> -gamepad2.rsy));
        robot.horizontalLift.setDefaultTask(robot.horizontalLift.tasks.control(() -> -gamepad2.lsy));
    }
}
