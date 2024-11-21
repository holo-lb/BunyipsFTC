package org.murraybridgebunyips.wheatley.debug;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.subsystems.BlinkinLights;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * Blinkin Lights test
 */
@TeleOp
public class WheatleyBlinkinTest extends CommandBasedBunyipsOpMode {
    private final WheatleyConfig config = new WheatleyConfig();

    private BlinkinLights lights;

    @Override
    protected void onInitialise() {
        config.init();
        lights = new BlinkinLights(config.lights, RevBlinkinLedDriver.BlinkinPattern.AQUA);
    }

    @Override
    protected void assignCommands() {
        driver().whenPressed(Controls.A)
                .run(lights.tasks.setPatternFor(Seconds.of(5), RevBlinkinLedDriver.BlinkinPattern.RAINBOW_PARTY_PALETTE));
        driver().whenPressed(Controls.B)
                .run(lights::turnOff);
        driver().whenPressed(Controls.Y)
                .run(() -> lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.SINELON_RAINBOW_PALETTE));
        driver().whenPressed(Controls.X)
                .run(lights::resetPattern);
    }
}
