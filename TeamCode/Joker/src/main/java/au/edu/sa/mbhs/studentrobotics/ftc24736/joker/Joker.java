package au.edu.sa.mbhs.studentrobotics.ftc24736.joker;


import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.InchesPerSecond;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.ftc.LazyImu;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.EncoderTicks;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.RobotConfig;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.CompositeController;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.ff.ArmFeedforward;
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
     * Control Hub 2: hook
     */
    public DcMotor hook;
    /**
     * Control Hub 3: arm
     */
    public DcMotor ascentMotor;

    /**
     * Control Hub 0: spintake
     */
    public CRServo spintake;
    /**
     * Control Hub 1: outtakeGrip
     */
    public Servo outtakeGrip;
    /**
     * Control Hub 2: lights
     */
    public RevBlinkinLedDriver lightsHardware;
    //**
    //* Control Hub 2: intakeGrip
    //*/
    //public Servo intakeGrip;

    /**
     * Control Hub 0-1 (1 used): liftLimiter
     */
    public TouchSensor liftBotStop;
    /**
     * Control Hub 2-3 (3 used): intakeInStop
     */
    public TouchSensor intakeInStop;
    /**
     * Control Hub 4-5 (5 used): intakeOutStop
     */
    public TouchSensor intakeOutStop;
    //**
     //* Control Hub 6-7 (7 used): handoverPoint
     //*/
    //public TouchSensor handoverPoint;

    /**
     * Internally connected
     */
    public LazyImu imu;

    /**
     * 4-Wheels MecanumDrive
     */
    public MecanumDrive drive;

    /**
     * Intake Arm HoldableActuator
     */
    public HoldableActuator intake;
    /**
     * Outtake Lift HoldableActuator
     */
    public HoldableActuator lift;
    /**
     * Ascent Arm HoldableActuator
     */
    public HoldableActuator ascentArm;

    /**
     * Light Strips BlinkinLights
     */
    public BlinkinLights lights;

    public static double INTAKE_GRIP_OPEN_POSITION = 0.5;
    public static int INTAKE_GRIP_CLOSED_POSITION = 0;

    public static int OUTTAKE_GRIP_OPEN_POSITION = 1;
    public static int OUTTAKE_GRIP_CLOSED_POSITION = 0;

    //public static int OUTTAKE_ALIGN_IN_POSITION = 1;
    //public static int OUTTAKE_ALIGN_OUT_POSITION = 0;

    public static double INTAKE_ARM_LOWER_POWER_CLAMP = -0.35;
    public static double INTAKE_ARM_UPPER_POWER_CLAMP = 0.35;

    public static int LIFT_LOWER_POWER_CLAMP = -1;
    public static int LIFT_UPPER_POWER_CLAMP = 1;
    //public static double LIFT_LOWER_POWER_CLAMP_WHEN_HANDOVER_POINT = -0.2;
    //public static double LIFT_UPPER_POWER_CLAMP_WHEN_HANDOVER_POINT = 0.2;

    //private boolean intakeGripClosed = false;
    private boolean outtakeGripClosed = false;
    //private boolean outtakeFacingOut = false;

    //live mecanum wheel rolling on keyboard reaction:
    //Zzzzzzzzzzzzzzzzzzzzzzzzzzzssxccfvgbhnjk,l.....;///'/'

    @Override
    protected void onRuntime() {
        frontLeft = getHardware("front_left", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        frontRight = getHardware("front_right", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        backLeft = getHardware("back_left", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        backRight = getHardware("back_right", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));

        intakeMotor = getHardware("intakeMotor", Motor.class, d -> {
            EncoderTicks.Generator angleGen = EncoderTicks.createGenerator(d, 0.333);
            PIDController pid = new PIDController(0.005, 0, 0.00001);
            ArmFeedforward ff = new ArmFeedforward(0, 0.1, 0, 0, angleGen::getAngle, angleGen::getAngularVelocity, angleGen::getAngularAcceleration);
            CompositeController c = new CompositeController(pid, ff, Double::sum);
            d.setRunToPositionController(c);
        });
        liftMotor = getHardware("liftMotor", DcMotor.class, d -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        hook = getHardware("hook", DcMotor.class);
        ascentMotor = getHardware("arm", Motor.class,
                d -> d.setRunToPositionController(new PIDController(0.01, 0, 0.00001)));

        //outtakeAlign = getHardware("outtakeAlign", Servo.class);
        spintake = getHardware("spintake", CRServo.class);
        outtakeGrip = getHardware("outtakeGrip", Servo.class, d -> d.setDirection(Servo.Direction.REVERSE));
        //intakeGrip = getHardware("intakeGrip", Servo.class, d -> d.setDirection(Servo.Direction.REVERSE));
        lightsHardware = getHardware("lights", RevBlinkinLedDriver.class);

        liftBotStop = getHardware("liftLimiter", TouchSensor.class);
        intakeInStop = getHardware("intakeInStop", TouchSensor.class);
        intakeOutStop = getHardware("intakeOutStop", TouchSensor.class);
        //handoverPoint = getHardware("handoverPoint", TouchSensor.class);

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

        /*
        NEW VALUES IN CASE OF ABILITY TO RETUNE ALL AUTONOMOUSes
        DriveModel driveModel = new DriveModel.Builder()
                .setInPerTick((95.5 - 0.787402) / 5155.25) //RE-DONE and result of calculation be 0.0187354924
                .setLateralInPerTick((95.5 - 0.787402) / 5200) //RE-DONE and result of calculation be
                .setTrackWidthTicks(1564.368988990854) //RE-DONE
                .build();
        MotionProfile motionProfile = new MotionProfile.Builder()
                .setMaxWheelVel(InchesPerSecond.of(40))
                .setKv(0.003819584225840457) //RE-DONE
                .setKs(1.4223271129208594) //RE-DONE
                .setKa(0.001) //RE-DONE
                .build();
        MecanumGains mecanumGains = new MecanumGains.Builder()
                .setAxialGain(3.5)
                .setLateralGain(3.5)
                .setHeadingGain(2)
                .build();
        */

        drive = new MecanumDrive(driveModel, motionProfile, mecanumGains, frontLeft, backLeft, backRight, frontRight, imu, hardwareMap.voltageSensor)
                .withName("drive");

        MecanumLocalizer localizer = (MecanumLocalizer) drive.getLocalizer();
        localizer.leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        localizer.leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        localizer.rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        localizer.rightFront.setDirection(DcMotorSimple.Direction.REVERSE);

        intake = new HoldableActuator(intakeMotor)
                .withBottomSwitch(intakeInStop)
                .withTopSwitch(intakeOutStop)
                .withUserSetpointControl((dt) -> 300 * dt)
                .withName("intake");

        lift = new HoldableActuator(liftMotor)
                .withBottomSwitch(liftBotStop)
                //.map(handoverPoint, 1500)
                .withPowerClamps(LIFT_LOWER_POWER_CLAMP,
                        LIFT_UPPER_POWER_CLAMP)
                .withUpperLimit(6000)
                .withUserSetpointControl((dt) -> 1800 * dt)
                .withName("lift");

        //can be replaced w/ pid controller if hook motor gets an encoder (not really needed though)
        hook.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        ascentArm = new HoldableActuator(ascentMotor)
                .withName("ascent");

        lights = new BlinkinLights(lightsHardware, RevBlinkinLedDriver.BlinkinPattern.LAWN_GREEN)
                .withName("lights");

        //intakeGrip.setPosition(INTAKE_GRIP_OPEN_POSITION);
        outtakeGrip.setPosition(OUTTAKE_GRIP_OPEN_POSITION);
    }

    public void toggleOuttakeGrip() {
        if (outtakeGripClosed) {
            outtakeGrip.setPosition(OUTTAKE_GRIP_OPEN_POSITION);
            outtakeGripClosed = false;
        }
        else {
            outtakeGrip.setPosition(OUTTAKE_GRIP_CLOSED_POSITION);
            outtakeGripClosed = true;
        }
    }

    /*
    public void toggleOuttake() {
        if (outtakeFacingOut) {
            outtakeAlign.setPosition(OUTTAKE_ALIGN_IN_POSITION);
            outtakeFacingOut = false;
        }
        else {
            outtakeAlign.setPosition(OUTTAKE_ALIGN_OUT_POSITION);
            outtakeFacingOut = true;
        }
    */
    }