package com.defano.wyldcard.parts.stack;

import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.wyldcard.icons.ButtonIcon;
import com.defano.wyldcard.icons.UserIcon;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.finder.StackPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.LimitedDepthStack;
import com.defano.wyldcard.window.WindowManager;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class StackModel extends PartModel implements StackPartFinder {

    private static final int BACKSTACK_DEPTH = 20;
    public final static String FILE_EXTENSION = ".stack";

    public final static String PROP_CANTPEEK = "cantpeek";
    public static final String PROP_RESIZABLE = "resizable";
    public static final String PROP_SHORTNAME = "short name";
    public static final String PROP_ABBREVNAME = "abbreviated name";
    public static final String PROP_LONGNAME = "long name";

    // Model properties that are not HyperTalk-addressable
    private int nextPartId = 0;
    private int nextCardId = 0;
    private int nextBackgroundId = 0;
    private int currentCardIndex = 0;
    private LimitedDepthStack<Integer> backStack = new LimitedDepthStack<>(BACKSTACK_DEPTH);
    private List<CardModel> cardModels;
    private final Map<Integer, BackgroundModel> backgroundModels;
    private final Map<String, BufferedImage> userIcons;

    // The location where this stack was saved to, or opened from, on disk. Null if the stack has not been saved.
    private transient Subject<Optional<File>> savedStackFileProvider;

    private StackModel(String stackName, Dimension dimension) {
        super(PartType.STACK, Owner.HYPERCARD, null);

        this.cardModels = new ArrayList<>();
        this.backgroundModels = new HashMap<>();
        this.userIcons = new HashMap<>();

        defineProperty(PROP_NAME, new Value(stackName), false);
        defineProperty(PROP_WIDTH, new Value(dimension.width), false);
        defineProperty(PROP_HEIGHT, new Value(dimension.height), false);
        defineProperty(PROP_RESIZABLE, new Value(false), false);
        defineProperty(PROP_CANTPEEK, new Value(false), false);

        initialize();
    }

    @PostConstruct
    public void postConstruct() {
        initialize();
        relinkParentPartModel(null);
    }

    @Override
    public void initialize() {
        super.initialize();

        this.savedStackFileProvider = BehaviorSubject.createDefault(Optional.empty());

        defineComputedReadOnlyProperty(PROP_LONGNAME, (context, model, propertyName) -> new Value(getLongName(context)));
        defineComputedReadOnlyProperty(PROP_ABBREVNAME, (context, model, propertyName) -> new Value(getAbbrevName(context)));
        defineComputedReadOnlyProperty(PROP_SHORTNAME, (context, model, propertyName) -> new Value(getShortName(context)));

        defineComputedGetterProperty(PartModel.PROP_LEFT, (context, model, propertyName) -> new Value(WindowManager.getInstance().getStackWindow(context).getWindow().getLocation().x));
        defineComputedSetterProperty(PartModel.PROP_LEFT, (context, model, propertyName, value) -> WindowManager.getInstance().getStackWindow(context).getWindow().setLocation(value.integerValue(), WindowManager.getInstance().getStackWindow(context).getWindow().getY()));
        defineComputedGetterProperty(PartModel.PROP_TOP, (context, model, propertyName) -> new Value(WindowManager.getInstance().getStackWindow(context).getWindow().getLocation().y));
        defineComputedSetterProperty(PartModel.PROP_TOP, (context, model, propertyName, value) -> WindowManager.getInstance().getStackWindow(context).getWindow().setLocation(WindowManager.getInstance().getStackWindow(context).getWindow().getX(), value.integerValue()));

        if (!hasProperty(PartModel.PROP_ID)) {
            defineProperty(PartModel.PROP_ID, new Value(UUID.randomUUID().toString()), true);
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

    public static StackModel newStackModel(String stackName) {
        StackModel stack = new StackModel(stackName, new Dimension(640, 480));
        stack.cardModels.add(CardModel.emptyCardModel(stack.getNextCardId(), stack.newBackgroundModel(), stack));
        return stack;
    }

    public int insertCard(CardModel cardModel) {
        cardModels.add(currentCardIndex + 1, cardModel);
        receiveMessage(new ExecutionContext(), SystemMessage.NEW_CARD.messageName);
        return currentCardIndex + 1;
    }

    public void newCard(int backgroundId) {
        insertCard(CardModel.emptyCardModel(getNextCardId(), backgroundId, this));
    }

    public void newCardWithNewBackground() {
        insertCard(CardModel.emptyCardModel(getNextCardId(), newBackgroundModel(), this));
    }

    private int newBackgroundModel() {
        int newBackgroundId = getNextBackgroundId();
        backgroundModels.put(newBackgroundId, BackgroundModel.emptyBackground(newBackgroundId, this));
        return newBackgroundId;
    }

    public void deleteCardModel() {
        cardModels.remove(currentCardIndex);
        receiveMessage(new ExecutionContext(), SystemMessage.DELETE_CARD.messageName);
    }

    public String getStackName(ExecutionContext context) {
        return getKnownProperty(context, PROP_NAME).stringValue();
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

    public int getCardCount() {
        return cardModels.size();
    }

    public int getCurrentCardIndex() {
        return currentCardIndex;
    }

    public CardModel getCurrentCard() {
        return getCardModel(getCurrentCardIndex());
    }

    public void setCurrentCardIndex(int currentCard) {
        this.currentCardIndex = currentCard;
    }

    public int getIndexOfCard(CardModel card) {
        return cardModels.indexOf(card);
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

    public LimitedDepthStack<Integer> getBackStack() {
        return backStack;
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

    /** {@inheritDoc}
     * @param context*/
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

    /** {@inheritDoc} */
    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return propertyName.equalsIgnoreCase(PROP_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public Adjective getDefaultAdjectiveForProperty(String propertyName) {
        if (propertyName.equalsIgnoreCase(PROP_NAME)) {
            return Adjective.ABBREVIATED;
        } else {
            return Adjective.DEFAULT;
        }
    }

    public String getShortName(ExecutionContext context) {
        return getKnownProperty(context, PROP_NAME).stringValue();
    }

    public String getAbbrevName(ExecutionContext context) {
        return "stack \"" + getShortName(context) + "\"";
    }

    public String getLongName(ExecutionContext context) {
        return savedStackFileProvider.blockingFirst()
                .map(file -> "stack \"" + file.getAbsolutePath() + "\"")
                .orElseGet(() -> getAbbrevName(context));
    }

    public String getStackPath(ExecutionContext context) {
        return savedStackFileProvider.blockingFirst()
                .map(file -> file.getAbsolutePath())
                .orElseGet(() -> getShortName(context));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackModel that = (StackModel) o;
        return this.getKnownProperty(new ExecutionContext(), PartModel.PROP_ID).equals(that.getKnownProperty(new ExecutionContext(), PartModel.PROP_ID));
    }
}

