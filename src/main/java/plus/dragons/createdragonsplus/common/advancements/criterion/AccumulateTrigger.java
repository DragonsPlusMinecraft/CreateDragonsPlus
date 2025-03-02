package plus.dragons.createdragonsplus.common.advancements.criterion;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import it.unimi.dsi.fastutil.doubles.DoubleBinaryOperator;
import it.unimi.dsi.fastutil.doubles.DoublePredicate;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import it.unimi.dsi.fastutil.floats.FloatPredicate;
import it.unimi.dsi.fastutil.ints.IntBinaryOperator;
import it.unimi.dsi.fastutil.ints.IntPredicate;
import net.minecraft.server.level.ServerPlayer;
import plus.dragons.createdragonsplus.common.advancements.CriterionTriggerBehaviour;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public abstract class AccumulateTrigger<T> extends BlockEntityBehaviourTrigger<T> implements Predicate<T>, BinaryOperator<T> {

    protected AccumulateTrigger(Codec<T> dataCodec) {
        super(dataCodec);
    }
    
    public static <T> AccumulateTrigger<T> of(Codec<T> codec, Predicate<T> predicate, BinaryOperator<T> accumulator) {
        return new AccumulateTrigger<T>(codec) {
            @Override
            public T apply(T a, T b) {
                return accumulator.apply(a, b);
            }

            @Override
            public boolean test(T value) {
                return predicate.test(value);
            }
        };
    }

    public static AccumulateTrigger<Integer> of(IntPredicate predicate, IntBinaryOperator accumulator) {
        return of(Codec.INT, predicate, accumulator);
    }

    public static AccumulateTrigger<Float> of(FloatPredicate predicate, FloatBinaryOperator accumulator) {
        return of(Codec.FLOAT, predicate, accumulator);
    }

    public static AccumulateTrigger<Double> of(DoublePredicate predicate, DoubleBinaryOperator accumulator) {
        return of(Codec.DOUBLE, predicate, accumulator);
    }

    public static AccumulateTrigger<Integer> increment(int limit) {
        return new AccumulateTrigger<>(Codec.INT) {
            @Override
            public Integer apply(Integer a, Integer b) {
                return a + b;
            }

            @Override
            public boolean test(Integer value) {
                return value >= limit;
            }
        };
    }

    public static AccumulateTrigger<Integer> decrement(int limit) {
        return new AccumulateTrigger<>(Codec.INT) {
            @Override
            public Integer apply(Integer a, Integer b) {
                return a - b;
            }

            @Override
            public boolean test(Integer value) {
                return value <= limit;
            }
        };
    }

    public static AccumulateTrigger<Float> increment(float limit) {
        return new AccumulateTrigger<>(Codec.FLOAT) {
            @Override
            public Float apply(Float a, Float b) {
                return a + b;
            }

            @Override
            public boolean test(Float value) {
                return value >= limit;
            }
        };
    }

    public static AccumulateTrigger<Float> decrement(float limit) {
        return new AccumulateTrigger<>(Codec.FLOAT) {
            @Override
            public Float apply(Float a, Float b) {
                return a - b;
            }

            @Override
            public boolean test(Float value) {
                return value <= limit;
            }
        };
    }

    public static AccumulateTrigger<Double> increment(double limit) {
        return new AccumulateTrigger<>(Codec.DOUBLE) {
            @Override
            public Double apply(Double a, Double b) {
                return a + b;
            }

            @Override
            public boolean test(Double value) {
                return value >= limit;
            }
        };
    }

    public static AccumulateTrigger<Double> decrement(double limit) {
        return new AccumulateTrigger<>(Codec.DOUBLE) {
            @Override
            public Double apply(Double a, Double b) {
                return a - b;
            }

            @Override
            public boolean test(Double value) {
                return value <= limit;
            }
        };
    }

    public void accumulate(SmartBlockEntity blockEntity, T data) {
        var behaviour = blockEntity.getBehaviour(CriterionTriggerBehaviour.TYPE);
        if (behaviour == null)
            return;
        data = this.apply(behaviour.getData(this), data);
        behaviour.setData(this, data);
        if (this.test(data)) {
            this.trigger(blockEntity);
        }
    }

    @Override
    protected final boolean test(ServerPlayer player, SmartBlockEntity blockEntity, T data) {
        return this.test(data);
    }
}
