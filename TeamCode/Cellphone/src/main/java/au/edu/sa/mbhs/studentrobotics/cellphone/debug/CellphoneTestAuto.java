package au.edu.sa.mbhs.studentrobotics.cellphone.debug;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Centimeters;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueRight;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redLeft;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redRight;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration;

/**
 * Test batch of 100 WaitTasks.
 */
@Autonomous
public class CellphoneTestAuto extends AutonomousBunyipsOpMode {
    @Override
    protected void onInitialise() {
//        setOpModes("Test Auto", "Test Auto 2", "3,", "4", "5", "6", "7", "8", "9", "10", "18", "19", "20");
        setOpModes(
                redRight()
                        .tile(2.5),
                redLeft()
                        .translate(Centimeters.of(15)),
                blueRight().tile(4)
                        .backward(Centimeters.of(10))
                        .rotate(Degrees.of(45))
        );
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {
        assert selectedOpMode != null;
        telemetry.log(((StartingConfiguration.Position) selectedOpMode.require()).toFieldPose());
//        for (int i = 0; i < 100; i++) {
//            addTask(new WaitTask(Milliseconds.of(100)).withName("WaitTask no. " + i));
//        }
//        addTask(new ParallelTaskGroup(
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000)),
//                new WaitTask(Milliseconds.of(20000))
//        ));
    }
}
