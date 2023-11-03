package org.firstinspires.ftc.teamcode.glados.components;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.GearedPivotMotor;
import org.firstinspires.ftc.teamcode.common.RobotConfig;

/**
 * FTC 15215 CENTERSTAGE 2023-2024 robot configuration
 *
 * @author Lucas Bubner, 2023
 */
public class GLaDOSConfigCore extends RobotConfig {

    // USB: Webcam "webcam"
    public WebcamName webcam;

    // Expansion 0: Front Left "fl"
    public DcMotorEx fl;

    // Expansion 1: Front Right "fr"
    public DcMotorEx fr;

    // Expansion 2: Back Right "br"
    public DcMotorEx br;

    // Expansion 3: Back Left "bl"
    public DcMotorEx bl;

    // Control 0: Suspender Actuator "sa"
    public DcMotorEx sa;

    // Control 1: Suspender Rotation "sr", 45T->90T
    public GearedPivotMotor sr;

    // Internally mounted on I2C C0 "imu"
    public IMU imu;

    protected static final double CORE_HEX_TICKS_PER_REVOLUTION = 288;
    protected static final double SR_GEAR_RATIO = 45.0 / 90.0;

    @Override
    protected void init() {
        webcam = (WebcamName) getHardware("webcam", WebcamName.class);
        fl = (DcMotorEx) getHardware("fl", DcMotorEx.class);
        fr = (DcMotorEx) getHardware("fr", DcMotorEx.class);
        br = (DcMotorEx) getHardware("br", DcMotorEx.class);
        bl = (DcMotorEx) getHardware("bl", DcMotorEx.class);
        DcMotorEx SRmotor = (DcMotorEx) getHardware("sr", DcMotorEx.class);
        if (SRmotor != null) {
            sr = new GearedPivotMotor(SRmotor, CORE_HEX_TICKS_PER_REVOLUTION, SR_GEAR_RATIO);
            sr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        sa = (DcMotorEx) getHardware("sa", DcMotorEx.class);
        imu = (IMU) getHardware("imu", IMU.class);

        if (fl != null) {
            // The forward left wheel goes the wrong way without us changing
            fl.setDirection(DcMotorSimple.Direction.REVERSE);
            fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // Explicitly set all other motors for easy debugging
        if (fr != null) {
            fr.setDirection(DcMotorSimple.Direction.FORWARD);
            fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        if (br != null) {
            br.setDirection(DcMotorSimple.Direction.FORWARD);
            br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        if (bl != null) {
            bl.setDirection(DcMotorSimple.Direction.FORWARD);
            bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        if (sa != null) {
            sa.setDirection(DcMotorSimple.Direction.FORWARD);
            sa.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        if (imu == null) {
            return;
        }

        imu.initialize(
                new IMU.Parameters(
                        new RevHubOrientationOnRobot(
                                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                        )
                )
        );
    }
}
