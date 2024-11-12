package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoControllerEx;

import java.lang.reflect.Field;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Dbg;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.Mathf;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;

/**
 * Blinkin lights debuggin
 */
@TeleOp
@Config
public class VanceBlinkin extends BunyipsOpMode {
    /**
     * selected pattern to respect
     */
    public static RevBlinkinLedDriver.BlinkinPattern pattern = RevBlinkinLedDriver.BlinkinPattern.LAWN_GREEN;
    /**
     * light voltage
     */
    public static Voltage voltage = Voltage.STRIP_12V;

    private final Vance robot = new Vance();

    private ServoControllerEx blinkinController;
    private int port;

    @Override
    protected void onInit() {
        robot.init();
        Field blinkinServoControllerField;
        Field blinkinPortField;
        try {
            blinkinServoControllerField = robot.hw.lights.getClass().getDeclaredField("controller");
            blinkinServoControllerField.setAccessible(true);
            blinkinPortField = robot.hw.lights.getClass().getDeclaredField("port");
            blinkinPortField.setAccessible(true);
            port = blinkinPortField.getInt(robot.hw.lights);
            blinkinController = (ServoControllerEx) blinkinServoControllerField.get(robot.hw.lights);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPwmPulse(double us) {
        PwmControl.PwmRange range = blinkinController.getServoPwmRange(port);
        // usPulseLower is at servo position 0
        // usPulseUpper is at servo position 1
        double position = (us - range.usPulseLower) / (range.usPulseUpper - range.usPulseLower);
        blinkinController.setServoPosition(port, Mathf.clamp(position, 0, 1));
    }

    @Override
    protected void activeLoop() {
        if (gamepad1.getDebounced(Controls.A)) {
            Dbg.log("sending voltage reset command for " + voltage);
            sendPwmPulse(voltage.pwmCommandUs);
            robot.hw.lights.setPattern(pattern.next());
        }
        robot.hw.lights.setPattern(pattern);
    }

    /**
     * REV Robotics Blinkin LED Driver voltage options.
     */
    public enum Voltage {
        /**
         * 5V mode
         */
        STRIP_5V(2125),
        /**
         * 12V mode
         */
        STRIP_12V(2145);

        /**
         * Command code to switch the strip mode to this voltage (provided by REV, in microseconds).
         */
        public final int pwmCommandUs;

        Voltage(int us) {
            pwmCommandUs = us;
        }
    }
}
