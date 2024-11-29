package au.edu.sa.mbhs.studentrobotics.ftc22407.vance;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.ftc.LazyImu;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.RobotConfig;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.CompositeController;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.ff.ElevatorFeedforward;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.pid.PController;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.pid.PIDController;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.ThreeWheelLocalizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.accumulators.PeriodicIMUAccumulator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.DriveModel;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MecanumGains;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MotionProfile;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.DualServos;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.Switch;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.drive.MecanumDrive;

/**
 * FTC 22407 INTO THE DEEP 2024-2025 robot configuration and subsystems
 *
 * @author Lachlan Paul, 2024
 */
@Config
public class Vance extends RobotConfig {
    /**
     * Vertical arm kP
     */
    public static double va_kP = 0.015;
    /**
     * Vertical arm kG
     */
    public static double va_kG = 0.3;
    /**
     * Vertical arm TPS
     */
    public static double va_TPS = 400;

    /**
     * Vance hardware
     */
    public final Hardware hw = new Hardware();

    /**
     * Mecanum drive
     */
    public MecanumDrive drive;
    /**
     * Vertical up arm
     */
    public HoldableActuator verticalLift;
    /**
     * Horizontal forward arm
     */
    public HoldableActuator horizontalLift;
    /**
     * Claw rotation
     */
    public Switch clawRotator;
    /**
     * Basket rotation
     */
    public Switch basketRotator;
    /**
     * Scoring element claws
     */
    public DualServos claws;
//    /**
//     * Lights
//     */
//    public BlinkinLights lights;

    @Override
    protected void onRuntime() {
        hw.fl = getHardware("fl", DcMotorEx.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.REVERSE);
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        });
        hw.bl = getHardware("bl", DcMotorEx.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.REVERSE);
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        });
        hw.fr = getHardware("fr", DcMotorEx.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.FORWARD);
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        });
        hw.br = getHardware("br", DcMotorEx.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.FORWARD);
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        });
        hw.imu = getLazyImu(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));

        hw.dwleft = getHardware("br", RawEncoder.class, (d) -> d.setDirection(DcMotorSimple.Direction.FORWARD));
        hw.dwright = getHardware("fl", RawEncoder.class, (d) -> d.setDirection(DcMotorSimple.Direction.FORWARD));
        hw.dwx = getHardware("bl", RawEncoder.class, (d) -> d.setDirection(DcMotorSimple.Direction.REVERSE));

        hw.verticalLift = getHardware("va", Motor.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.REVERSE);
            PIDController pid = new PController(va_kP);
            ElevatorFeedforward ff = new ElevatorFeedforward(0.0, va_kG, 0.0, 0.0, () -> 0, () -> 0);
            CompositeController c = pid.compose(ff, Double::sum);
            d.setRunToPositionController(c);
            BunyipsOpMode.ifRunning(o -> o.onActiveLoop(() -> c.setCoefficients(va_kP, 0.0, 0.0, 0.0, 0.0, va_kG, 0.0, 0.0)));
        });
        hw.bottomLimit = getHardware("bottom", TouchSensor.class);
        hw.horizontalLimit = getHardware("hori", TouchSensor.class);
        hw.horizontalLift = getHardware("ha", DcMotorEx.class, (d) -> d.setDirection(DcMotorSimple.Direction.REVERSE));

        hw.leftClaw = getHardware("lc", Servo.class, (d) -> {
            d.setDirection(Servo.Direction.REVERSE);
            d.scaleRange(0.5, 1.0);
        });
        hw.rightClaw = getHardware("rc", Servo.class, (d) -> d.scaleRange(0.0, 0.5));

        hw.clawRotator = getHardware("cr", Servo.class, (d) -> d.setDirection(Servo.Direction.REVERSE));
        hw.basketRotator = getHardware("bk", Servo.class, (d) -> {
            d.setDirection(Servo.Direction.FORWARD);
            d.scaleRange(0.2, 0.5);
        });

//        hw.lights = getHardware("lights", RevBlinkinLedDriver.class);

        DriveModel driveModel = new DriveModel.Builder()
                .setInPerTick(122.5 / 61697.0)
                .setLateralInPerTick(0.001498916323279902)
                .setTrackWidthTicks(7670.3069265030135)
                .build();
        MotionProfile motionProfile = new MotionProfile.Builder()
                .setKv(0.00035)
                .setKs(1)
                .setKa(0.00007)
                .build();
        MecanumGains mecanumGains = new MecanumGains.Builder()
                .setAxialGain(2)
                .setLateralGain(2)
                .setHeadingGain(4)
                .build();
        ThreeWheelLocalizer.Params localiserParams = new ThreeWheelLocalizer.Params.Builder()
                .setPar0YTicks(-1274.4310945248199)
                .setPar1YTicks(1355.6339929262751)
                .setPerpXTicks(-3361.673151430961)
                .build();

//        aprilTag = new AprilTag(b -> AprilTag.setCameraPose(b)
//                .backward(Inches.of(10))
//                .yaw(Degrees.of(180))
//                .up(Inches.of(3))
//                .apply());
        drive = new MecanumDrive(driveModel, motionProfile, mecanumGains, hw.fl, hw.bl, hw.br, hw.fr, hw.imu, hardwareMap.voltageSensor)
                .withLocalizer(new ThreeWheelLocalizer(driveModel, localiserParams, hw.dwleft, hw.dwright, hw.dwx))
                .withAccumulator(new PeriodicIMUAccumulator(hw.imu.get(), Seconds.of(5)))
                .withName("Drive");
        verticalLift = new HoldableActuator(hw.verticalLift)
                .withBottomSwitch(hw.bottomLimit)
//                .enableUserSetpointControl((dt) -> dt * va_TPS)
                .withTolerance(10, true)
                .withUpperLimit(900)
                .withHomingPower(0.7)
                .withName("Vertical Arm");
        horizontalLift = new HoldableActuator(hw.horizontalLift)
                .withPowerClamps(-0.5, 0.5)
                .withTolerance(10, true)
                .withBottomSwitch(hw.horizontalLimit)
                .withTolerance(7, true)
                .withName("Horizontal Arm");
        clawRotator = new Switch(hw.clawRotator, 1, 0)
                .withName("Claw Rotator");
        basketRotator = new Switch(hw.basketRotator, 1, 0)
                .withName("Basket Rotator");
        claws = new DualServos(hw.leftClaw, hw.rightClaw);
//        lights = new BlinkinLights(hw.lights, RevBlinkinLedDriver.BlinkinPattern.LAWN_GREEN);
    }

    /**
     * Definition of all hardware on Vance.
     */
    public static class Hardware {
        /**
         * Internally mounted on I2C C0 "imu"
         */
        public LazyImu imu;

        /**
         * Control 0: fr
         */
        public DcMotorEx /*Are you*/ fr /*Or jk*/;

        /**
         * Control 1: fl
         */
        public DcMotorEx fl;

        /**
         * Control 2: bl
         */
        public DcMotorEx bl;

        /**
         * Control 3: br
         */
        public DcMotorEx br;

        /**
         * Control 3: br
         */
        public RawEncoder dwleft;

        /**
         * Control 0: fr
         */
        public RawEncoder dwright;

        /**
         * Control 1: fl
         */
        public RawEncoder dwx;

        /**
         * Expansion 1: va
         */
        public Motor verticalLift;

        /**
         * Expansion 0: ha
         */
        public DcMotorEx horizontalLift;

        /**
         * Control Servo 2: lc
         */
        public Servo leftClaw;

        /**
         * Control Servo 1: rc
         */
        public Servo rightClaw;

        /**
         * Control Servo 0: cr
         */
        public Servo clawRotator;

        /**
         * Control Servo 3: bk
         */
        public Servo basketRotator;

//        /**
//         * Control Servo 5: Blinkin Lights "lights"
//         */
//        public RevBlinkinLedDriver lights;

        /**
         * Control Digital 1: Limit Switch "bottom" for vertical arm
         */
        public TouchSensor bottomLimit;

        /**
         * hori: Limit switch "horizontal" for horizontal arm
         */
        public TouchSensor horizontalLimit;
    }
}
