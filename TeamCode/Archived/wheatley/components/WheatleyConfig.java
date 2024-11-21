package org.murraybridgebunyips.wheatley.components;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.DegreesPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Inches;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.InchesPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Millimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Second;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.murraybridgebunyips.bunyipslib.Dbg;
import org.murraybridgebunyips.bunyipslib.RobotConfig;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.DriveConstants;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.MecanumCoefficients;

/**
 * Wheatley robot configuration and hardware declarations
 *
 * @author Lucas Bubner, 2024
 * @author Lachlan Paul, 2024
 */

public class WheatleyConfig extends RobotConfig {
    /**
     * USB: Webcam "webcam"
     */
    public WebcamName webcam;

    /**
     * Internally mounted on I2C C0 "imu"
     */
    public IMU imu;

    /**
     * Expansion 0: bl
     */
    public DcMotorEx bl;

    /**
     * Expansion 1: fl
     */
    public DcMotorEx fl;

    /**
     * Expansion 2: fr
     */
    public DcMotorEx /*Are you*/ fr /*Or jk*/;

    /**
     * Expansion 3: br
     */
    public DcMotorEx br;

    /**
     * Control 0: Linear Actuator "la"
     */
    public DcMotorEx linearActuator;

    /**
     * Control 1: Claw Rotator "cr"
     */
    public DcMotorEx clawRotator;

    /**
     * Control Servo 0: Left Servo "ls"
     */
    public Servo leftPixel;

    /**
     * Control Servo 1: Plane Launcher "pl"
     */
    public Servo launcher;

    /**
     * Control Digital 0: Touch Sensor/Limit Switch "bottom"
     */
    public TouchSensor bottomLimit;

//    /**
//     * Control Digital 1: Touch Sensor/Limit Switch "top"
//     */
//    public TouchSensor topLimit;

    /**
     * Control Servo 5: Right Servo "rs"
     */
    public Servo rightPixel;

    /**
     * Control Servo ?: Blinkin Driver
     */
    public RevBlinkinLedDriver lights;

    /**
     * RoadRunner drive constants
     */
    public DriveConstants driveConstants;
    /**
     * RoadRunner Mecanum coefficients
     */
    public MecanumCoefficients mecanumCoefficients;

    @Override
    protected void onRuntime() {
        webcam = getHardware("webcam", WebcamName.class);

        // Motor directions configured to work with current config
        fl = getHardware("fl", DcMotorEx.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.REVERSE);
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        });
        bl = getHardware("bl", DcMotorEx.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.REVERSE);
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        });
        fr = getHardware("fr", DcMotorEx.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.REVERSE);
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        });
        br = getHardware("br", DcMotorEx.class, (d) -> {
            d.setDirection(DcMotorSimple.Direction.REVERSE);
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        });
        imu = getHardware("imu", IMU.class, (d) -> {
            boolean res = d.initialize(
                    new IMU.Parameters(
                            new RevHubOrientationOnRobot(
                                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
                                    RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                            )
                    )
            );
            if (!res) {
                Dbg.error("IMU failed to initialise!");
            }
        });

        // Suspender upward motion system
        linearActuator = getHardware("la", DcMotorEx.class, (d) -> {
            d.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            d.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, new PIDFCoefficients(0.8, 0, 1000000000000000000));
//            d.setDirection(DcMotorSimple.Direction.REVERSE);
        });
        bottomLimit = getHardware("bottom", TouchSensor.class);
//        topLimit = getHardware("top", TouchSensor.class);

        // Pixel manipulation system
        clawRotator = getHardware("cr", DcMotorEx.class);

        leftPixel = getHardware("ls", Servo.class, (d) -> d.scaleRange(0.2, 1.0));
        rightPixel = getHardware("rs", Servo.class);

        // Paper Drone launcher system
        launcher = getHardware("pl", Servo.class);

        // Fancy lights
        lights = getHardware("lights", RevBlinkinLedDriver.class);

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
                .setKV(0.0145)
                .setKStatic(0.06422)
                .setKA(0.001)
                .build();

        mecanumCoefficients = new MecanumCoefficients.Builder()
                .setLateralMultiplier(60.0 / 54.07)
                .setTranslationalPID(new PIDCoefficients(8, 0, 0))
                .setHeadingPID(new PIDCoefficients(10, 0, 0))
                .build();
    }
}
