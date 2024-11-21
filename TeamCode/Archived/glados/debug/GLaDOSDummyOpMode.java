package org.murraybridgebunyips.glados.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Template (response)
 *
 * @author Lucas Bubner, 2023
 */
@TeleOp(name = "template")
@Disabled
public class GLaDOSDummyOpMode extends BunyipsOpMode {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();

    @Override
    protected void onInit() {
        config.init();
    }

    @Override
    protected void activeLoop() {

    }
}
