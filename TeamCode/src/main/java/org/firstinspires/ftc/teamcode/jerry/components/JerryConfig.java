package org.firstinspires.ftc.teamcode.jerry.components;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.Deadwheel;
import org.firstinspires.ftc.teamcode.common.RobotConfig;

import java.util.ArrayList;
import java.util.Iterator;

public class JerryConfig extends RobotConfig {

    // Add declarations here
    public WebcamName webcam;
    public int monitorID;
    public DcMotorEx bl;
    public DcMotorEx br;
    public DcMotorEx fl;
    public DcMotorEx fr;
    public Deadwheel x, y;
    public Servo claw1;
    public Servo claw2;
    public DcMotorEx arm1;
    public DcMotorEx arm2;
    public BNO055IMU imu;
    public TouchSensor limit;

    public static JerryConfig newConfig(HardwareMap hardwareMap, Telemetry telemetry) {
        JerryConfig config = new JerryConfig();
        config.init(hardwareMap, telemetry);
        return config;
    }

    @Override
    protected void init(HardwareMap hardwareMap, Telemetry telemetry) {
        setTelemetry(telemetry);

        webcam = (WebcamName) getHardwareOn("Webcam", WebcamName.class);
        monitorID = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        bl = (DcMotorEx) getHardwareOn("Back Left", hardwareMap.dcMotor);
        br = (DcMotorEx) getHardwareOn("Back Right", hardwareMap.dcMotor);
        fl = (DcMotorEx) getHardwareOn("Front Left", hardwareMap.dcMotor);
        fr = (DcMotorEx) getHardwareOn("Front Right", hardwareMap.dcMotor);
        arm1 = (DcMotorEx) getHardwareOn("Arm Motor 1", hardwareMap.dcMotor);
        arm2 = (DcMotorEx) getHardwareOn("Arm Motor 2", hardwareMap.dcMotor);
        claw1 = (Servo) getHardwareOn("Claw Servo 1", hardwareMap.servo);
        claw2 = (Servo) getHardwareOn("Claw Servo 2", hardwareMap.servo);
        limit = (TouchSensor) getHardwareOn("Arm Stop", hardwareMap.touchSensor);

        // Motor direction configuration
        fl.setDirection(DcMotorEx.Direction.REVERSE);
        fr.setDirection(DcMotorEx.Direction.FORWARD);
        bl.setDirection(DcMotorEx.Direction.FORWARD);
        br.setDirection(DcMotorEx.Direction.REVERSE);

        // Encoder configuration (Using modified DcMotor classes with built-in distance calculations)
        // These encoders will mirror a DcMotor, but will be attached to their own port (for example,
        // motor 0 and 1 on Expansion Hub, but without any power connection)
        x = (Deadwheel) getHardwareOn("X Encoder", Deadwheel.class);
        y = (Deadwheel) getHardwareOn("Y Encoder", Deadwheel.class);


        // Control Hub IMU configuration
        // This uses the legacy methods for IMU initialisation, this should be refactored and updated
        // at some point in time. (23 Nov 2022)
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = (BNO055IMU) getHardwareOn("imu", BNO055IMU.class);
        if (imu != null)
            imu.initialize(parameters);

        ArrayList<String> errors = getHardwareErrors();
        if (errors == null) {
            telemetry.addData("BunyipsOpMode Status", "ROBOT CONFIGURATION COMPLETED SUCCESSFULLY WITH NO ERRORS.");
            return;
        }

        telemetry.addData("BunyipsOpMode Status", "ERROR(S) DURING CONFIGURATION, THESE DEVICES WERE NOT ABLE TO BE CONFIGURED.");
        Iterator<String> error = errors.iterator();
        for (int i = 0; i < errors.size(); i++) {
            telemetry.addData(String.valueOf(i), error.next());
        }
    }
}
