package com.defano.wyldcard.part.stack;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.enums.LengthAdjective;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.model.specifier.StackPartSpecifier;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.icon.ButtonIcon;
import com.defano.wyldcard.icon.UserIcon;
import com.defano.wyldcard.part.NamedPart;
import com.defano.wyldcard.part.bkgnd.BackgroundModel;
import com.defano.wyldcard.part.builder.BackgroundModelBuilder;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.finder.StackPartFinder;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.pattern.WyldCardPatternFactory;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.serializer.Serializer;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class StackModel extends PartModel implements StackPartFinder, NamedPart {

    public static final String FILE_EXTENSION = ".stack";
    public static final String PROP_CANTPEEK = "cantpeek";
    public static final String PROP_CANTABORT = "cantabort";
    public static final String PROP_RESIZABLE = "resizable";
    public static final String PROP_SHORTNAME = "short name";
    public static final String PROP_ABBREVNAME = "abbreviated name";
    public static final String PROP_LONGNAME = "long name";

    private Value windowPosition;
    private Map<Integer, BackgroundModel> backgroundModels = new HashMap<>();
    private Map<String, BufferedImage> userIcons;
    private Map<Integer, BufferedImage> userPatterns;

    // Model properties that are not HyperTalk-addressable
    private int currentCardIndex = 0;
    private List<CardModel> cardModels = new ArrayList<>();

    // The location where this stack was saved to, or opened from, on disk. Null if the stack has not been saved.
    private transient Subject<Optional<File>> savedStackFileProvider;
    private transient int nextPartId = new Random().nextInt();
    private transient boolean isBeingClosed = false;

    public StackModel() {
        super(PartType.STACK, Owner.HYPERCARD, null);

        define(PROP_NAME).asValue("Untitled");
        define(PROP_WIDTH).asValue(640);
        define(PROP_HEIGHT).asValue(480);
        define(PROP_RESIZABLE).asValue(false);
        define(PROP_CANTPEEK).asValue(false);
        define(PROP_CANTABORT).asValue(false);      // TODO: Not implemented

        postConstructStackModel();
    }

    @PostConstruct
    @SuppressWarnings("unused")
    public void postConstruct() {
        postConstructStackModel();
        relinkParentPartModel(null);
    }

    public void postConstructStackModel() {
        super.postConstructPartModel();

        savedStackFileProvider = BehaviorSubject.createDefault(Optional.empty());

        // User patterns may be missing from serialized object form; rehydrate empty map in this case
        if (userPatterns == null) {
            userPatterns = new HashMap<>();
        }

        // User icons may be missing from serialized object form; rehydrate empty map in this case
        if (userIcons == null) {
            userIcons = new HashMap<>();
        }

        define(PROP_LONGNAME).asComputedReadOnlyValue((context, model) -> new Value(getLongName(context)));
        define(PROP_ABBREVNAME).asComputedReadOnlyValue((context, model) -> new Value(getAbbreviatedName(context)));
        define(PROP_SHORTNAME).asComputedReadOnlyValue((context, model) -> new Value(getShortName(context)));

        define(PROP_LEFT).asComputedValue()
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getLocation().x))
                .withSetter((context, model, value) -> WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().setLocation(value.integerValue(), WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getY()));

        define(PROP_TOP).asComputedValue()
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getLocation().y))
                .withSetter((context, model, value) -> WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().setLocation(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getX(), value.integerValue()));

        if (!hasProperty(PROP_ID)) {
            define(PROP_ID).asConstant(getNextId(nextPartId));
        }
    }

    @Override
    public Value getValue(ExecutionContext context) {
        return new Value();
    }

    @Override
    public PartSpecifier getMe(ExecutionContext context) {
        return new StackPartSpecifier(getLongName(context));
    }

    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        this.setParentPartModel(parentPartModel);
        cardModels.forEach(model -> model.relinkParentPartModel(this));
        backgroundModels.values().forEach(model -> model.relinkParentPartModel(this));
    }

    public Point getWindowPosition() {
        return windowPosition == null || !windowPosition.isPoint() ? null : windowPosition.pointValue();
    }

    public void setWindowPosition(Value windowPosition) {
        this.windowPosition = windowPosition;
    }

    public Observable<Optional<File>> getSavedStackFileProvider() {
        return savedStackFileProvider;
    }

    public void setSavedStackFile(ExecutionContext context, File file) {
        this.savedStackFileProvider.onNext(Optional.of(file));

        String filename = file.getName();
        if (filename.endsWith(FILE_EXTENSION)) {
            filename = filename.substring(0, filename.length() - FILE_EXTENSION.length());
        }

        set(context, PROP_NAME, new Value(filename));
    }

    public int getNextButtonId(int parentPartId) {
        return getNextId(parentPartId);
    }

    public int getNextFieldId(int parentPartId) {
        return getNextId(parentPartId);
    }

    public int getNextCardId() {
        return getNextId(getId());
    }

    public int getNextBackgroundId() {
        return getNextId(getId());
    }

    private int getNextId(int seed) {
        int hash = String.valueOf(seed * nextPartId).hashCode();
        int high = (hash & 0xffff0000) >> 16;
        int low = hash & 0xffff;
        nextPartId = (high ^ low) & 0xffff;
        return nextPartId;
    }

    public void addCard(CardModel cardModel) {
        cardModels.add(cardModel);
    }

    public void addCard(CardModel cardModel, int atIndex) {
        cardModels.add(atIndex, cardModel);
    }

    public void addBackground(BackgroundModel backgroundModel) {
        backgroundModels.put(backgroundModel.getId(), backgroundModel);
    }

    public int newBackground() {
        int newBackgroundId = getNextBackgroundId();
        backgroundModels.put(newBackgroundId, new BackgroundModelBuilder(this).withId(newBackgroundId).build());
        return newBackgroundId;
    }

    public void deleteCurrentCard() {
        cardModels.remove(currentCardIndex);
    }

    public String getStackName(ExecutionContext context) {
        return get(context, PROP_NAME).toString();
    }

    public void setStackName(ExecutionContext context, String name) {
        set(context, PROP_NAME, new Value(name));
    }

    public List<CardModel> getCardModels() {
        return new ArrayList<>(cardModels);
    }

    public void setCardModels(List<CardModel> cardModels) {
        this.cardModels = cardModels;
    }

    public CardModel getCardModel(int index) {
        return cardModels.get(index);
    }

    public boolean hasCard(CardModel cardModel) {
        return cardModels.contains(cardModel);
    }

    public int getCardCount() {
        return cardModels.size();
    }

    public int getCurrentCardIndex() {
        return currentCardIndex;
    }

    public void setCurrentCardIndex(int currentCard) {
        this.currentCardIndex = currentCard;
    }

    public CardModel getCurrentCard() {
        return getCardModel(getCurrentCardIndex());
    }

    public int getIndexOfCard(CardModel card) {
        return cardModels.indexOf(card);
    }

    public Integer getIndexOfCardId(int cardId) {
        return cardModels.stream()
                .filter(c -> c.getId() == cardId)
                .map(c -> cardModels.indexOf(c))
                .findFirst()
                .orElse(null);
    }

    public int getIndexOfBackground(int backgroundId) {
        Optional<CardModel> card = cardModels.stream()
                .filter(c -> c.getBackgroundId() == backgroundId)
                .findFirst();

        if (card.isPresent()) {
            return getIndexOfCard(card.get());
        } else {
            throw new IllegalArgumentException("No such background.");
        }
    }

    public boolean isResizable(ExecutionContext context) {
        return get(context, PROP_RESIZABLE).booleanValue();
    }

    public void setResizable(ExecutionContext context, boolean resizable) {
        set(context, PROP_RESIZABLE, new Value(resizable));
    }

    public Dimension getSize(ExecutionContext context) {
        return new Dimension(getWidth(context), getHeight(context));
    }

    public int getWidth(ExecutionContext context) {
        return get(context, PROP_WIDTH).integerValue();
    }

    public int getHeight(ExecutionContext context) {
        return get(context, PROP_HEIGHT).integerValue();
    }

    public Dimension getDimension(ExecutionContext context) {
        return new Dimension(getWidth(context), getHeight(context));
    }

    public void setDimension(ExecutionContext context, Dimension dimension) {
        set(context, PROP_WIDTH, new Value(dimension.width));
        set(context, PROP_HEIGHT, new Value(dimension.height));
    }

    public BackgroundModel getBackground(int backgroundId) {
        return backgroundModels.get(backgroundId);
    }

    public int getBackgroundCount() {
        return backgroundModels.size();
    }

    public List<CardModel> getMarkedCards(ExecutionContext context) {
        return getCardModels().stream()
                .filter(c -> c.get(context, CardModel.PROP_MARKED).booleanValue())
                .collect(Collectors.toList());
    }

    public List<CardModel> getCardsInBackground(int backgroundId) {
        return getCardModels().stream()
                .filter(c -> c.getBackgroundId() == backgroundId)
                .collect(Collectors.toList());
    }

    @Override
    public StackModel getStackModel() {
        return this;
    }

    @Override
    public PartSpecifier getPartSpecifier(ExecutionContext context) {
        return new StackPartSpecifier(getLongName(context));
    }

    public void createIcon(String name, BufferedImage image) {
        userIcons.put(name, image);
    }

    public List<ButtonIcon> getUserIcons() {
        ArrayList<ButtonIcon> icons = new ArrayList<>();
        for (String thisIconName : userIcons.keySet()) {
            icons.add(new UserIcon(thisIconName, userIcons.get(thisIconName)));
        }

        return icons;
    }

    public BufferedImage getUserPattern(int patternId) {
        return userPatterns.get(patternId);
    }

    public void setUserPattern(int patternId, BufferedImage pattern) {
        userPatterns.put(patternId, pattern);
        WyldCardPatternFactory.getInstance().invalidatePatternCache();
    }

    /**
     * Flag this stack as in the process of being closed. This is a latched behavior; once set the flag cannot be
     * cleared.
     */
    public void setBeingClosed() {
        isBeingClosed = true;
    }

    /**
     * Determines if this stack has been marked as being closed (that is, WyldCard is in the process of sending final
     * messages to the stack and disposing its resources).
     *
     * @return True if the stack is being closed, false otherwise.
     */
    public boolean isBeingClosed() {
        return isBeingClosed;
    }

    /**
     * A cheesy and expensive mechanism to determine if the user has made a change to the stack since it was last opened.
     *
     * @return True if the stack has changes; false otherwise
     */
    public boolean isDirty() {
        Optional<File> stackFile = savedStackFileProvider.blockingFirst();

        if (stackFile.isPresent()) {
            try {
                String savedStack = new String(Files.readAllBytes(stackFile.get().toPath()), StandardCharsets.UTF_8);
                String currentStack = Serializer.serialize(this);

                return !isEmpty() && !savedStack.equalsIgnoreCase(currentStack);
            } catch (Exception e) {
                return true;
            }
        }

        return !isEmpty();
    }

    /**
     * Determines if this stack is empty. That is, whether the stack is essentially a new stack with no parts, cards, or
     * graphics added.
     *
     * @return True if the stack is empty, false otherwise
     */
    public boolean isEmpty() {
        BackgroundModel background = getBackground(getCurrentCard().getBackgroundId());
        CardModel card = getCurrentCard();

        return getCardCount() == 1 &&                                   // Single card
                card.getFieldModels().isEmpty() &&                      // No card fields
                card.getButtonModels().isEmpty() &&                     // No card buttons
                background.getFieldModels().isEmpty() &&                // No bkgnd fields
                background.getButtonModels().isEmpty() &&               // No bkgnd buttons
                !card.hasCardImage() &&                                 // No card graphics
                !background.hasBackgroundImage() &&                     // No bkgnd graphics
                card.getScriptText(null).isEmpty() &&           // No card script
                background.getScriptText(null).isEmpty() &&     // No bkgnd script
                getScriptText(null).isEmpty();                  // No stack script
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PartModel> getPartsInDisplayOrder(ExecutionContext context) {
        ArrayList<PartModel> parts = new ArrayList<>();

        for (CardModel thisCard : getCardModels()) {
            parts.add(thisCard);

            BackgroundModel thisBackground = getBackground(thisCard.getBackgroundId());
            if (!parts.contains(thisBackground)) {
                parts.add(thisBackground);
            }
        }

        return parts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return propertyName.equalsIgnoreCase(PROP_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LengthAdjective getDefaultAdjectiveForProperty(String propertyName) {
        if (propertyName.equalsIgnoreCase(PROP_NAME)) {
            return LengthAdjective.ABBREVIATED;
        } else {
            return LengthAdjective.DEFAULT;
        }
    }

    @Override
    public String getShortName(ExecutionContext context) {
        return get(context, PROP_NAME).toString();
    }

    @Override
    public String getAbbreviatedName(ExecutionContext context) {
        return "stack \"" + getShortName(context) + "\"";
    }

    @Override
    public String getLongName(ExecutionContext context) {
        return savedStackFileProvider.blockingFirst()
                .map(file -> "stack \"" + file.getAbsolutePath() + "\"")
                .orElseGet(() -> getAbbreviatedName(context));
    }

    public String getStackPath(ExecutionContext context) {
        return savedStackFileProvider.blockingFirst()
                .map(File::getAbsolutePath)
                .orElseGet(() -> getShortName(context));
    }
}

