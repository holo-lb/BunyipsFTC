package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.debug;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Milliseconds;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.TrapezoidProfile;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.ProfiledServo;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;

/**
 * BunyipsLib test for ProfiledServo
 *
 * @author Lucas Bubner, 2024
 */
@Config
@TeleOp
public class VanceTestPF extends BunyipsOpMode {
    /**
     * velocity
     */
    public static double vel;
    /**
     * acceleration
     */
    public static double acc;
    /**
     * target
     */
    public static double target;
    /**
     * Delta threshold
     */
    public static double delta;
    /**
     * update delta time
     */
    public static double dtMs;

    private final Vance robot = new Vance();
    private ProfiledServo pf;

    @Override
    protected void onInit() {
        robot.init();
        pf = new ProfiledServo(robot.hw.clawRotator);
    }

    @Override
    protected void activeLoop() {
        pf.setConstraints(new TrapezoidProfile.Constraints(vel, acc));
        pf.setPositionDeltaThreshold(delta);
        pf.setPositionRefreshRate(Milliseconds.of(dtMs));
        pf.setPosition(target);
        t.addData("goal", target);
        t.addData("command", pf.getPosition());
    }
}
