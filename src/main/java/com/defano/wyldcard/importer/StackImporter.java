package com.defano.wyldcard.importer;

import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.layouts.StackImportProgress;

import java.awt.*;
import java.io.File;

public class StackImporter {

    public static void importStack(ExecutionContext context) {
        importStack(context, findFile(context));
    }

    private static void importStack(ExecutionContext context, File stackFile) {

        // Nothing to do when file isn't specified (user cancelled file selection, for example)
        if (stackFile == null) {
            return;
        }

        StackImportProgress progressWindow = new StackImportProgress();
        new WindowBuilder<>(progressWindow)
                .withTitle("Importing HyperCard Stack")
                .withLocationCenteredOnScreen()
                .alwaysOnTop()
                .build();

        StackFormatConverter.convert(stackFile, new ConversionStatusObserver() {
            @Override
            public void onConversionFailed(String message, Exception cause) {
                Invoke.onDispatch(() -> {
                    progressWindow.dispose();
                    if (!progressWindow.isCancelled()) {
                        WyldCard.getInstance().showErrorDialog(new HtException(message));
                        cause.printStackTrace();
                    }
                });
            }
            @Override
            public void onConversionSucceeded(StackModel importedStack) {
                progressWindow.setWaitingForStackToOpen();

                Invoke.onDispatch(() -> {
                    if (!progressWindow.isCancelled()) {
                        WyldCard.getInstance().getStackManager().openStack(context, importedStack, true);
                    }
                    progressWindow.dispose();
                });
            }
        }, progressWindow);
    }

    private static File findFile(ExecutionContext context) {
        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow(), "Choose stack to import", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            return fd.getFiles()[0];
        }

        return null;
    }
}
