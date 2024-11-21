package org.murraybridgebunyips.glados.debug;

import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Arm testing
 */
@TeleOp
public class GLaDOSArmTest extends AutonomousBunyipsOpMode {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();

    private HoldableActuator a;

    @Override
    protected void onInitialise() {
        config.init();
        a = new HoldableActuator(config.arm);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        for (int i = 0; i < 10; i++) {
            addTask(a.tasks.delta(i % 2 == 0 ? 1000 : -1000));
        }
    }

    @Override
    protected void periodic() {
        telemetry.addDashboard("current", config.arm.getCurrentPosition());
        telemetry.addDashboard("target", config.arm.getTargetPosition());
    }
}
