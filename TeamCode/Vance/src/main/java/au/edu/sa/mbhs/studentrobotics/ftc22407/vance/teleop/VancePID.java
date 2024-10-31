package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.EncoderTicks;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;

/**
 * PID Testing
 */
@TeleOp
public class VancePID extends BunyipsOpMode {
    private final Vance robot = new Vance();
    private HoldableActuator ac;

    @Override
    protected void onInit() {
        robot.init();
        ac = new HoldableActuator(robot.verticalArm);
        ac.enableUserSetpointControl((dt) -> 100 * dt);
    }

    @Override
    protected void activeLoop() {
        ac.setPower(-gamepad1.lsy);
        ac.update();
        EncoderTicks.debug(robot.verticalArm, t);
    }
}
