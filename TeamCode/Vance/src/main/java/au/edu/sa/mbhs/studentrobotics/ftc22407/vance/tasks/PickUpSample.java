package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Milliseconds;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.DualServos;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.SequentialTaskGroup;

/**
 * Task to pick up sample that is directly in front of robot.
 * Made for Auto.
 *
 * @author Lachlan Paul, 2024
 */
public class PickUpSample extends SequentialTaskGroup {
    @SuppressWarnings("MissingJavadoc")  // you can figure it out
    public PickUpSample(HoldableActuator horizontalArm, DualServos claws, Integer targetPos) {
        super(
                new ParallelTaskGroup(/*horizontalArm.tasks.home().timeout(Milliseconds.of(1500)),*/ claws.tasks.openBoth()),
                horizontalArm.tasks.goTo(targetPos).timeout(Milliseconds.of(1200)),
                claws.tasks.closeBoth()
        );
    }
}
