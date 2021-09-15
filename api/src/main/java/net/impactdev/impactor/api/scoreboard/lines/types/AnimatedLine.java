package net.impactdev.impactor.api.scoreboard.lines.types;

import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.ConstantFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Represents a line on a scoreboard that is configured with an animation that'll play over time.
 *
 *
 */
public interface AnimatedLine extends ScoreboardLine, Updatable {

    CircularLinkedList<ScoreboardFrame> getFrames();

    TimeConfiguration getTimingConfig();

    int getUpdateAmount();

    interface AnimatedBuilder extends Builder<AnimatedLine, AnimatedBuilder> {

        AnimatedBuilder frame(ScoreboardFrame frame);

        AnimatedBuilder frames(Iterable<ScoreboardFrame> frames);

        AnimatedBuilder score(int score);

        /**
         * Sets the line to update after a set amount of ticks have elapsed. If this is used in conjunction
         * with the async protocol, the scheduler will apply a multiplication of 50, then assign it to a
         * unit interval of milliseconds. This is in an attempt to best replicate the actual game clock.
         *
         * @param ticks The amount of ticks
         * @return The updated builder
         */
        AnimatedBuilder interval(long ticks);

        /**
         * Sets the line to refresh/update its contents after waiting for the amount of time given by the value
         * and unit.
         *
         * @param interval The amount of time before a refresh/update in conjunction with the given unit
         * @param unit The unit indicating the measure of time for the interval
         * @return The updated builder
         */
        AnimatedBuilder interval(long interval, TimeUnit unit);

        /**
         * Indicates the number of times the active frame will update before being swapped with the next frame
         * in the queue. If the set of frames only contains 1 frame, then this value will be ignored.
         *
         * @param amount The amount of times to refresh the content.
         * @return The updated builder
         */
        AnimatedBuilder updates(int amount);

        AnimatedBuilder async();

    }

}
