package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.debug;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.MecanumLocalizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.DriveModel;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.Vision;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.AprilTag;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;

@TeleOp(name = "DriveTest")
public class DriveTest extends BunyipsOpMode {
    private final Joker robot = new Joker();
    private Vision webcam;

    @Override
    protected void onInit() {
        robot.init();
        //robot.outtakeAlign.setPosition(Joker.OUTTAKE_ALIGN_IN_POSITION);
        DriveModel dm = new DriveModel.Builder()

                .build();
        robot.drive
                .withLocalizer(new MecanumLocalizer(dm, robot.frontLeft, robot.backLeft, robot.backRight, robot.frontRight, robot.imu.get()));
        webcam = new Vision(robot.camera);
        AprilTag at = new AprilTag();
        webcam.init(at);
        webcam.start(at);
        webcam.startPreview();
//        AprilTagPoseEstimator.enable(at, localizer)
//                .setCameraOffset(new Pose2d(8.5, 0, 0))
//                .setHeadingEstimate(false)
//                .setKalmanGains(4, 0.1);
        robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.LAWN_GREEN);
    }

    @Override
    protected void activeLoop() {
        double leftStickX = gamepad1.left_stick_x;
        double leftStickY = gamepad1.left_stick_y;
        double rightStickX = gamepad1.right_stick_x;
        robot.drive.setPower(Controls.vel(leftStickX, leftStickY, rightStickX));
        robot.drive.update();

        telemetry.addData("pose", robot.drive.getPose());
    }
}