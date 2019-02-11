package com.defano.hypertalk;

import com.defano.hypertalk.ast.model.Value;
import com.google.common.collect.Lists;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public interface TestDataGenerator {

    default List<Integer> nonNegativeIntValues() {
        return Lists.newArrayList(0, 1, 2, 10, 100, 1000);
    }

    default <Type> List<Type> listOfMocks(Class<Type> clazz, int size) {
        ArrayList<Type> list = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            list.add(Mockito.mock(clazz));
        }
        return list;
    }

    default Value valueOfLines(int size) {
        return valueOfItems(size, "\n");
    }

    default Value valueOfWords(int size) {
        return valueOfItems(size, " ");
    }

    default Value valueOfItems(int size) {
        return valueOfItems(size, ",");
    }

    default Value valueOfItems(int size, String itemDelimiter) {
        StringBuilder builder = new StringBuilder();
        for (int index = 1; index <= size; index++) {
            builder.append(index);
            if (index < size) {
                builder.append(itemDelimiter);
            }
        }

        return new Value(builder.toString());
    }

    default Value valueOfChars(int size) {
        StringBuilder builder = new StringBuilder();
        for (int index = 1; index <= size; index++) {
            builder.append(index % 9);
        }

        return new Value(builder.toString());
    }

}
