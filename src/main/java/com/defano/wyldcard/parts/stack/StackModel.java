package com.defano.wyldcard.parts.stack;

import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.icons.ButtonIcon;
import com.defano.wyldcard.icons.UserIcon;
import com.defano.wyldcard.parts.NamedPart;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.builder.BackgroundModelBuilder;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.finder.StackPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.patterns.WyldCardPatternFactory;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.serializer.Serializer;
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

    public final static String FILE_EXTENSION = ".stack";
    public final static String PROP_CANTPEEK = "cantpeek";
    public final static String PROP_CANTABORT = "cantabort";
    public static final String PROP_RESIZABLE = "resizable";
    public static final String PROP_SHORTNAME = "short name";
    public static final String PROP_ABBREVNAME = "abbreviated name";
    public static final String PROP_LONGNAME = "long name";

    private Map<Integer, BackgroundModel> backgroundModels = new HashMap<>();
    private Map<String, BufferedImage> userIcons = new HashMap<>();
    private Map<Integer, BufferedImage> userPatterns = new HashMap<>();

    // Model properties that are not HyperTalk-addressable
    private int nextPartId = 0;
    private int nextCardId = 0;
    private int nextBackgroundId = 0;
    private int currentCardIndex = 0;
    private List<CardModel> cardModels = new ArrayList<>();

    // The location where this stack was saved to, or opened from, on disk. Null if the stack has not been saved.
    private transient Subject<Optional<File>> savedStackFileProvider;

    public StackModel() {
        super(PartType.STACK, Owner.HYPERCARD, null);

        newProperty(PROP_NAME, new Value("Untitled"), false);
        newProperty(PROP_WIDTH, new Value(640), false);
        newProperty(PROP_HEIGHT, new Value(480), false);
        newProperty(PROP_RESIZABLE, new Value(false), false);
        newProperty(PROP_CANTPEEK, new Value(false), false);
        newProperty(PROP_CANTABORT, new Value(false), false);       // TODO: Not implemented

        initialize();
    }

    @PostConstruct
    @SuppressWarnings("unused")
    public void postConstruct() {
        initialize();
        relinkParentPartModel(null);
    }

    @Override
    public void initialize() {
        super.initialize();

        savedStackFileProvider = BehaviorSubject.createDefault(Optional.empty());

        // User patterns may be missing from serialized object form; rehydrate empty map in this case
        if (userPatterns == null) {
            userPatterns = new HashMap<>();
        }

        // User icons may be missing from serialized object form; rehydrate empty map in this case
        if (userIcons == null) {
            userIcons = new HashMap<>();
        }

        newComputedReadOnlyProperty(PROP_LONGNAME, (context, model) -> new Value(getLongName(context)));
        newComputedReadOnlyProperty(PROP_ABBREVNAME, (context, model) -> new Value(getAbbreviatedName(context)));
        newComputedReadOnlyProperty(PROP_SHORTNAME, (context, model) -> new Value(getShortName(context)));

        newComputedGetterProperty(PartModel.PROP_LEFT, (context, model) -> new Value(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getLocation().x));
        newComputedSetterProperty(PartModel.PROP_LEFT, (context, model, value) -> WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().setLocation(value.integerValue(), WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getY()));
        newComputedGetterProperty(PartModel.PROP_TOP, (context, model) -> new Value(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getLocation().y));
        newComputedSetterProperty(PartModel.PROP_TOP, (context, model, value) -> WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().setLocation(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getX(), value.integerValue()));

        if (!hasProperty(PartModel.PROP_ID)) {
            newProperty(PartModel.PROP_ID, new Value(UUID.randomUUID().toString().hashCode()), true);
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

        for (CardModel thisCard : cardModels) {
            thisCard.relinkParentPartModel(this);
        }

        for (BackgroundModel thisBkgnd : backgroundModels.values()) {
            thisBkgnd.relinkParentPartModel(this);
        }
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

        setKnownProperty(context, PROP_NAME, new Value(filename));
    }

    public void addCard(CardModel cardModel) {
        cardModels.add(cardModel);
    }

    public void addCard(CardModel cardModel, int atIndex) {
        cardModels.add(atIndex, cardModel);
    }

    public void addBackground(BackgroundModel backgroundModel) {
        backgroundModels.put(backgroundModel.getId(new ExecutionContext()), backgroundModel);
    }

    public int newBackgroundModel() {
        int newBackgroundId = getNextBackgroundId();
        backgroundModels.put(newBackgroundId, new BackgroundModelBuilder(this).withId(newBackgroundId).build());
        return newBackgroundId;
    }

    public void deleteCurrentCard() {
        cardModels.remove(currentCardIndex);
    }

    public String getStackName(ExecutionContext context) {
        return getKnownProperty(context, PROP_NAME).toString();
    }

    public void setStackName(ExecutionContext context, String name) {
        setKnownProperty(context, PROP_NAME, new Value(name));
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
                .filter(c -> c.getId(null) == cardId)
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
        return getKnownProperty(context, PROP_RESIZABLE).booleanValue();
    }

    public void setResizable(ExecutionContext context, boolean resizable) {
        setKnownProperty(context, PROP_RESIZABLE, new Value(resizable));
    }

    public Dimension getSize(ExecutionContext context) {
        return new Dimension(getWidth(context), getHeight(context));
    }

    public int getWidth(ExecutionContext context) {
        return getKnownProperty(context, PROP_WIDTH).integerValue();
    }

    public int getHeight(ExecutionContext context) {
        return getKnownProperty(context, PROP_HEIGHT).integerValue();
    }

    public Dimension getDimension(ExecutionContext context) {
        return new Dimension(getWidth(context), getHeight(context));
    }

    public void setDimension(ExecutionContext context, Dimension dimension) {
        setKnownProperty(context, PROP_WIDTH, new Value(dimension.width));
        setKnownProperty(context, PROP_HEIGHT, new Value(dimension.height));
    }

    public BackgroundModel getBackground(int backgroundId) {
        return backgroundModels.get(backgroundId);
    }

    public int getNextButtonId() {
        return nextPartId++;
    }

    public int getNextFieldId() {
        return nextPartId++;
    }

    public int getNextCardId() {
        return nextCardId++;
    }

    public int getNextBackgroundId() {
        return nextBackgroundId++;
    }

    public int getBackgroundCount() {
        return backgroundModels.size();
    }

    public List<CardModel> getMarkedCards(ExecutionContext context) {
        return getCardModels().stream()
                .filter(c -> c.getKnownProperty(context, CardModel.PROP_MARKED).booleanValue())
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
     * A cheesy and expensive mechanism to determine if the user has made a change to the stack since it was last opened.
     *
     * @return True if the stack has changes; false otherwise
     */
    public boolean isDirty() {
        if (savedStackFileProvider.blockingFirst().isPresent()) {

            try {
                String savedStack = new String(Files.readAllBytes(savedStackFileProvider.blockingFirst().get().toPath()), StandardCharsets.UTF_8);
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
                card.getFieldModels().size() == 0 &&                    // No card fields
                card.getButtonModels().size() == 0 &&                   // No card buttons
                background.getFieldModels().size() == 0 &&              // No bkgnd fields
                background.getButtonModels().size() == 0 &&             // No bkgnd buttons
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
        return getKnownProperty(context, PROP_NAME).toString();
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

