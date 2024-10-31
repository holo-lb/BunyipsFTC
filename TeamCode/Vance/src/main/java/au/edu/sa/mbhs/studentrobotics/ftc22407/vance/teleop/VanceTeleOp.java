package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.teleop;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.UnaryFunction;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.ThreeWheelLocalizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.accumulators.PeriodicIMUAccumulator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.BlinkinLights;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.DualServos;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.Switch;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.drive.MecanumDrive;
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
    private MecanumDrive drive;
    private HoldableActuator verticalArm;
    private HoldableActuator horizontalArm;
    private Switch clawRotator;
    private Switch basketRotator;
    private DualServos claws;
    private BlinkinLights lights;

    @Override
    protected void onInitialise() {
        robot.init();
        drive = new MecanumDrive(robot.driveModel, robot.motionProfile, robot.mecanumGains, robot.fl, robot.bl, robot.br, robot.fr, robot.imu, hardwareMap.voltageSensor)
                .withLocalizer(new ThreeWheelLocalizer(robot.driveModel, robot.localiserParams, robot.dwleft, robot.dwright, robot.dwx))
                .withAccumulator(new PeriodicIMUAccumulator(robot.imu.get(), Seconds.of(5)))
                .withName("Drive");
        verticalArm = new HoldableActuator(robot.verticalArm)
                .withPowerClamps(-0.5, 0.5)
                .enableUserSetpointControl((dt) -> 100 * dt)
                .withName("Vertical Arm");
        horizontalArm = new HoldableActuator(robot.horizontalArm)
                .withPowerClamps(-0.5, 0.5)
                .withName("Horizontal Arm");
        clawRotator = new Switch(robot.clawRotator)
                .withName("Claw Rotator");
        basketRotator = new Switch(robot.basketRotator)
                .withName("Basket Rotator");

        // TODO: check open/close values
        claws = new DualServos(robot.leftClaw, robot.rightClaw, 1.0, 0.0, 0.0, 1.0);
        lights = new BlinkinLights(robot.lights, RevBlinkinLedDriver.BlinkinPattern.LAWN_GREEN);

        gamepad1.set(Controls.AnalogGroup.STICKS, UnaryFunction.SQUARE_KEEP_SIGN);
    }

    @Override
    protected void assignCommands() {
        operator().whenPressed(Controls.X)
                        .run(claws.tasks.toggleBoth());
        operator().whenPressed(Controls.A)
                        .run(clawRotator.tasks.toggle());
        operator().whenPressed(Controls.Y)
                        .run(basketRotator.tasks.toggle());

//        operator().whenPressed(Controls.RIGHT_BUMPER)
//                .run(new SampleToBasket(verticalArm, horizontalArm, clawRotator, claws));

        verticalArm.setDefaultTask(verticalArm.tasks.control(() -> -gamepad2.lsy));
        horizontalArm.setDefaultTask(horizontalArm.tasks.control(() -> -gamepad2.rsy));
        drive.setDefaultTask(new HolonomicVectorDriveTask(gamepad1, drive, () -> false));
    }

    @Override
    protected void periodic() {
        PoseVelocity2d vel = drive.getVelocity();
        boolean robotIsMoving = drive.getVelocity().linearVel.norm() > 0;

        // LED Management
        if (timer.elapsedTime().in(Seconds) <= 145) {
            // Activates 5 seconds before endgame
            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_RED);
        } else if (robotIsMoving) {
            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
        }

        telemetry.add(robotIsMoving);

        // Some drivers have noted that they sometimes cannot tell whether a claw is open or closed.
        // Hopefully this helps. Update: It did :)
        // The actual string is set to the opposite of what you might expect, by driver request.
        telemetry.add("\n---------");
        telemetry.add("Claws: " + (claws.isOpen(DualServos.ServoSide.BOTH) ? "Closed" : "Open")).big();
        telemetry.add("Claw Rotator: " + (clawRotator.isOpen() ? "Closed" : "Open")).big();
        telemetry.add("Basket Rotator: " + (basketRotator.isOpen() ? "Closed" : "Open")).big();
        telemetry.add("---------\n");

        telemetry.add("Bottom Switch Is %", robot.bottomLimit.isPressed() ? "Pressed" : "Not Pressed");
    }
}
