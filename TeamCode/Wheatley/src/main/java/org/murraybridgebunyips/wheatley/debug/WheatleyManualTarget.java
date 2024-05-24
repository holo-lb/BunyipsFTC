package org.murraybridgebunyips.wheatley.debug;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * Manual motor runner for clawRotator
 */
@TeleOp
public class WheatleyManualTarget extends BunyipsOpMode {
    private final WheatleyConfig config = new WheatleyConfig();
    private double pos;
    @Override
    protected void onInit() {
        config.init();
    }

    @Override
    protected void activeLoop() {
        pos -= gamepad2.lsy;
        config.clawRotator.setTargetPosition((int)pos);
        config.clawRotator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        config.clawRotator.setPower(1);
        addTelemetry("current:%", config.clawRotator.getCurrentPosition());
        addTelemetry("target:%", (int)pos);
    }
}
