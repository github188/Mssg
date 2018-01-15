package com.fable.framework.proxy.sip.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * .
 *
 * @author stormning 2017/11/16
 * @since 1.3.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappedProp<T> implements Serializable {

    private T input;

    private T output;

    public static <O> MappedProp<O> newProp(O input, O output) {
        return new MappedProp<>(input, output);
    }

    public static <O> MappedProp<O> input(O input) {
        return newProp(input, null);
    }

    public static <O> MappedProp<O> output(O output) {
        return newProp(null, output);
    }

    public static <O> MappedProp<O> same(O input) {
        return newProp(input, input);
    }

    public MappedProp<T> reverse() {
        return newProp(output, input);
    }
}
