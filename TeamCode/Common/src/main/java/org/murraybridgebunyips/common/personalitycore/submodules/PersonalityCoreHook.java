package org.murraybridgebunyips.common.personalitycore.submodules;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.murraybridgebunyips.bunyipslib.BunyipsSubsystem;

/**
 * Suspension hook for the GLaDOS/Wheatley robot.
 *
 * @author Lucas Bubner, 2023
 */
public class PersonalityCoreHook extends BunyipsSubsystem {
    private final Servo hook;

    // Assumes a scale range is being used
    private final double EXTENDED = 1.0;
    private final double UPRIGHT = 0.9;
    private final double RETRACTED = 0.0;
    private double target;

    public PersonalityCoreHook(Servo hook) {
        this.hook = hook;
        target = RETRACTED;
        update();
    }

    public PersonalityCoreHook actuateUsingController(double y) {
        target -= y / 5;
        target = Range.clip(target, 0.0, 1.0);
        return this;
    }

    public PersonalityCoreHook setPosition(double target) {
        this.target = Range.clip(target, 0.0, 1.0);
        return this;
    }

    public PersonalityCoreHook extend() {
        target = EXTENDED;
        return this;
    }

    public PersonalityCoreHook retract() {
        target = RETRACTED;
        return this;
    }

    public PersonalityCoreHook upright() {
        target = UPRIGHT;
        return this;
    }

    @Override
    public void update() {
        hook.setPosition(target);
        opMode.addTelemetry("Hook: %", target == EXTENDED ? "EXTENDED" : target == RETRACTED ? "RETRACTED" : target == UPRIGHT ? "UPRIGHT" : "CUSTOM_POS");
    }
}
