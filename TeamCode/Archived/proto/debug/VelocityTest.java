package org.murraybridgebunyips.proto.debug;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Dbg;
import org.murraybridgebunyips.bunyipslib.EncoderTicks;
import org.murraybridgebunyips.bunyipslib.Motor;
import org.murraybridgebunyips.bunyipslib.external.PIDFFController;
import org.murraybridgebunyips.bunyipslib.external.ff.SimpleMotorFeedforward;
import org.murraybridgebunyips.bunyipslib.external.pid.PIDController;
import org.murraybridgebunyips.proto.Proto;

/**
 * BunyipsLib velocity test
 */
@Config
@TeleOp
@Disabled
public class VelocityTest extends BunyipsOpMode {
    /**
     * Velocity target (deg/s)
     */
    public static double DEG_PER_SEC = 0;

    private final Proto robot = new Proto();
    private int tpr;
    private Motor motor;

    @Override
    protected void onInit() {
        robot.init();
        motor = new Motor(hardwareMap.dcMotor.get("test"));
        motor.setRunUsingEncoderController(
                new PIDFFController(
                        new PIDController(0.2, 0, 0),
                        new SimpleMotorFeedforward(0, 0, 0), motor.getEncoder()
                ),
                1,
                motor.getMotorType().getTicksPerRev()
        );
        MotorConfigurationType mConf = motor.getMotorType();
        tpr = (int) mConf.getTicksPerRev();
        Dbg.log("[Motor Intrinsics] TPR:%, MAX_TPS:%, MAX_RPM_BUFF:%", tpr, mConf.getAchieveableMaxTicksPerSecond(), mConf.getAchieveableMaxRPMFraction());
    }

    @Override
    protected void activeLoop() {
        motor.setVelocity(DEG_PER_SEC, AngleUnit.DEGREES);
        telemetry.addData("angVel",
                EncoderTicks
                        .toAngle((int) Math.round(motor.getVelocity()), tpr, 1)
                        .in(Degrees)
        );
        telemetry.addData("angVelTarget", DEG_PER_SEC);
        telemetry.addData("tickVel", motor.getVelocity());
        telemetry.addData("tickVelTarget", EncoderTicks.fromAngle(Degrees.of(DEG_PER_SEC), tpr, 1));
    }
}
