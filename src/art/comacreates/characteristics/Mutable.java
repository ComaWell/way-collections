package art.comacreates.characteristics;

import java.util.function.*;

import art.comacreates.characteristics.Keyed.*;
import art.comacreates.characteristics.compound.*;

public interface Mutable {
	
	public static final Mutable INSTANCE = new Mutable() { };
	
	//--- Plus ---//
	
	public static <T, D extends Plus.Any<T> & Mutable> D plusAll(D input, T...values) {
		if (values == null)
			throw new NullPointerException();
		if (values.length == 0)
			return input;
		for (T value : values)
			input.plus(value);
		return input;
	}
	
	public static <T, D extends Plus.Any<T> & Mutable> D plusAll(D input, Get<? extends T> values) {
		if (values == null)
			throw new NullPointerException();
		for (T value : values)
			input.plus(value);
		return input;
	}
	
	//--- Plus.ByKey ---//
	
	public static <K, V, D extends Plus.ByKey<K, V> & Mutable> D plusToByKey(D input, Entry<? extends K, ? extends V> entry) {
		if (entry == null)
			throw new NullPointerException();
		input.plus(entry.key(), entry.value());
		return input;
	}
	
	public static <K, V, D extends Plus.ByKey<K, V> & Mutable> D plusAllByKey(D input, Entry<? extends K, ? extends V>...entries) {
		if (entries == null)
			throw new NullPointerException();
		if (entries.length == 0)
			return input;
		for (Entry<? extends K, ? extends V> entry : entries.clone())
			input.plus(entry.key(), entry.value());
		return input;
	}
	
	public static <K, V, D extends Plus.ByKey<K, V> & Mutable> D plusAllByKey(D input, Get<? extends Entry<K, V>> entries) {
		if (entries == null)
			throw new NullPointerException();
		for (Entry<K, V> entry : entries)
			input.plus(entry.key(), entry.value());
		return input;
	}
	
	//--- Plus.First ---//
	
	public static <T, D extends Plus.First<T> & Mutable> D plusAllFirst(D input, T...values) {
		if (values == null)
			throw new NullPointerException();
		if (values.length == 0)
			return input;
		for (T value : values.clone())
			input.plusFirst(value);
		return input;
	}
	
	public static <T, D extends Plus.First<T> & Mutable> D plusAllFirst(D input, Get<? extends T> values) {
		if (values == null)
			throw new NullPointerException();
		for (T value : values)
			input.plusFirst(value);
		return input;
	}
	
	//--- Plus.Last ---//
	
	public static <T, D extends Plus.Last<T> & Mutable> D plusAllLast(D input, T...values) {
		if (values == null)
			throw new NullPointerException();
		if (values.length == 0)
			return input;
		for (T value : values.clone())
			input.plusLast(value);
		return input;
	}
	
	public static <T, D extends Plus.Last<T> & Mutable> D plusAllLast(D input, Get<? extends T> values) {
		if (values == null)
			throw new NullPointerException();
		for (T value : values)
			input.plusLast(value);
		return input;
	}
	
	//--- Minus.ByKey ---//
	
	public static <K, V, D extends Minus.ByKey<K, V> & Mutable> D minus(D input, Entry<?, ? extends V> entry) {
		if (entry == null)
			throw new NullPointerException();
		input.minus(entry.key(), entry.value());
		return input;
	}
	
	public static <K, V, D extends Minus.ByKey<K, V> & Mutable> D minusAll(D input, Object...keys) {
		if (keys == null)
			throw new NullPointerException();
		for (Object key : keys.clone())
			input.minus(key);
		return input;
	}
	
	public static <K, V, D extends Minus.ByKey<K, V> & Mutable> D minusAll(D input, Entry<?, ? extends V>...entries) {
		if (entries == null)
			throw new NullPointerException();
		if (entries.length == 0)
			return input;
		for (Entry<?, ? extends V> entry : entries.clone())
			input.minus(entry);
		return input;
	}
	
	public static <K, V, D extends Minus.ByKey<K, V> & Mutable> D minusAll(D input, Get<Entry<?, ? extends V>> entries) {
		if (entries == null)
			throw new NullPointerException();
		for (Entry<?, ? extends V> entry : entries)
			input.minus(entry.key(), entry.value());
		return input;
	}
	
	//--- Minus.First ---//
	
	public static <T, D extends Minus.First<T> & Mutable> D minusAll(D input, long amount) {
		for (int i = 0; i < amount; i++)
			input.minusFirst();
		return input;
	}
	
	//--- Minus.Last ---//
	
	public static <T, D extends Minus.Last<T> & Mutable> D minusAll(D input, long amount) {
		for (int i = 0; i < amount; i++)
			input.minusLast();
		return input;
	}
	
	//--- Minus.ByValue ---//
	
	public static <T, D extends Minus.ByValue<T> & Mutable> D minusAll(D input, Object...values) {
		if (values == null)
			throw new NullPointerException();
		if (values.length == 0)
			return input;
		for (Object value : values.clone())
			input.minus(value);
		return input;
	}
	
	public static <T, D extends Minus.ByValue<T> & Mutable> D minusAll(D input, Get<?> values) {
		if (values == null)
			throw new NullPointerException();
		for (Object value : values)
			input.minus(value);
		return input;
	}
	
	//--- Pop.ByKey ---//
	
	public static <K, V, D extends Pop.ByKey<K, V> & Mutable> D popAll(D input, Consumer<? super V> consumer, K...keys) {
		if (consumer == null || keys == null)
			throw new NullPointerException();
		if (keys.length == 0)
			return input;
		for (K key : keys.clone()) {
			Pop.Result<V, ? extends Pop.ByKey<K, V>> pop = input.pop(key);
			consumer.accept(pop.popped());
		}
		return input;
	}
	
	public static <K, V, D extends Pop.ByKey<K, V> & Mutable> D popAll(D input, Consumer<? super V> consumer, Get<? extends K> keys) {
		if (consumer == null || keys == null)
			throw new NullPointerException();
		for (K key : keys) {
			Pop.Result<V, ? extends Pop.ByKey<K, V>> pop = input.pop(key);
			consumer.accept(pop.popped());
		}
		return input;
	}
	
	//--- Pop.First ---//
	
	public static <T, D extends Pop.First<T> & Mutable> D popAll(D input, Consumer<? super T> consumer, long amount) {
		if (consumer == null)
			throw new NullPointerException();
		for (int i = 0; i < amount; i++) {
			Pop.Result<T, ? extends Pop.First<T>> pop = input.popFirst();
			consumer.accept(pop.popped());
		}
		return input;
	}
	
	//--- Pop.Last ---//
	
	public static <T, D extends Pop.Last<T> & Mutable> D popAll(D input, Consumer<? super T> consumer, long amount) {
		if (consumer == null)
			throw new NullPointerException();
		for (int i = 0; i < amount; i++) {
			Pop.Result<T, ? extends Pop.Last<T>> pop = input.popLast();
			consumer.accept(pop.popped());
		}
		return input;
	}
	
	//--- Set.ByIndex ---//
	
	public static <T, D extends Put.ByLong<T> & Mutable> D setAll(D input, long offset, T...values) {
		if (values == null)
			throw new NullPointerException();
		if (values.length == 0)
			return input;
		long start = offset;
		for (T value : values.clone())
			input.put(start++, value);
		return input;
	}
	
	public static <T, D extends Put.ByLong<T> & Mutable> D setAll(D input, long offset, Get<? extends T> values) {
		if (values == null)
			throw new NullPointerException();
		long start = offset;
		for (T value : values)
			input.put(start++, value);
		return input;
	}
	
	//--- Set.ByKey ---//
	
	public static <K, V, D extends Put.ByKey<K, V> & Mutable> D setAll(D input, Entry<? extends K, V>...entries) {
		if (entries == null)
			throw new NullPointerException();
		if (entries.length == 0)
			return input;
		for (Entry<? extends K, ? extends V> entry : entries.clone())
			input.put(entry.key(), entry.value());
		return input;
	}
	
	public static <K, V, D extends Put.ByKey<K, V> & Mutable> D setAll(D input, Get<? extends Entry<K, V>> entries) {
		if (entries == null)
			throw new NullPointerException();
		for (Entry<K, V> entry : entries)
			input.put(entry.key(), entry.value());
		return input;
	}
	
	//--- Swap.ByIndex ---//
	
	public static <T, D extends Swap.ByLong<T> & Mutable> D swapIf(D input, Predicate<? super T> condition, long index, T value) {
		if (condition == null)
			throw new NullPointerException();
		T existing = input.get(index);
		if (condition.test(existing))
			input.swap(index, value);
		return input;
	}
	
	//--- Swap.ByKey ---//
	
	public static <K, V, D extends Swap.ByKey<K, V> & Mutable> D swapIf(D input, Predicate<? super V> condition, K key, V value) {
		if (condition == null)
			throw new NullPointerException();
		V existing = input.getOrNull(key);
		if (condition.test(existing))
			input.swap(key, value);
		return input;
	}

}
