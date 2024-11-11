package au.edu.sa.mbhs.studentrobotics.ftc24736.joker;


import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.InchesPerSecond;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.ftc.LazyImu;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.RobotConfig;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.pid.PIDController;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.MecanumLocalizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.DriveModel;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MecanumGains;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MotionProfile;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.BlinkinLights;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.drive.MecanumDrive;

/**
 * who am i? hmmmm, hm hm hm
 * im the joker, baby! hm hm hm
 * im the, f#%@ joker, i make cool jokes, i
 * AAAAAAAAAAAAAA, AAAAAAAAAAAAAAAA
 * THE PIANO
 * IT FELL ON ME
 * AAAAAAAAAAAAAAAAAAAA
 */
@Config
public class Joker extends RobotConfig {
    /**
     * Expansion 1: front_left
     */
    public DcMotor frontLeft;
    /**
     * Expansion 2: front_right
     */
    public DcMotor frontRight;
    /**
     * Expansion 0: back_left
     */
    public DcMotor backLeft;
    /**
     * Expansion 3: back_right
     */
    public DcMotor backRight;
    /**
     * Control Hub 0: intakeMotor
     */
    public Motor intakeMotor;
    /**
     * Control Hub 1: liftMotor
     */
    public DcMotor liftMotor;

    /**
     * Control Hub 0: outtakeAlign
     */
    public Servo outtakeAlign;
    /**
     * Control Hub 1: outtakeGrip
     */
    public Servo outtakeGrip;
    /**
     * Control Hub 2: intakeGrip
     */
    public Servo intakeGrip;
    /**
     * Control Hub 3: lights
     */
    public RevBlinkinLedDriver lightsHardware;

    /**
     * Control Hub 0-1 (1 used): liftLimiter
     */
    public TouchSensor liftBotStop;
    /**
     * Control Hub 2-3 (3 used): intakeInStop
     */
    public TouchSensor intakeInStop;
    ///**
     //* Control Hub 4-5 (5 used): intakeOutStop
     //*/
    //public TouchSensor intakeOutStop;
    /**
     * Control Hub 6-7 (7 used): handoverPoint
     */
    public TouchSensor handoverPoint;
    /**
     * Control Hub USB-3.0: webcam
     */
    public WebcamName camera;
    /**
     * Internally connected
     */
    public LazyImu imu;

    /**
     * Intake Arm HoldableActuator
     */
    public HoldableActuator intake;
    /**
     * 4-Wheels MecanumDrive
     */
    public MecanumDrive drive;
    /**
     * Outtake Lift HoldableActuator
     */
    public HoldableActuator lift;
    /**
     * Light Strips BlinkinLights
     */
    public BlinkinLights lights;

    public static double INTAKE_GRIP_OPEN_POSITION = 0.5;
    public static int INTAKE_GRIP_CLOSED_POSITION = 0;

    public static int OUTTAKE_GRIP_OPEN_POSITION = 1;
    public static int OUTTAKE_GRIP_CLOSED_POSITION = 0;

    public static int OUTTAKE_ALIGN_IN_POSITION = 1;
    public static int OUTTAKE_ALIGN_OUT_POSITION = 0;

    public static double INTAKE_ARM_LOWER_POWER_CLAMP = -0.35;
    public static double INTAKE_ARM_UPPER_POWER_CLAMP = 0.35;

    public static int LIFT_LOWER_POWER_CLAMP_WHEN_NOT_HANDOVER_POINT = -1;
    public static int LIFT_UPPER_POWER_CLAMP_WHEN_NOT_HANDOVER_POINT = 1;
    public static double LIFT_LOWER_POWER_CLAMP_WHEN_HANDOVER_POINT = -0.2;
    public static double LIFT_UPPER_POWER_CLAMP_WHEN_HANDOVER_POINT = 0.2;

    private boolean intakeGripClosed = false;
    private boolean outtakeFacingOut = false;

    @Override
    protected void onRuntime() {
        frontLeft = getHardware("front_left", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        frontRight = getHardware("front_right", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        backLeft = getHardware("back_left", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        backRight = getHardware("back_right", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        intakeMotor = getHardware("intakeMotor", Motor.class, d -> {
            d.setDirection(DcMotorSimple.Direction.REVERSE);
            d.setRunToPositionController(new PIDController(0.01, 0, 0.00001));
        });
        liftMotor = getHardware("liftMotor", DcMotor.class);
        outtakeAlign = getHardware("outtakeAlign", Servo.class);
        outtakeGrip = getHardware("outtakeGrip", Servo.class, d -> d.setDirection(Servo.Direction.REVERSE));
        intakeGrip = getHardware("intakeGrip", Servo.class, d -> d.setDirection(Servo.Direction.REVERSE));
        lightsHardware = getHardware("lights", RevBlinkinLedDriver.class);
        liftBotStop = getHardware("liftLimiter", TouchSensor.class);
        intakeInStop = getHardware("intakeInStop", TouchSensor.class);
        //intakeOutStop = getHardware("intakeOutStop", TouchSensor.class);
        handoverPoint = getHardware("handoverPoint", TouchSensor.class);
        camera = getHardware("webcam", WebcamName.class);
        imu = getLazyImu(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));

        DriveModel driveModel = new DriveModel.Builder()
                .setInPerTick(123.5 / 6454.75)
                .setLateralInPerTick(125 / 5090.25)
                .setTrackWidthTicks(1562.8653888336344)
                .build();
        MotionProfile motionProfile = new MotionProfile.Builder()
                .setMaxWheelVel(InchesPerSecond.of(40))
                .setKv(0.004)
                .setKs(1.2071095031375727)
                .setKa(0.001)
                .build();
        MecanumGains mecanumGains = new MecanumGains.Builder()
                .setAxialGain(3.5)
                .setLateralGain(3.5)
                .setHeadingGain(2)
                .build();

        drive = new MecanumDrive(driveModel, motionProfile, mecanumGains, frontLeft, backLeft, backRight, frontRight, imu, hardwareMap.voltageSensor)
                .withName("drive");
        intake = new HoldableActuator(intakeMotor)
                .withBottomSwitch(intakeInStop)
                //.withTopSwitch(intakeOutStop)
                .enableUserSetpointControl((dt) -> 8)
                .withName("intake");
        MecanumLocalizer localizer = (MecanumLocalizer) drive.getLocalizer();
        localizer.leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        localizer.leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        localizer.rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        localizer.rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        lift = new HoldableActuator(liftMotor)
                .withBottomSwitch(liftBotStop)
                .map(handoverPoint, 1500)
                .withPowerClamps(LIFT_LOWER_POWER_CLAMP_WHEN_NOT_HANDOVER_POINT,
                        LIFT_UPPER_POWER_CLAMP_WHEN_NOT_HANDOVER_POINT)
                .enableUserSetpointControl((dt) -> 1800 * dt)
//                .withUpperLimit(4950)
                .withName("lift");
        lights = new BlinkinLights(lightsHardware, RevBlinkinLedDriver.BlinkinPattern.RED)
                .withName("lights");
        intakeGrip.setPosition(INTAKE_GRIP_OPEN_POSITION);
        outtakeGrip.setPosition(OUTTAKE_GRIP_CLOSED_POSITION);
    }

    public void toggleGrips() {
        if (intakeGripClosed) {
            intakeGrip.setPosition(INTAKE_GRIP_OPEN_POSITION);
            outtakeGrip.setPosition(OUTTAKE_GRIP_CLOSED_POSITION);
            intakeGripClosed = false;
        }
        else {
            intakeGrip.setPosition(INTAKE_GRIP_CLOSED_POSITION);
            outtakeGrip.setPosition(OUTTAKE_GRIP_OPEN_POSITION);
            intakeGripClosed = true;
        }
    }

    public void toggleOuttake() {
        if (outtakeFacingOut) {
            outtakeAlign.setPosition(OUTTAKE_ALIGN_IN_POSITION);
            outtakeFacingOut = false;
        }
        else {
            outtakeAlign.setPosition(OUTTAKE_ALIGN_OUT_POSITION);
            outtakeFacingOut = true;
        }
    }
}