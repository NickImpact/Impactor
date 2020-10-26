package net.impactdev.impactor.api.gui.signs;

import java.util.List;

@FunctionalInterface
public interface SignSubmission {

    boolean process(List<String> input);

}
