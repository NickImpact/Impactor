package net.impactdev.impactor.api.gui.signs;

import com.flowpowered.math.vector.Vector3d;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.utilities.Builder;

import java.util.List;

public interface SignQuery<T, P, M> {

    String TEXT_FORMAT = "{\"text\":\"%s\"}";
    int action = 9;

    List<T> getText();

    M getSignPosition();

    boolean shouldReopenOnFailure();

    SignSubmission getSubmissionHandler();

    void sendTo(P player);

    @SuppressWarnings("unchecked")
    static <T, P, M> SignQueryBuilder<T, P, M> builder() {
        return Impactor.getInstance().getRegistry().createBuilder(SignQueryBuilder.class);
    }

    interface SignQueryBuilder<T, P, M> extends Builder<SignQuery<T, P, M>, SignQueryBuilder<T, P, M>> {

        SignQueryBuilder<T, P, M> text(List<T> text);

        SignQueryBuilder<T, P, M> position(M position);

        SignQueryBuilder<T, P, M> reopenOnFailure(boolean state);

        SignQueryBuilder<T, P, M> response(SignSubmission response);

    }

}
