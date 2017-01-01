package hypercard.gui.util;

public class SingleSelectionGroup<T> {

    private final Iterable<T> group;
    private final SelectFunction selectFunction;
    private final DeselectFunction deselectFunction;

    public interface SelectFunction<T> {
        void onSelect(T item);
    }

    public interface DeselectFunction<T> {
        void onDeselect(T item);
    }

    public SingleSelectionGroup(Iterable<T> group, SelectFunction<T> selectFunction, DeselectFunction<T> deselectFunction) {
        this.group = group;
        this.selectFunction = selectFunction;
        this.deselectFunction = deselectFunction;
    }

    public void select(T item) {
        for (T thisItem : group) {
            deselectFunction.onDeselect(thisItem);
        }

        selectFunction.onSelect(item);
    }

}
