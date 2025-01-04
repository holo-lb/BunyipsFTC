package au.edu.sa.mbhs.studentrobotics.cellphone;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.acmerobotics.roadrunner.ProfileParams;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.TimeTrajectory;
import com.acmerobotics.roadrunner.TimeTurn;
import com.acmerobotics.roadrunner.TrajectoryBuilderParams;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.TurnConstraints;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.Vector2dDual;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.Arrays;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.RobotConfig;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.Mathf;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.Localizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.accumulators.Accumulator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.RoadRunnerDrive;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.Constants;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.DriveModel;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MotionProfile;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.util.Geometry;

/**
 * Cellphone
 */
@RobotConfig.AutoInit
public class Cellphone extends RobotConfig {
    /**
     * Auto initialised instance
     */
    public static final Cellphone instance = new Cellphone();

    /**
     * Back camera
     */
    public CameraName cameraB;
    /**
     * Front camera
     */
    public CameraName cameraF;
    /**
     * Dummy motor, not a real device
     */
    public Motor dummyMotor;
    /**
     * Dummy drive, not a real drive
     */
    public RoadRunnerDrive dummyDrive;

    @Override
    protected void onRuntime() {
        cameraB = ClassFactory.getInstance().getCameraManager().nameFromCameraDirection(BuiltinCameraDirection.BACK);
        cameraF = ClassFactory.getInstance().getCameraManager().nameFromCameraDirection(BuiltinCameraDirection.FRONT);

        DummyMotor m = new DummyMotor();
        dummyMotor = new Motor(new DcMotorImplEx(m, -1));

        dummyDrive = new DummyHDrive();

        BunyipsOpMode.ifRunning(o -> o.onActiveLoop(m::update));
    }

    private static class DummyHDrive implements RoadRunnerDrive {
        private PoseVelocity2d target = Geometry.zeroVel();

        private Localizer localizer = new Localizer() {
            private Rotation2d lastHeading;
            @NonNull
            @Override
            public Twist2dDual<Time> update() {
                lastHeading = accumulator.getPose().heading;
                return new Twist2dDual<>(
                        Vector2dDual.constant(target.linearVel.times(30 * BunyipsOpMode.getInstance().timer.deltaTime().in(Seconds)), 2),
                        DualNum.constant(accumulator.getPose().heading.minus(lastHeading) + target.angVel * 3 * BunyipsOpMode.getInstance().timer.deltaTime().in(Seconds), 2)
                );
            }
        };

        private Accumulator accumulator = new Accumulator();

        private class Turn implements Action {
            private final TimeTurn t;
            private ElapsedTime timer;

            public Turn(TimeTurn timeTurn) {
                t = timeTurn;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (timer == null)
                    timer = new ElapsedTime();
                if (timer.seconds() >= t.duration)
                    return false;
                Pose2dDual<Time> txWorldTarget = t.get(timer.seconds());
                setPose(txWorldTarget.value());
                periodic();
                return true;
            }
        }

        private class Trajectory implements Action {
            private final TimeTrajectory t;
            private ElapsedTime timer;

            public Trajectory(TimeTrajectory timeTrajectory) {
                t = timeTrajectory;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (timer == null)
                    timer = new ElapsedTime();
                if (timer.seconds() >= t.duration)
                    return false;
                Pose2dDual<Time> txWorldTarget = t.get(timer.seconds());
                setPose(txWorldTarget.value());
                periodic();
                return true;
            }
        }

        @NonNull
        @Override
        public Constants getConstants() {
            return new Constants(
                    new DriveModel.Builder().build(),
                    new MotionProfile.Builder().build(),
                    Turn::new,
                    Trajectory::new,
                    new TrajectoryBuilderParams(
                            1.0e-6,
                            new ProfileParams(
                                    0.25, 0.1, 1.0e-2
                            )
                    ),
                    0,
                    new TurnConstraints(3, -3, 3),
                    new MinVelConstraint(Arrays.asList(new TranslationalVelConstraint(30), new AngularVelConstraint(3))),
                    new ProfileAccelConstraint(-30, 30)
            );
        }

        @Override
        public void periodic() {
            accumulator.accumulate(localizer.update());
        }

        @NonNull
        @Override
        public RoadRunnerDrive withLocalizer(@NonNull Localizer localizer) {
            this.localizer = localizer;
            return this;
        }

        @NonNull
        @Override
        public Localizer getLocalizer() {
            return localizer;
        }

        @NonNull
        @Override
        public RoadRunnerDrive withAccumulator(@NonNull Accumulator accumulator) {
            this.accumulator = accumulator;
            return this;
        }

        @NonNull
        @Override
        public Accumulator getAccumulator() {
            return accumulator;
        }

        @Override
        public void setPower(@NonNull PoseVelocity2d target) {
            this.target = target;
        }

        @Nullable
        @Override
        public Pose2d getPose() {
            return accumulator.getPose();
        }

        @Override
        public void setPose(@NonNull Pose2d newPose) {
            accumulator.setPose(newPose);
        }

        @Nullable
        @Override
        public PoseVelocity2d getVelocity() {
            return accumulator.getVelocity();
        }
    }

    @SuppressWarnings({"all", "deprecation"})
    private static class DummyMotor implements DcMotorControllerEx {
        private final ElapsedTime timer = new ElapsedTime();
        private final int TPS = 600;
        private final int ACCEL = 1200;
        private double power;
        private double pos;
        private int target;
        private MotorConfigurationType conf = MotorConfigurationType.getUnspecifiedMotorType();
        private DcMotor.RunMode mode;
        private double velo;

        public void update() {
            // update this dummy motor by feeding power into pos, delta time approach
            velo = Mathf.moveTowards(velo, power * TPS, ACCEL * timer.seconds());
            pos += velo * timer.seconds();
            timer.reset();
        }

        @Override
        public void setMotorType(int motor, MotorConfigurationType motorType) {
            conf = motorType;
        }

        @Override
        public MotorConfigurationType getMotorType(int motor) {
            return conf;
        }

        @Override
        public void setMotorMode(int motor, DcMotor.RunMode mode) {
            this.mode = mode;
        }

        @Override
        public DcMotor.RunMode getMotorMode(int motor) {
            return mode;
        }

        @Override
        public void setMotorPower(int motor, double power) {
            this.power = power;
        }

        @Override
        public double getMotorPower(int motor) {
            return power;
        }

        @Override
        public boolean isBusy(int motor) {
            return false;
        }

        @Override
        public void setMotorZeroPowerBehavior(int motor, DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        }

        @Override
        public DcMotor.ZeroPowerBehavior getMotorZeroPowerBehavior(int motor) {
            return DcMotor.ZeroPowerBehavior.BRAKE;
        }

        @Override
        public boolean getMotorPowerFloat(int motor) {
            return false;
        }

        @Override
        public void setMotorTargetPosition(int motor, int position) {
            target = position;
        }

        @Override
        public int getMotorTargetPosition(int motor) {
            return target;
        }

        @Override
        public int getMotorCurrentPosition(int motor) {
            return (int) pos;
        }

        @Override
        public void resetDeviceConfigurationForOpMode(int motor) {

        }

        @Override
        public Manufacturer getManufacturer() {
            return null;
        }

        @Override
        public String getDeviceName() {
            return "Dummy";
        }

        @Override
        public String getConnectionInfo() {
            return "";
        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public void resetDeviceConfigurationForOpMode() {

        }

        @Override
        public void close() {

        }

        @Override
        public void setMotorEnable(int motor) {

        }

        @Override
        public void setMotorDisable(int motor) {

        }

        @Override
        public boolean isMotorEnabled(int motor) {
            return true;
        }

        @Override
        public void setMotorVelocity(int motor, double ticksPerSecond) {
            pos += ticksPerSecond * timer.seconds();
            timer.reset();
        }

        @Override
        public void setMotorVelocity(int motor, double angularRate, AngleUnit unit) {

        }

        @Override
        public double getMotorVelocity(int motor) {
            return velo;
        }

        @Override
        public double getMotorVelocity(int motor, AngleUnit unit) {
            return velo;
        }

        @Override
        public void setPIDCoefficients(int motor, DcMotor.RunMode mode, PIDCoefficients pidCoefficients) {

        }

        @Override
        public void setPIDFCoefficients(int motor, DcMotor.RunMode mode, PIDFCoefficients pidfCoefficients) throws UnsupportedOperationException {

        }

        @Override
        public PIDCoefficients getPIDCoefficients(int motor, DcMotor.RunMode mode) {
            return null;
        }

        @Override
        public PIDFCoefficients getPIDFCoefficients(int motor, DcMotor.RunMode mode) {
            return null;
        }

        @Override
        public void setMotorTargetPosition(int motor, int position, int tolerance) {
            target = position;
        }

        @Override
        public double getMotorCurrent(int motor, CurrentUnit unit) {
            return 0;
        }

        @Override
        public double getMotorCurrentAlert(int motor, CurrentUnit unit) {
            return 0;
        }

        @Override
        public void setMotorCurrentAlert(int motor, double current, CurrentUnit unit) {

        }

        @Override
        public boolean isMotorOverCurrent(int motor) {
            return false;
        }
    }
}
