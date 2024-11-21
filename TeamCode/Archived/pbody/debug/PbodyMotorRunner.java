package org.murraybridgebunyips.pbody.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.pbody.components.PbodyConfig;

/**
 * Tests motor position and direction
 */
@TeleOp(name = "Motor Runner (DEBUG)")
@Disabled
public class PbodyMotorRunner extends BunyipsOpMode {
    private final PbodyConfig config = new PbodyConfig();

    @Override
    protected void onInit() {
        config.init();
    }

    @Override
    protected void activeLoop() {
        if (config.bl != null)
            config.bl.setPower(gamepad1.a ? 1.0 : 0.0);
        if (config.br != null)
            config.br.setPower(gamepad1.b ? 1.0 : 0.0);
        if (config.fl != null)
            config.fl.setPower(gamepad1.x ? 1.0 : 0.0);
        if (config.fr != null)
            config.fr.setPower(gamepad1.y ? 1.0 : 0.0);
    }
}
