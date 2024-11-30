package au.edu.sa.mbhs.studentrobotics.cellphone.debug;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Minutes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsSubsystem;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.bases.Lambda;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.WaitTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.cellphone.components.CellphoneConfig;


/**
 * Scheduler testing.
 */
@TeleOp
public class CellphoneWithAnAbnormallyLongName extends CommandBasedBunyipsOpMode {
    private final CellphoneConfig config = new CellphoneConfig();
    private TestSubsystem s;

    @Override
    protected void onInitialise() {
        config.init();
        s = new TestSubsystem();
//        try (Storage.Filesystem f = Storage.filesystem()) {
//        }

//        setInitTask(new MessageTask(Seconds.of(2), "hello world"));
//        throw new YourCodeSucksException();
    }

    @Override
    protected void assignCommands() {
        always().run(() -> telemetry.add("hi"));
        // self destruct in 1 minute
        s.setDefaultTask(new WaitTask(Minutes.of(1)));
        driver().whenReleased(Controls.BACK).run(new Lambda(() -> {
        }).withName("TaskTask").withTimeout(Minutes.of(2)));
        driver().whenHeld(Controls.LEFT_STICK_BUTTON).run(() -> telemetry.add("left stick button")).in(Minutes.of(60));
    }

    private static class YourCodeSucksException extends RuntimeException {
        public YourCodeSucksException() {
            super("your code is bad and have been reported to the authority");
        }
    }

    static class TestSubsystem extends BunyipsSubsystem {
        public TestSubsystem() {
//            assertParamsNotNull((Object) null);
        }

        @Override
        protected void periodic() {

        }
    }
}
