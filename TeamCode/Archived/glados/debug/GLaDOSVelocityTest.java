package org.murraybridgebunyips.glados.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Motor;
import org.murraybridgebunyips.bunyipslib.external.PIDFFController;
import org.murraybridgebunyips.bunyipslib.external.ff.SimpleMotorFeedforward;
import org.murraybridgebunyips.bunyipslib.external.pid.PIDController;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Velocity controller testing
 */
@TeleOp
@Config
@SuppressWarnings("MissingJavadoc")
public class GLaDOSVelocityTest extends BunyipsOpMode {
    public static int TARGET_VELO_TPS = 0;
    public static double BUFFER_FRACTION = 1;
    public static int kP;
    public static int kI;
    public static int kD;
    public static int kS;
    public static int kV;
    public static int kA;

    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private Motor motor;

    @Override
    protected void onInit() {
        config.init();
        motor = config.arm;
        motor.setRunUsingEncoderController(new PIDFFController(
                new PIDController(kP, kI, kD),
                new SimpleMotorFeedforward(kS, kV, kA),
                motor.getEncoder()
        ), BUFFER_FRACTION, motor.getMotorType().getAchieveableMaxTicksPerSecond());
    }

    @Override
    protected void activeLoop() {
        assert motor.getRunUsingEncoderController().isPresent();
        motor.getRunUsingEncoderController().get().setCoefficients(kP, kI, kD, kS, kV, kA);

        motor.setVelocity(TARGET_VELO_TPS);

        telemetry.addDashboard("currentVelo", motor.getVelocity());
        telemetry.addDashboard("targetVelo", TARGET_VELO_TPS);
    }
}
