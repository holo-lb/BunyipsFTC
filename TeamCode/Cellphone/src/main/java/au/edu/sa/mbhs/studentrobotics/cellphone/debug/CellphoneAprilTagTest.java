package au.edu.sa.mbhs.studentrobotics.cellphone.debug;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Second;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Vector2dDual;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.Localizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.NullLocalizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.accumulators.Accumulator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.accumulators.AprilTagRelocalizingAccumulator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.util.Dashboard;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.util.Geometry;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.Vision;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.AprilTag;
import au.edu.sa.mbhs.studentrobotics.cellphone.components.CellphoneConfig;

/**
 * test for AprilTagPoseEstimator sanity checking
 */
@TeleOp
public class CellphoneAprilTagTest extends BunyipsOpMode {
    private final CellphoneConfig config = new CellphoneConfig();

    private final Localizer l = new Localizer() {
        @NonNull
        @Override
        public Twist2dDual<Time> update() {
            return new Twist2dDual<>(Vector2dDual.constant(new Vector2d(12 * timer.deltaTime().in(Second), 12 * timer.deltaTime().in(Second)), 2),
                    DualNum.constant(timer.deltaTime().in(Second), 2));
        }
    };
//    private final Localizer l = new NullLocalizer();
    private Accumulator a;

    @Override
    protected void onInit() {
        config.init();
        Vision vision = new Vision(config.cameraB);
        AprilTagMetadata meta = new AprilTagMetadata(14, "test", 1.5, new VectorF(0, 0, 0), DistanceUnit.INCH,
                new QuaternionMaker(0, 0, 0).make());
        AprilTag aprilTag = new AprilTag((b) -> {
//            b.setTagLibrary(new AprilTagLibrary.Builder().setAllowOverwrite(true).addTags(AprilTagGameDatabase.getCurrentGameTagLibrary()).addTag(meta).build());
            AprilTag.setCameraPose(b)
                    .forward(Inches.of(9))
//                    .left(Inches.of(9))
//                    .yaw(Degrees.of(90))
                    .apply();
            return b;
        });
        vision.init(aprilTag).start(aprilTag);
        vision.startPreview();
        a = new AprilTagRelocalizingAccumulator(aprilTag);
    }

    @Override
    protected void activeLoop() {
        a.accumulate(l.update());
        telemetry.add("pose: %", Geometry.toUserString(a.getPose()));
        telemetry.dashboardFieldOverlay().setStroke("#FF0000");
        telemetry.dashboardFieldOverlay().strokeCircle(a.getPose().position.x, a.getPose().position.y, 1)
                .strokeCircle(0,0,24);
        Dashboard.drawRobot(telemetry.dashboardFieldOverlay(), a.getPose());
    }

    static class QuaternionMaker {
        public double w, x, y, z;

        public QuaternionMaker(double rollDegrees, double pitchDegrees, double yawDegrees) {
            double roll = Math.toRadians(rollDegrees);
            double pitch = Math.toRadians(pitchDegrees);
            double yaw = Math.toRadians(yawDegrees);

            double cy = Math.cos(yaw * 0.5);
            double sy = Math.sin(yaw * 0.5);
            double cp = Math.cos(pitch * 0.5);
            double sp = Math.sin(pitch * 0.5);
            double cr = Math.cos(roll * 0.5);
            double sr = Math.sin(roll * 0.5);

            w = cr * cp * cy + sr * sp * sy;
            x = sr * cp * cy - cr * sp * sy;
            y = cr * sp * cy + sr * cp * sy;
            z = cr * cp * sy - sr * sp * cy;
        }

        public Quaternion make() {
            return new Quaternion((float) w, (float) x, (float) y, (float) z, 0);
        }
    }
}
