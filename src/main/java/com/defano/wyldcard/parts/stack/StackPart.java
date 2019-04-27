package com.defano.wyldcard.parts.stack;

import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.NavigationManager;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.fx.CurtainManager;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.parts.Part;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.builder.CardModelBuilder;
import com.defano.wyldcard.parts.builder.StackModelBuilder;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.window.layouts.StackWindow;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import java.awt.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the controller object of the stack itself. See {@link StackModel} for the data model.
 * <p>
 * This controller is "virtual" because a stack has no view of its own, aside from the card that is currently displayed
 * in it. Thus, this class has no associated Swing component and cannot be added to a view hierarchy.
 */
public class StackPart implements Part<StackModel>, PropertyChangeObserver {

    private final NavigationManager navigationManager = WyldCard.getInstance().getNavigationManager();

    private final StackModel stackModel;
    private final CurtainManager curtainManager = new CurtainManager();
    private final Set<StackObserver> stackObservers = new HashSet<>();
    private final Set<StackNavigationObserver> stackNavigationObservers = new HashSet<>();
    private final Subject<Integer> cardCountProvider = BehaviorSubject.createDefault(0);
    private final Subject<Optional<CardPart>> cardClipboardProvider = BehaviorSubject.createDefault(Optional.empty());
    private CardPart currentCard;

    private StackPart(StackModel stackModel) {
        this.stackModel = stackModel;
    }

    public static StackPart newStack(ExecutionContext context) {
        return fromStackModel(context, new StackModelBuilder()
                .withName("Untitled")
                .withInitialCard()
                .build());
    }

    public static StackPart fromStackModel(ExecutionContext context, StackModel model) {
        StackPart stackPart = new StackPart(model);

        stackPart.cardCountProvider.onNext(model.getCardCount());
        stackPart.stackModel.addPropertyChangedObserver(stackPart);
        stackPart.currentCard = CardPart.fromPositionInStack(context, model.getCurrentCardIndex(), model);

        return stackPart;
    }

    /**
     * "Opens" this stack inside of a given window.
     * <p>
     * Sets up connections between the window controller and stack controller, displays the stack's current card and
     * sends the 'openStack' and 'openCard' message to the stack.
     *
     * @param stackWindow The window to bind this stack to
     */
    public void bindToWindow(StackWindow stackWindow) {

        // Make the window aware of us
        ExecutionContext context = new ExecutionContext(this);
        stackWindow.bindModel(this);

        // Display the current card
        navigationManager.goCard(context, this, stackModel.getCurrentCardIndex(), false);

        // Resize the window to fit this stack
        fireOnCardDimensionChanged(stackModel.getDimension(context));
    }

    /**
     * Gets the data model associated with this stack.
     *
     * @return The stack model.
     */
    public StackModel getStackModel() {
        return stackModel;
    }

    /**
     * Deletes the current card provided there are more than one card in the stack.
     *
     * @param context The execution context.
     */
    @RunOnDispatch
    public void deleteCard(ExecutionContext context) {
        if (canDeleteCard(context)) {
            WyldCard.getInstance().getToolsManager().setIsEditingBackground(false);

            int deletedCardIndex = stackModel.getCurrentCardIndex();
            CardModel deletedCardModel = stackModel.getCardModel(deletedCardIndex);

            deletedCardModel.receiveMessage(new ExecutionContext(deletedCardModel), SystemMessage.DELETE_CARD);

            if (deletedCardModel.getBackgroundModel().getCardModels().size() == 1) {
                deletedCardModel.receiveMessage(new ExecutionContext(deletedCardModel), SystemMessage.DELETE_BACKGROUND);
            }

            stackModel.deleteCurrentCard();
            cardCountProvider.onNext(stackModel.getCardCount());
            fireOnCardOrderChanged();

            openCard(context, deletedCardIndex == 0 ? 0 : deletedCardIndex - 1, deletedCardModel);
        } else {
            WyldCard.getInstance().showErrorDialog(new HtSemanticException("This card cannot be deleted because it or its background is marked as \"Can't Delete\"."));
        }
    }

    /**
     * Creates a new card with a new background. Differs from {@link #newCard(ExecutionContext)} in that {@link #newCard(ExecutionContext)} creates a
     * new card with the same background as the current card.
     *
     * @param context The execution context.
     * @return The newly created card.
     */
    @RunOnDispatch
    public CardPart newBackground(ExecutionContext context) {
        WyldCard.getInstance().getToolsManager().setIsEditingBackground(false);

        insertCard(new CardModelBuilder(getStackModel())
                .withId(getStackModel().getNextCardId())
                .withBackgroundId(getStackModel().newBackground())
                .build());

        cardCountProvider.onNext(stackModel.getCardCount());
        fireOnCardOrderChanged();

        return navigationManager.goNextCard(context, this);
    }

    /**
     * Creates a new card with the same background as the current card. See {@link #newBackground(ExecutionContext)} to create a new
     * card with a new background.
     *
     * @param context The execution context.
     * @return The newly created card.
     */
    @RunOnDispatch
    public CardPart newCard(ExecutionContext context) {
        WyldCard.getInstance().getToolsManager().setIsEditingBackground(false);

        insertCard(new CardModelBuilder(getStackModel())
                .withId(getStackModel().getNextCardId())
                .withBackgroundId(currentCard.getPartModel().getBackgroundId())
                .build());

        cardCountProvider.onNext(stackModel.getCardCount());
        fireOnCardOrderChanged();

        return navigationManager.goNextCard(context, this);
    }

    /**
     * Removes the current card from the stack and places it into the card clipboard (for pasting elsewhere in the
     * stack).
     *
     * @param context The execution context.
     */
    @RunOnDispatch
    public void cutCard(ExecutionContext context) {
        cardClipboardProvider.onNext(Optional.of(getDisplayedCard()));
        cardCountProvider.onNext(stackModel.getCardCount());

        deleteCard(context);
    }

    /**
     * Copies the displayed card to the card clipboard for pasting elsewhere in the stack.
     */
    @RunOnDispatch
    public void copyCard() {
        cardClipboardProvider.onNext(Optional.of(getDisplayedCard()));
    }

    /**
     * Adds the card presently held in the card clipboard to the stack in the current card's position. Has no affect
     * if the clipboard is empty.
     *
     * @param context The execution context.
     */
    @RunOnDispatch
    public void pasteCard(ExecutionContext context) {
        if (cardClipboardProvider.blockingFirst().isPresent()) {
            WyldCard.getInstance().getToolsManager().setIsEditingBackground(false);

            CardModel card = cardClipboardProvider.blockingFirst().get().getPartModel().copyOf();
            card.relinkParentPartModel(getStackModel());
            card.define(CardModel.PROP_ID).asConstant(new Value(getStackModel().getNextCardId()));

            insertCard(card);
            cardCountProvider.onNext(stackModel.getCardCount());
            fireOnCardOrderChanged();

            navigationManager.goNextCard(context, this);
        }
    }

    /**
     * Gets an observable object containing the contents of the card clipboard.
     *
     * @return The card clipboard provider.
     */
    public Observable<Optional<CardPart>> getCardClipboardProvider() {
        return cardClipboardProvider;
    }

    /**
     * Gets the currently displayed card.
     *
     * @return The current card
     */
    public CardPart getDisplayedCard() {
        return currentCard;
    }

    /**
     * Invalidates the card cache; useful only if modifying this stack's underlying stack model (i.e., as a
     * result of card sorting or re-ordering).
     *
     * @param context   The execution context.
     * @param cardIndex - The index of the card in the stack to transition to after invalidating the cache.
     */
    @RunOnDispatch
    public void invalidateCache(ExecutionContext context, int cardIndex) {
        this.currentCard.partClosed(context);
        this.currentCard = loadCard(context, getStackModel().getCurrentCardIndex());

        this.cardCountProvider.onNext(stackModel.getCardCount());

        fireOnCardOrderChanged();
        navigationManager.goCard(context, this, cardIndex, false);
    }

    /**
     * Gets an observable object containing the number of card in the stack.
     *
     * @return The card count provider
     */
    public Observable<Integer> getCardCountProvider() {
        return cardCountProvider;
    }

    /**
     * Adds an observer of stack changes.
     *
     * @param observer The observer
     */
    public void addObserver(StackObserver observer) {
        stackObservers.add(observer);
    }

    /**
     * Removes an observer of stack changes.
     *
     * @param observer The observer
     */
    public void removeObserver(StackObserver observer) {
        stackObservers.remove(observer);
    }

    /**
     * Adds an observer of stack navigation changes (i.e., user changed cards)
     *
     * @param observer The observer
     */
    public void addNavigationObserver(StackNavigationObserver observer) {
        stackNavigationObservers.add(observer);
    }

    /**
     * Removes an observer of stack navigation changes.
     *
     * @param observer The observer to remove
     */
    public void removeNavigationObserver(StackNavigationObserver observer) {
        stackNavigationObservers.remove(observer);
    }

    public CurtainManager getCurtainManager() {
        return curtainManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case StackModel.PROP_NAME:
                fireOnStackNameChanged(newValue.toString());
                break;

            case StackModel.PROP_HEIGHT:
            case StackModel.PROP_WIDTH:
                // Resize the window
                fireOnCardDimensionChanged(getStackModel().getDimension(context));

                // Re-load the card model into the size
                closeCard(context, getDisplayedCard().getPartModel());
                openCard(context, stackModel.getCurrentCardIndex(), getDisplayedCard().getPartModel());
                break;

            case StackModel.PROP_RESIZABLE:
                WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).setAllowResizing(newValue.booleanValue());
                break;
        }
    }

    /**
     * Loads the requested card in the stack, allowing the card to initialize itself before being returned. Note that
     * this only creates the card controller object (it does not display it in the stack window); use
     * {@link com.defano.wyldcard.NavigationManager} to navigate to and display the card.
     *
     * @param context   The execution context
     * @param cardIndex The index of the card in this stack (0-based) to load.
     * @return The loaded and initialized card
     */
    @RunOnDispatch
    public CardPart loadCard(ExecutionContext context, int cardIndex) {
        try {
            CardPart card = CardPart.fromPositionInStack(context, cardIndex, stackModel);
            card.partOpened(context);
            return card;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load card.", e);
        }
    }

    /**
     * Closes the current card by deactivating paint tools on its canvas and notifying observers that the card is being
     * closed.
     *
     * @param context The execution context
     */
    public void closeCard(ExecutionContext context, CardModel newCard) {
        Invoke.onDispatch(() -> {
            CardPart displayedCard = getDisplayedCard();

            // Deactivate paint tool before doing anything (to commit in-fight changes)
            WyldCard.getInstance().getToolsManager().getPaintTool().deactivate();

            // Stop editing background when card changes
            WyldCard.getInstance().getToolsManager().setIsEditingBackground(false);

            // Close the currently displayed card
            displayedCard.partClosed(context);

            // Send 'closeBackground' message as needed
            if (newCard == null || (newCard.getBackgroundModel() != displayedCard.getPartModel().getBackgroundModel())) {
                displayedCard.getPartModel().receiveMessage(new ExecutionContext(displayedCard), SystemMessage.CLOSE_BACKGROUND);
            }
        });
    }

    /**
     * Loads and activates the identified card in the stack (that is, the requested card becomes the card that the user
     * is currently interacting with).
     *
     * @param context   The execution context
     * @param cardIndex The index (0-based) of the card to activate in this stack
     * @return The activated card
     */
    @RunOnDispatch
    public CardPart openCard(ExecutionContext context, int cardIndex, CardModel oldCard) {

        try {
            // Change card
            stackModel.setCurrentCardIndex(cardIndex);
            currentCard = loadCard(context, cardIndex);

            // Notify observers of new card
            fireOnCardOpened(oldCard, currentCard);

            // Send 'openBackground' message as needed
            if (oldCard == null || (currentCard.getPartModel().getBackgroundModel() != oldCard.getBackgroundModel())) {
                currentCard.getPartModel().receiveMessage(new ExecutionContext(currentCard), SystemMessage.OPEN_BACKGROUND);
            }

            // Reactivate paint tool on new card's canvas
            WyldCard.getInstance().getToolsManager().reactivateTool(currentCard.getActiveCanvas());

            return currentCard;

        } catch (Exception e) {
            throw new RuntimeException("Failed to activate card.", e);
        }
    }

    private void insertCard(CardModel cardModel) {
        getStackModel().addCard(cardModel, getStackModel().getCurrentCardIndex() + 1);
        getStackModel().receiveMessage(new ExecutionContext(), SystemMessage.NEW_CARD);
    }

    private boolean canDeleteCard(ExecutionContext context) {
        long cardCountInBackground = stackModel.getCardsInBackground(getDisplayedCard().getPartModel().getBackgroundId()).size();

        return stackModel.getCardCount() > 1 &&
                !getDisplayedCard().getPartModel().get(context, CardModel.PROP_CANTDELETE).booleanValue() &&
                (cardCountInBackground > 1 || !getDisplayedCard().getPartModel().getBackgroundModel().get(context, BackgroundModel.PROP_CANTDELETE).booleanValue());
    }

    private void fireOnStackClosed() {
        Invoke.onDispatch(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onStackClosed(StackPart.this);
            }
        });
    }

    private void fireOnStackOpened() {
        Invoke.onDispatch(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onStackOpened(StackPart.this);
            }
        });
    }

    private void fireOnCardOpened(CardModel lastCard, CardPart openedCard) {
        Invoke.onDispatch(() -> {
            for (StackNavigationObserver observer : stackNavigationObservers) {
                observer.onDisplayedCardChanged(lastCard, openedCard);
            }
        });
    }

    private void fireOnCardDimensionChanged(Dimension newDimension) {
        Invoke.onDispatch(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onStackDimensionChanged(newDimension);
            }
        });
    }

    private void fireOnStackNameChanged(String newName) {
        Invoke.onDispatch(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onStackNameChanged(newName);
            }
        });
    }

    private void fireOnCardOrderChanged() {
        Invoke.onDispatch(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onCardOrderChanged();
            }
        });
    }

    @Override
    public PartType getType() {
        return PartType.STACK;
    }

    @Override
    public StackModel getPartModel() {
        return getStackModel();
    }

    @Override
    public void partOpened(ExecutionContext context) {
        currentCard = loadCard(context, getStackModel().getCurrentCardIndex());
        getStackModel().receiveMessage(context.bindStack(this), SystemMessage.OPEN_STACK);

        fireOnCardOpened(null, getDisplayedCard());
        fireOnStackOpened();
    }

    @Override
    public void partClosed(ExecutionContext context) {
        closeCard(context, null);
        fireOnStackClosed();
    }
}
