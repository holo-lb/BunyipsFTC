package org.murraybridgebunyips.glados.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.EncoderTicks;
import org.murraybridgebunyips.bunyipslib.Motor;
import org.murraybridgebunyips.bunyipslib.external.ArmController;
import org.murraybridgebunyips.bunyipslib.external.ff.ArmFeedforward;
import org.murraybridgebunyips.bunyipslib.external.pid.PIDController;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Experimental class to test new Motor class and position.
 */
@TeleOp
@Config
@SuppressWarnings("MissingJavadoc")
public class GLaDOSArmControllerTuning extends BunyipsOpMode {
    public static double kP = 0.02;
    public static double kI;
    public static double kD = 0.00012;
    public static double kS;
    public static double kCos = 0.1;
    public static double kV = 0.00001;
    public static double kA;

    public static int SET_POINT_TICKS;

    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private ArmController controller;
    private Motor motor;

    @Override
    protected void onInit() {
        config.init();
        motor = new Motor(config.arm);
        EncoderTicks.Generator g = EncoderTicks.createGenerator(config.arm, (int) motor.getMotorType().getTicksPerRev(), 1);
        controller = new ArmController(new PIDController(kP, kI, kD), new ArmFeedforward(kS, kCos, kV, kA), () -> g.angle(SET_POINT_TICKS), g::getAngularVelocity, g::getAngularAcceleration);
        motor.setRunToPositionController(controller);
        motor.resetEncoder();
    }

    @Override
    protected void activeLoop() {
        controller.setCoefficients(kP, kI, kD, kS, kCos, kV, kA);
        motor.setTargetPosition(SET_POINT_TICKS);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(1);
        telemetry.addDashboard("setpoint", SET_POINT_TICKS);
        telemetry.addDashboard("current", motor.getCurrentPosition());
        if (gamepad1.b) exit();
    }
}
