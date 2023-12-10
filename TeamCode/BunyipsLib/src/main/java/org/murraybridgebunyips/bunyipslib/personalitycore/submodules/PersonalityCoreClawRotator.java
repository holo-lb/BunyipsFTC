package org.murraybridgebunyips.bunyipslib.personalitycore.submodules;

import static org.murraybridgebunyips.bunyipslib.Text.round;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.murraybridgebunyips.bunyipslib.BunyipsComponent;
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;

/**
 * Rotation/claw rotational alignment for the GLaDOS/Wheatley robot
 * @author Lucas Bubner, 2023
 */
public class PersonalityCoreClawRotator extends BunyipsComponent {
    private final Servo rotator;

    // Assumes servo is programmed to 0==0 && 1=30
    private final double FACING_DOWN = 0.0;
    private final double FACING_BOARD = 1.0;
    private double target;

    public PersonalityCoreClawRotator(@NonNull BunyipsOpMode opMode, Servo rotator) {
        super(opMode);
        this.rotator = rotator;
    }

    public void faceBoard() {
        target = FACING_BOARD;
    }

    public void faceGround() {
        target = FACING_DOWN;
    }

    public void actuateUsingController(double y) {
        this.target -= y / 12;
        this.target = Range.clip(this.target, 0.0, 1.0);
    }

    public void setPosition(double target) {
        this.target = Range.clip(target, 0.0, 1.0);
    }

    public void setDegrees(double degrees) {
        target = Range.clip(degrees / 30, 0.0, 1.0);
    }

    public void update() {
        rotator.setPosition(target);
        getOpMode().addTelemetry("Claw Alignment: % pos (%)", round(target, 1), target == FACING_BOARD ? "FACING_BOARD" : target == FACING_DOWN ? "FACING_DOWN" : "CUSTOM");
    }
}
