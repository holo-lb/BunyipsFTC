package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.teleop;

import com.acmerobotics.roadrunner.Rotation2d;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;

/**
 * "Whoooaaa! Looking cool, Joker!"
 */
@TeleOp(name = "TeleOp")
public class TeleOperation extends BunyipsOpMode {
    private final Joker robot = new Joker();
    private Rotation2d origin = Rotation2d.exp(0);

    @Override
    protected void onInit() {
        robot.init();
        robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.LAWN_GREEN);
        //robot.outtakeAlign.setPosition(Joker.OUTTAKE_ALIGN_IN_POSITION);
    }

    @Override
    protected void activeLoop() {
        double leftStickX = gamepad1.left_stick_x;
        double leftStickY = gamepad1.left_stick_y;
        double rightStickX = gamepad1.right_stick_x;
        double gp2LeftStickY = -gamepad2.left_stick_y;
        double gp2RightStickY = -gamepad2.right_stick_y;

        //if (gamepad2.getDebounced(Controls.LEFT_BUMPER)) {
        //    robot.toggleIntakeGrip();
        //}
        if (gamepad2.getDebounced(Controls.RIGHT_BUMPER)) {
            robot.toggleOuttakeGrip();
        }

        if (gamepad2.dpad_up) {
            robot.hook.setPower(1);
        }
        else if (gamepad2.dpad_down) {
            robot.hook.setPower(-1);
        }
        else {
            robot.hook.setPower(0);
        }

        if (gamepad2.dpad_left) {
            robot.ascentArm.setPower(0.19);
        }
        else if (gamepad2.dpad_right) {
            robot.ascentArm.setPower(-0.19);
        }
        else {
            robot.ascentArm.setPower(0);
        }

        if (gamepad1.getDebounced(Controls.A)) {
            origin = robot.drive.getPose().heading;
        }

        /*
        if (gamepad2.getDebounced(Controls.X)) {
            robot.toggleOuttake();
        }
        if (robot.handoverPoint.isPressed()) {
            robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
            robot.lift.withPowerClamps(Joker.LIFT_LOWER_POWER_CLAMP_WHEN_HANDOVER_POINT,
                    Joker.LIFT_UPPER_POWER_CLAMP_WHEN_HANDOVER_POINT);
        }
        else {
            robot.lights.resetPattern();
            robot.lift.withPowerClamps(Joker.LIFT_LOWER_POWER_CLAMP,
                    Joker.LIFT_UPPER_POWER_CLAMP);
        }
        */
        robot.drive.setPower(robot.drive.getPose().heading.inverse().times(origin).times(Controls.vel(leftStickX, leftStickY, rightStickX)));
        robot.intake.setPower(gp2LeftStickY);
        robot.lift.setPower(gp2RightStickY);
        robot.spintake.setPower(gamepad2.right_trigger - gamepad2.left_trigger);

        robot.drive.update();
        robot.intake.update();
        robot.lift.update();
        robot.lights.update();
        robot.ascentArm.update();
        telemetry.addData("intake arm current position", robot.intakeMotor.getCurrentPosition());
        telemetry.addData("intake arm target position", robot.intakeMotor.getTargetPosition());
        telemetry.addData("intake arm power", robot.intakeMotor.getPower());
        telemetry.update();
    }
}