package com.defano.wyldcard.runtime.context;

import com.defano.wyldcard.parts.ToolEditablePart;
import io.reactivex.Observable;

import java.util.Optional;

public interface PartToolManager {
    void start();

    void setSelectedPart(ToolEditablePart part);

    void deselectAllParts();

    void bringSelectedPartCloser();

    void sendSelectedPartFurther();

    void deleteSelectedPart();

    Observable<Optional<ToolEditablePart>> getSelectedPartProvider();

    ToolEditablePart getSelectedPart();
}
