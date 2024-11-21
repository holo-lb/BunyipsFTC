package org.murraybridgebunyips.wheatley.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * Debugging OpMode for reading the linear actuator encoder position.
 */
@Disabled
@TeleOp
public class WheatleyLinearActuatorEncoderDebug extends BunyipsOpMode {
    private final WheatleyConfig config = new WheatleyConfig();

    @Override
    protected void onInit() {
        config.init();
    }

    @Override
    protected void activeLoop() {
        telemetry.add(config.linearActuator.getCurrentPosition());
        config.linearActuator.setPower(-gamepad2.lsy);
    }
}
