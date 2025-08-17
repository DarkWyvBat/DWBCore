package net.darkwyvbat.dwbcore.util.time;

import java.util.function.Consumer;

public record TimelineEvent<T>(int triggerTime, Consumer<T> action) {
}
