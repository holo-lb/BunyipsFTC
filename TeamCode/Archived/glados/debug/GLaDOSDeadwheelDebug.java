package org.murraybridgebunyips.glados.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Debugging OpMode for reading the two deadwheel positions.
 */
@TeleOp(name = "Deadwheel Debug")
@Disabled
public class GLaDOSDeadwheelDebug extends BunyipsOpMode {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();

    @Override
    protected void onInit() {
        config.init();
    }

    @Override
    protected void activeLoop() {
        telemetry.add("L:% R:%", config.parallelDeadwheel.getCurrentPosition(), config.perpendicularDeadwheel.getCurrentPosition());
    }
}
