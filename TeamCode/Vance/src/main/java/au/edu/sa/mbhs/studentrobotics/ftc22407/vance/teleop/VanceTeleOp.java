package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.teleop;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Minutes;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.LookupTable;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.UnaryFunction;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicVectorDriveTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.util.Geometry;
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
    private final LookupTable<Double, RevBlinkinLedDriver.BlinkinPattern> toCenterPattern = new LookupTable<>();

    @Override
    protected void onInitialise() {
        robot.init();
        gamepad1.set(Controls.AnalogGroup.STICKS, UnaryFunction.SQUARE_KEEP_SIGN);
        toCenterPattern.add(32.0, RevBlinkinLedDriver.BlinkinPattern.GREEN);
        toCenterPattern.add(50.0, RevBlinkinLedDriver.BlinkinPattern.YELLOW);
        toCenterPattern.add(72.0, RevBlinkinLedDriver.BlinkinPattern.RED);
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

    @Override
    protected void periodic() {
        boolean robotIsMoving = robot.drive.getVelocity().linearVel.norm() > 2;

        // LED Management
        if (timer.elapsedTime().gte(Minutes.of(1.5)) || lightForceStart) {
            double distToCenter = Geometry.distTo(robot.drive.getPose().position, new Vector2d(0, 0));
            robot.lights.setPattern(toCenterPattern.get(distToCenter));
        } else if (robotIsMoving) {
            robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_RED);
        } else {
            robot.lights.resetPattern();
        }
        robot.verticalLift.setDefaultTask(robot.verticalLift.tasks.control(() -> -gamepad2.rsy));
        robot.horizontalLift.setDefaultTask(robot.horizontalLift.tasks.control(() -> -gamepad2.lsy));
        robot.drive.setDefaultTask(new HolonomicVectorDriveTask(gamepad1, robot.drive, () -> FC));
    }
}
