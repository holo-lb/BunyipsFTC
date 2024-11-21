package org.murraybridgebunyips.wheatley.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * Template (response)
 */
@TeleOp(name = "")
@Disabled
public class WheatleyDummy extends BunyipsOpMode {
    private final WheatleyConfig config = new WheatleyConfig();

    @Override
    protected void onInit() {
        config.init();
    }

    @Override
    protected void activeLoop() {

    }
}