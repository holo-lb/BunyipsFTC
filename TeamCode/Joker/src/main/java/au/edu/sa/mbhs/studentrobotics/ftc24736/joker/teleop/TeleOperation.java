package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.teleop;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;

@TeleOp(name = "TeleOp")
public class TeleOperation extends BunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInit() {
        robot.init();
        //robot.outtakeAlign.setPosition(Joker.OUTTAKE_ALIGN_IN_POSITION);
    }

    @Override
    protected void activeLoop() {
        double leftStickX = gamepad1.left_stick_x;
        double leftStickY = gamepad1.left_stick_y;
        double rightStickX = gamepad1.right_stick_x;
        double gp2LeftStickY = -gamepad2.left_stick_y;
        double gp2RightStickY = -gamepad2.right_stick_y;
        if (gamepad2.getDebounced(Controls.A)) {
            robot.toggleIntakeGrip();
        }
        if (gamepad2.getDebounced(Controls.X)) {
            robot.toggleOuttakeGrip();
        }
        //if (gamepad2.getDebounced(Controls.X)) {
            //robot.toggleOuttake();
        //}
        if (robot.handoverPoint.isPressed()) {
            robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
            robot.lift.withPowerClamps(Joker.LIFT_LOWER_POWER_CLAMP_WHEN_HANDOVER_POINT,
                    Joker.LIFT_UPPER_POWER_CLAMP_WHEN_HANDOVER_POINT);
        }
        else {
            robot.lights.resetPattern();
            robot.lift.withPowerClamps(Joker.LIFT_LOWER_POWER_CLAMP_WHEN_NOT_HANDOVER_POINT,
                    Joker.LIFT_UPPER_POWER_CLAMP_WHEN_NOT_HANDOVER_POINT);
        }
        robot.drive.setPower(Controls.vel(leftStickX, leftStickY, rightStickX));
        robot.intake.setPower(gp2LeftStickY);
        robot.lift.setPower(gp2RightStickY);
        robot.drive.update();
        robot.intake.update();
        robot.lift.update();
        robot.lights.update();
        telemetry.addData("currentPosition", robot.intakeMotor.getCurrentPosition());
        telemetry.addData("targetPosition", robot.intakeMotor.getTargetPosition());
    }
}