package org.murraybridgebunyips.glados.components;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.DegreesPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Inches;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.InchesPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Millimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Second;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.murraybridgebunyips.bunyipslib.Dbg;
import org.murraybridgebunyips.bunyipslib.EncoderTicks;
import org.murraybridgebunyips.bunyipslib.Motor;
import org.murraybridgebunyips.bunyipslib.RobotConfig;
import org.murraybridgebunyips.bunyipslib.external.ArmController;
import org.murraybridgebunyips.bunyipslib.external.ff.ArmFeedforward;
import org.murraybridgebunyips.bunyipslib.external.pid.PIDController;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.DriveConstants;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.MecanumCoefficients;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.localizers.TwoWheelLocalizer;
import org.murraybridgebunyips.bunyipslib.roadrunner.util.Deadwheel;

/**
 * FTC 15215 CENTERSTAGE 2023-2024 robot configuration
 *
 * @author Lucas Bubner, 2024
 */
public class GLaDOSConfigCore extends RobotConfig {
    /**
     * USB: Webcam "webcam"
     */
    public WebcamName webcam;
    /**
     * Expansion 0: Front Left "fl"
     */
    public Motor frontLeft;
    /**
     * Expansion 1: Front Right "fr"
     */
    public Motor frontRight;
    /**
     * Expansion 2: Back Right "br"
     */
    public Motor backRight;
    /**
     * Expansion 3: Back Left "bl"
     */
    public Motor backLeft;
    /**
     * Control 3: Parallel Encoder "pe"
     */
    public Deadwheel parallelDeadwheel;
    /**
     * Control 2: Perpendicular Encoder "ppe"
     */
    public Deadwheel perpendicularDeadwheel;
    /**
     * Control 0: Arm "arm"
     */
    public Motor arm;
    /**
     * Control Servo 1: Left Servo "ls"
     */
    public Servo leftPixel;
    /**
     * Control Servo 2: Right Servo "rs"
     */
    public Servo rightPixel;
    /**
     * Control Servo 0: Plane Launcher "pl"
     */
    public Servo launcher;
    /**
     * Control 1: Suspender Actuator "sa"
     */
    public Motor suspenderActuator;
    /**
     * Control Digital 1: Touch Sensor/Limit Switch "bottom"
     */
    public TouchSensor bottomLimit;
    /**
     * Internally mounted on I2C C0 "imu"
     */
    public IMU imu;

    /**
     * RoadRunner drive constants
     */
    public DriveConstants driveConstants;
    /**
     * Dual deadwheel intrinsics
     */
    public TwoWheelLocalizer.Coefficients localizerCoefficients;
    /**
     * Mecanum coefficients
     */
    public MecanumCoefficients mecanumCoefficients;
    /**
     * AprilTagPoseEstimator robot to camera offset (inch, rad)
     */
    public Pose2d robotCameraOffset = new Pose2d(9, -1, Math.toRadians(-10));

    @Override
    protected void onRuntime() {
        // Sensors
        webcam = getHardware("webcam", WebcamName.class);
        imu = getHardware("imu", IMU.class, (d) -> {
            boolean init = d.initialize(new IMU.Parameters(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                    )
            ));
            if (!init) Dbg.error("imu failed init");
        });

        // Mecanum system
        frontLeft = getHardware("fl", Motor.class, (d) -> d.setDirection(DcMotorSimple.Direction.FORWARD));
        frontRight = getHardware("fr", Motor.class, (d) -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        backRight = getHardware("br", Motor.class, (d) -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        backLeft = getHardware("bl", Motor.class, (d) -> d.setDirection(DcMotorSimple.Direction.REVERSE));

        parallelDeadwheel = getHardware("pe", Deadwheel.class,
                (d) -> d.setDirection(Deadwheel.Direction.FORWARD));
        perpendicularDeadwheel = getHardware("ppe", Deadwheel.class,
                (d) -> d.setDirection(Deadwheel.Direction.FORWARD));

        // Pixel manipulation system
        arm = getHardware("arm", Motor.class, (d) -> {
            EncoderTicks.Generator gen = EncoderTicks.createGenerator(d, (int) d.getMotorType().getTicksPerRev(), 1);
            ArmController controller = new ArmController(
                    new PIDController(0.02, 0, 0.00012),
                    new ArmFeedforward(0, 0.1, 0.0001, 0),
                    () -> gen.angle(d.getTargetPosition()), gen::getAngularVelocity, gen::getAngularAcceleration
            );
            controller.getPIDFController().setTolerance(5);
            d.setRunToPositionController(controller);
        });

        double LIM = 0.7;
        leftPixel = getHardware("ls", Servo.class, (d) -> d.scaleRange(LIM, 1.0));
        rightPixel = getHardware("rs", Servo.class, (d) -> d.scaleRange(0.0, 1 - LIM));

        // Paper Drone launcher system
        launcher = getHardware("pl", Servo.class, (d) -> d.setDirection(Servo.Direction.REVERSE));

        // Suspension system
        suspenderActuator = getHardware("sa", Motor.class, (d) ->
                d.setRunToPositionController(new PIDController(0.0001, 0, 0)));
        bottomLimit = getHardware("bottom", TouchSensor.class);

        // RoadRunner configuration
        driveConstants = new DriveConstants.Builder()
                .setTicksPerRev(28)
                .setMaxRPM(6000)
                .setRunUsingEncoder(false)
                .setWheelRadius(Millimeters.of(75).divide(2))
                .setGearRatio(1.0 / 13.1)
                .setTrackWidth(Inches.of(20.5))
                // ((MAX_RPM / 60) * GEAR_RATIO * WHEEL_RADIUS * 2 * Math.PI) * 0.85
                .setMaxVel(InchesPerSecond.of(41.065033847087705))
                .setMaxAccel(InchesPerSecond.per(Second).of(41.065033847087705))
                .setMaxAngVel(DegreesPerSecond.of(175))
                .setMaxAngAccel(DegreesPerSecond.per(Second).of(175))
                .setKV(0.01395)
                .setKStatic(0.06311)
                .setKA(0.0015)
                .build();

        localizerCoefficients = new TwoWheelLocalizer.Coefficients.Builder()
                .setTicksPerRev(1200)
                .setGearRatio(1)
                .setWheelRadius(Millimeters.of(50).divide(2))
                .setXMultiplier(100.0 / 134.5)
                .setYMultiplier(100.0 / 134.5)
                .setParallelX(Inches.of(2))
                .setParallelY(Inches.zero())
                .setPerpendicularX(Inches.of(6.5))
                .setPerpendicularY(Inches.of(2.5))
                .build();

        mecanumCoefficients = new MecanumCoefficients.Builder()
                .setLateralMultiplier(60.0 / 59.846666)
                .setTranslationalPID(new PIDCoefficients(8, 0, 0))
                .setHeadingPID(new PIDCoefficients(10, 0, 0))
                .build();
    }
}
