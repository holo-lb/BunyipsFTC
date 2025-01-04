package au.edu.sa.mbhs.studentrobotics.cellphone.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.TrapezoidProfile;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.pid.ProfiledPIDController;
import au.edu.sa.mbhs.studentrobotics.cellphone.Cellphone;

/**
 * Fake motor (motuh)
 */
@TeleOp
@Config
public class CellphoneMotuh extends BunyipsOpMode {
    /**
     * vroom setpoint
     */
    public static int SETPOINT = 0;
    /**
     * vroom amount
     */
    public static double kP = 0.01;
    /**
     * motuh direction innit
     */
    public static DcMotorSimple.Direction DIRECTION = DcMotorSimple.Direction.FORWARD;
    /**
     * the un-guhening
     */
    public static double ACC_LP_GAIN = 0.95;
    /**
     * vroom
     */
    public static double mVelo = 15;
    /**
     * vroom vroom
     */
    public static double mAccel = 8;

    private final Cellphone phone = new Cellphone();

    @Override
    protected void onInit() {
        phone.init();
        phone.dummyMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        phone.dummyMotor.setRunToPositionController(new ProfiledPIDController(kP, 0, 0, new TrapezoidProfile.Constraints(mVelo, mAccel)));
        phone.dummyMotor.encoder.setAccelLowPassGain(ACC_LP_GAIN);
        t.setMsTransmissionInterval(10);
    }

    @Override
    protected void activeLoop() {
        phone.dummyMotor.setDirection(DIRECTION);
        phone.dummyMotor.setTargetPosition(SETPOINT);
        phone.dummyMotor.setPower(1);

        t.addData("power", phone.dummyMotor.getPower());
        t.addData("setpoint", SETPOINT);
        t.addData("pos", phone.dummyMotor.getCurrentPosition());
        t.addData("velo", phone.dummyMotor.getVelocity());
        t.addData("accel", phone.dummyMotor.getAcceleration());
    }
}
