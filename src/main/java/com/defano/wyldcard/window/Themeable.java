package com.defano.wyldcard.window;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.layout.StackWindow;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public interface Themeable {

    Subject<String> themeProvider = BehaviorSubject.create();

    @RunOnDispatch
    default List<Value> getThemeNames() {
        ArrayList<Value> themes = new ArrayList<>();

        for (UIManager.LookAndFeelInfo thisLaf : UIManager.getInstalledLookAndFeels()) {
            themes.add(new Value(thisLaf.getName()));
        }

        return themes;
    }

    @RunOnDispatch
    default String getThemeClassForName(String themeName) {
        for (UIManager.LookAndFeelInfo thisLaf : UIManager.getInstalledLookAndFeels()) {
            if (thisLaf.getName().equalsIgnoreCase(themeName)) {
                return thisLaf.getClassName();
            }
        }

        return null;
    }

    @RunOnDispatch
    default String getThemeNameForClass(String themeClassName) {
        for (UIManager.LookAndFeelInfo thisLaf : UIManager.getInstalledLookAndFeels()) {
            if (thisLaf.getClassName().equalsIgnoreCase(themeClassName)) {
                return thisLaf.getName();
            }
        }

        return null;
    }

    default void setTheme(String themeClassName) {
        if (themeClassName != null) {
            themeProvider.onNext(getThemeNameForClass(themeClassName));

            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(themeClassName);

                    for (WyldCardFrame thisWindow : WyldCard.getInstance().getWindowManager().getFrames(true)) {
                        thisWindow.getWindow().dispose();

                        SwingUtilities.updateComponentTreeUI(thisWindow.getWindow());
                        thisWindow.applyMenuBar();
                        thisWindow.getWindow().pack();
                        thisWindow.getWindow().validate();
                        thisWindow.getWindow().setVisible(true);

                        if (thisWindow instanceof StackWindow) {
                            ((StackWindow) thisWindow).invalidateWindowSize(new ExecutionContext());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @RunOnDispatch
    default String getCurrentThemeName() {
        return UIManager.getLookAndFeel().getName();
    }

    @RunOnDispatch
    default boolean isMacOsTheme() {
        return UIManager.getLookAndFeel().getName().equalsIgnoreCase("Mac OS X");
    }

    default Observable<String> getThemeProvider() {
        return themeProvider;
    }
}
