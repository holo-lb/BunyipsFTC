package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.debug;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;

/**
 * PID Testing
 */
//@TeleOp
public class VancePID extends BunyipsOpMode {
    private final Vance robot = new Vance();

    @Override
    protected void onInit() {
        robot.init();
    }

    @Override
    protected void activeLoop() {
        robot.verticalLift.setPower(-gamepad1.lsy);
        Motor.debug(robot.hw.verticalLift, "Vertical Lift", t);
        robot.verticalLift.update();
    }
}
